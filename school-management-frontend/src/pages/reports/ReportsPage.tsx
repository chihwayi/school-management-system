import React, { useState, useEffect } from 'react';
import { useAuth, useRoleCheck } from '../../hooks/useAuth';
import { reportService, type StudentReport } from '../../services/reportService';
import { teacherService } from '../../services/teacherService';
import { Card, Button, Select, Table, Modal, Input, Badge } from '../../components/ui';
import ReportingGuide from '../../components/reports/ReportingGuide';
import { MessageSquare, CheckCircle, FileText, Users } from 'lucide-react';
import { toast } from 'react-hot-toast';

const ReportsPage: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const { isClassTeacher, isClerk, canAddSubjectComments, canAddOverallComments } = useRoleCheck();
  
  // Helper function to check if teacher can comment on a specific subject
  const canCommentOnSubject = (subjectId: number) => {
    return teacherAssignments.some(assignment => assignment.subjectId === subjectId);
  };
  
  const [reports, setReports] = useState<StudentReport[]>([]);
  const [teacherAssignments, setTeacherAssignments] = useState<any[]>([]);
  const [supervisedClasses, setSupervisedClasses] = useState<any[]>([]);
  const [selectedClass, setSelectedClass] = useState<string>('');
  const [selectedSubject, setSelectedSubject] = useState<string>('');
  const [selectedTerm, setSelectedTerm] = useState<string>('Term 1');
  const [selectedYear, setSelectedYear] = useState<string>(new Date().getFullYear().toString());
  const [loading, setLoading] = useState(false);
  
  // Modal states
  const [isCommentModalOpen, setIsCommentModalOpen] = useState(false);
  const [isOverallModalOpen, setIsOverallModalOpen] = useState(false);
  const [selectedReport, setSelectedReport] = useState<StudentReport | null>(null);
  const [selectedSubjectReport, setSelectedSubjectReport] = useState<any>(null);
  const [comment, setComment] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('all');
  const [sortBy, setSortBy] = useState('name');

  useEffect(() => {
    if (isAuthenticated) {
      loadTeacherData();
    }
  }, [isAuthenticated]);

  // Auto-load reports when selections change
  useEffect(() => {
    if (teacherAssignments.length > 0 && (selectedClass || selectedSubject) && selectedTerm && selectedYear) {
      loadReports();
    }
  }, [selectedClass, selectedSubject, selectedTerm, selectedYear, teacherAssignments]);

  // Set default class for class teachers if they have supervised classes with students
  useEffect(() => {
    if (isClassTeacher() && supervisedClasses.length > 0 && !selectedClass && !selectedSubject) {
      // Auto-select the first supervised class that has students
      const classWithStudents = supervisedClasses.find(cls => (cls.studentCount || 0) > 0);
      if (classWithStudents) {
        setSelectedClass(`${classWithStudents.form}-${classWithStudents.section}`);
      }
    }
  }, [isClassTeacher, supervisedClasses, selectedClass, selectedSubject]);

  // Helper function to check if all teaching subjects are in the same class
  const areAllSubjectsInSameClass = () => {
    if (teacherAssignments.length <= 1) return false;
    const firstAssignment = teacherAssignments[0];
    return teacherAssignments.every(assignment => 
      assignment.form === firstAssignment.form && assignment.section === firstAssignment.section
    );
  };

  // Helper function to get the common class for all subjects
  const getCommonClass = () => {
    if (!areAllSubjectsInSameClass()) return null;
    const firstAssignment = teacherAssignments[0];
    return `${firstAssignment.form}-${firstAssignment.section}`;
  };

  // Helper function to check if teacher supervises a specific student's class
  const isSupervisingStudentClass = (studentForm: string, studentSection: string) => {
    return supervisedClasses.some(cls => 
      cls.form === studentForm && cls.section === studentSection
    );
  };

  // Helper function to get teacher's subject IDs
  const getTeacherSubjectIds = () => {
    return teacherAssignments.map(assignment => assignment.subjectId);
  };

  // Helper function to filter reports to only show subjects the teacher teaches
  const filterReportsToTeacherSubjects = (reports: StudentReport[]) => {
    const teacherSubjectIds = getTeacherSubjectIds();
    return reports.map(report => ({
      ...report,
      subjectReports: report.subjectReports?.filter(subjectReport => 
        teacherSubjectIds.includes(subjectReport.subjectId)
      ) || []
    })).filter(report => report.subjectReports.length > 0); // Only keep students who have the teacher's subjects
  };

  const loadTeacherData = async () => {
    try {
      let assignments, classes;
      
      if (isClerk()) {
        // For clerks, get all teacher assignments
        assignments = await teacherService.getAllTeacherAssignments();
        classes = [];
      } else {
        // For teachers, get their specific assignments
        [assignments, classes] = await Promise.all([
          teacherService.getAssignedSubjectsAndClasses(),
          isClassTeacher() ? teacherService.getSupervisedClasses() : Promise.resolve([])
        ]);
      }
      
      setTeacherAssignments(assignments);
      setSupervisedClasses(classes);
    } catch (error) {
      toast.error('Failed to load teacher data');
    }
  };

  const loadReports = async () => {
    if (!selectedTerm || !selectedYear) {
      toast.error('Please select both term and year');
      return;
    }
    
    try {
      setLoading(true);
      let reportsData: StudentReport[] = [];

      if ((isClassTeacher() || isClerk()) && selectedClass) {
        // Class teachers and clerks loading class reports (all subjects for all students)
        const [form, section] = selectedClass.split('-');
        if (!form || !section) {
          toast.error('Invalid class format');
          return;
        }
        reportsData = await reportService.getClassReports(form, section, selectedTerm, selectedYear);
      } else if (selectedSubject) {
        // Smart logic: If all subjects are in the same class, load class reports
        // Otherwise, load subject-specific reports
        const assignment = teacherAssignments.find(a => a.id.toString() === selectedSubject);
        if (!assignment) {
          toast.error('No assignment found for selected subject');
          return;
        }

        if (areAllSubjectsInSameClass()) {
          // All subjects are in the same class - load class reports for better grouping
          // but filter to only show subjects the teacher teaches
          const commonClass = getCommonClass();
          if (commonClass) {
            const [form, section] = commonClass.split('-');
            const allReports = await reportService.getClassReports(form, section, selectedTerm, selectedYear);
            reportsData = filterReportsToTeacherSubjects(allReports);
          }
        } else {
          // Subjects are spread across different classes - load subject-specific reports
          reportsData = await reportService.getSubjectReports(
            assignment.subjectId, 
            assignment.form, 
            assignment.section, 
            selectedTerm, 
            selectedYear
          );
        }
      } else {
        toast.error('Please select either a class (for class teachers/clerks) or subject (for any teacher)');
        return;
      }

      setReports(reportsData);
    } catch (error: any) {
      console.error('Reports loading error:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to load reports';
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleSubjectComment = (report: StudentReport, subjectReport: any) => {
    setSelectedReport(report);
    setSelectedSubjectReport(subjectReport);
    setComment(subjectReport.comment || '');
    setIsCommentModalOpen(true);
  };

  const handleOverallComment = (report: StudentReport) => {
    setSelectedReport(report);
    setComment(report.overallComment || '');
    setIsOverallModalOpen(true);
  };

  const saveSubjectComment = async () => {
    if (!selectedReport || !selectedSubjectReport) return;

    try {
      await reportService.addSubjectComment({
        reportId: selectedReport.id,
        subjectId: selectedSubjectReport.subjectId,
        comment
      });
      toast.success('Subject comment saved');
      setIsCommentModalOpen(false);
      loadReports();
    } catch (error) {
      toast.error('Failed to save comment');
    }
  };

  const saveOverallComment = async () => {
    if (!selectedReport) return;

    try {
      await reportService.addOverallComment({
        reportId: selectedReport.id,
        comment
      });
      toast.success('Overall comment saved');
      setIsOverallModalOpen(false);
      loadReports();
    } catch (error) {
      toast.error('Failed to save comment');
    }
  };

  const finalizeReport = async (reportId: number) => {
    if (!window.confirm('Are you sure you want to finalize this report? This action cannot be undone.')) return;

    try {
      await reportService.finalizeReport(reportId);
      toast.success('Report finalized');
      loadReports();
    } catch (error) {
      toast.error('Failed to finalize report');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Student Reports</h1>
        <p className="text-gray-600">Manage student reports and add comments</p>
      </div>

      {/* Guide */}
      <ReportingGuide />

      {/* Filters */}
      <Card className="p-4">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          {(isClassTeacher() || isClerk()) && (
            <Select
              label={isClerk() ? "Class" : "Supervised Class"}
              value={selectedClass}
              onChange={(e) => {
                setSelectedClass(e.target.value);
                if (e.target.value) {
                  setSelectedSubject(''); // Clear subject selection when class is selected
                }
              }}
              options={[
                { value: '', label: isClerk() ? 'Select Class' : 'Select Supervised Class' },
                ...(isClerk() 
                  ? teacherAssignments
                      .map(assignment => `${assignment.form}-${assignment.section}`)
                      .filter((value, index, self) => self.indexOf(value) === index) // Remove duplicates
                      .map(classKey => {
                        const [form, section] = classKey.split('-');
                        const studentCount = teacherAssignments.filter(a => 
                          a.form === form && a.section === section
                        ).length;
                        return {
                          value: classKey,
                          label: `${form} ${section}`
                        };
                      })
                  : supervisedClasses.map(cls => ({
                      value: `${cls.form}-${cls.section}`,
                      label: `${cls.form} ${cls.section} (${cls.studentCount || 0} students)`
                    }))
                )
              ]}
            />
          )}
          
          {!isClerk() && teacherAssignments.length > 0 && (
            <Select
              label="Teaching Subject"
              value={selectedSubject}
              onChange={(e) => {
                setSelectedSubject(e.target.value);
                if (e.target.value) {
                  setSelectedClass(''); // Clear class selection when subject is selected
                }
              }}
              options={[
                { value: '', label: 'Select Teaching Subject' },
                ...teacherAssignments.map(assignment => ({
                  value: assignment.id.toString(),
                  label: `${assignment.subjectName} - ${assignment.form} ${assignment.section}`
                }))
              ]}
            />
          )}

          <Select
            label="Term"
            value={selectedTerm}
            onChange={(e) => setSelectedTerm(e.target.value)}
            options={[
              { value: 'Term 1', label: 'Term 1' },
              { value: 'Term 2', label: 'Term 2' },
              { value: 'Term 3', label: 'Term 3' }
            ]}
          />

          <Select
            label="Academic Year"
            value={selectedYear}
            onChange={(e) => setSelectedYear(e.target.value)}
            options={[
              { value: new Date().getFullYear().toString(), label: new Date().getFullYear().toString() },
              { value: (new Date().getFullYear() + 1).toString(), label: (new Date().getFullYear() + 1).toString() }
            ]}
          />
        </div>
        
        <div className="mt-4">
          {(isClassTeacher() || isClerk()) && !selectedClass && !selectedSubject && (
            <div className="mb-3 p-3 bg-blue-50 border border-blue-200 rounded-md">
              <p className="text-sm text-blue-800">
                💡 <strong>Tip:</strong> {isClerk() ? 'As a clerk, you can view reports for any class:' : 'As a class teacher, you can view reports in two ways:'}
                <br />• <strong>{isClerk() ? 'Class:' : 'Supervised Class:'}</strong> See ALL subjects for all students in the selected class
                {!isClerk() && (
                  <>
                    <br />• <strong>Teaching Subject:</strong> 
                    {areAllSubjectsInSameClass() 
                      ? ` See only your teaching subjects grouped by student (since all subjects are in the same class)`
                      : ` See subjects spread across different classes`
                    }
                    <br />You can add subject comments for subjects you teach and overall comments for students in your supervised class.
                  </>
                )}
              </p>
            </div>
          )}
          
          {(isClassTeacher() || isClerk()) && selectedClass && (
            <div className="mb-3 p-3 bg-yellow-50 border border-yellow-200 rounded-md">
              <p className="text-sm text-yellow-800">
                ⚠️ <strong>Note:</strong> {isClerk() ? 'The selected class may not have any students or reports yet.' : 'Your supervised class has no students. Try selecting a teaching subject to view students you teach.'}
              </p>
            </div>
          )}
          <Button onClick={loadReports} loading={loading}>
            Load Reports
          </Button>
        </div>
      </Card>

      {/* Smart Reports Display */}
      {reports.length > 0 && (
        <div className="space-y-6">
          {/* Summary Stats */}
          <Card>
            <div className="p-4">
              {!isClerk() && selectedSubject && areAllSubjectsInSameClass() && (
                <div className="mb-4 p-3 bg-green-50 border border-green-200 rounded-md">
                  <p className="text-sm text-green-800">
                    🎯 <strong>Smart Grouping Active:</strong> All your teaching subjects are in the same class, so they're grouped by student for better overview. Only your teaching subjects are displayed.
                  </p>
                </div>
              )}
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-600">{reports.length}</div>
                  <div className="text-sm text-gray-600">Total Students</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-green-600">
                    {reports.filter(r => r.finalized).length}
                  </div>
                  <div className="text-sm text-gray-600">Finalized Reports</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-orange-600">
                    {reports.filter(r => !r.finalized).length}
                  </div>
                  <div className="text-sm text-gray-600">Draft Reports</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-purple-600">
                    {reports[0]?.subjectReports?.length || 0}
                  </div>
                  <div className="text-sm text-gray-600">Subjects per Student</div>
                </div>
              </div>
            </div>
          </Card>

          {/* Search and Filter Controls */}
          <Card>
            <div className="p-4">
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                  <Input
                    placeholder="Search students..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full"
                  />
                </div>
                <div>
                  <Select
                    value={statusFilter}
                    onChange={(e) => setStatusFilter(e.target.value)}
                    options={[
                      { value: 'all', label: 'All Reports' },
                      { value: 'finalized', label: 'Finalized Only' },
                      { value: 'draft', label: 'Draft Only' }
                    ]}
                  />
                </div>
                <div>
                  <Select
                    value={sortBy}
                    onChange={(e) => setSortBy(e.target.value)}
                    options={[
                      { value: 'name', label: 'Sort by Name' },
                      { value: 'finalized', label: 'Sort by Status' },
                      { value: 'average', label: 'Sort by Average' }
                    ]}
                  />
                </div>
                <div className="text-right flex items-center space-x-2">
                  <span className="text-sm text-gray-600">
                    Showing {reports.filter(r => 
                      r.studentName.toLowerCase().includes(searchTerm.toLowerCase()) &&
                      (statusFilter === 'all' || 
                       (statusFilter === 'finalized' && r.finalized) ||
                       (statusFilter === 'draft' && !r.finalized))
                    ).length} of {reports.length} students
                  </span>
                  {isClassTeacher() && (
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => {
                        const needsComments = reports.filter(r => 
                          r.subjectReports?.some(sr => 
                            canCommentOnSubject(sr.subjectId) && !sr.comment
                          )
                        ).length;
                        toast.success(`${needsComments} students need subject comments`);
                      }}
                      className="bg-gradient-to-r from-blue-50 to-indigo-50 border-blue-200 text-blue-700 hover:from-blue-100 hover:to-indigo-100 hover:border-blue-300 hover:text-blue-800 transition-all duration-200 shadow-sm"
                    >
                      <MessageSquare className="h-3 w-3 mr-1" />
                      Check Comments
                    </Button>
                  )}
                </div>
              </div>
            </div>
          </Card>

          {/* Student Cards Grid */}
          <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
            {reports
              .filter(report => 
                report.studentName.toLowerCase().includes(searchTerm.toLowerCase()) &&
                (statusFilter === 'all' || 
                 (statusFilter === 'finalized' && report.finalized) ||
                 (statusFilter === 'draft' && !report.finalized))
              )
              .sort((a, b) => {
                switch (sortBy) {
                  case 'name':
                    return a.studentName.localeCompare(b.studentName);
                  case 'finalized':
                    return (b.finalized ? 1 : 0) - (a.finalized ? 1 : 0);
                  case 'average':
                    const aAvg = a.subjectReports?.reduce((sum, sub) => sum + (sub.finalMark || 0), 0) / (a.subjectReports?.length || 1);
                    const bAvg = b.subjectReports?.reduce((sum, sub) => sum + (sub.finalMark || 0), 0) / (b.subjectReports?.length || 1);
                    return bAvg - aAvg;
                  default:
                    return 0;
                }
              })
              .map((report, reportIndex) => (
              <Card key={`report-${reportIndex}`} className="hover:shadow-lg transition-shadow">
                {/* Student Header */}
                <div className="p-4 border-b border-gray-200">
                  <div className="flex items-center justify-between">
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">{report.studentName}</h3>
                      <p className="text-sm text-gray-500">{report.form} {report.section}</p>
                      {report.subjectReports && report.subjectReports.length > 0 && (
                        <p className="text-xs text-gray-600 mt-1">
                          Avg: {(
                            report.subjectReports.reduce((sum, sub) => sum + (sub.finalMark || 0), 0) / 
                            report.subjectReports.length
                          ).toFixed(1)}%
                        </p>
                      )}
                    </div>
                    <div className="flex items-center space-x-2">
                      <Badge variant={report.finalized ? 'success' : 'warning'}>
                        {report.finalized ? 'Finalized' : 'Draft'}
                      </Badge>
                    </div>
                  </div>
                </div>

                {/* Subjects Summary */}
                <div className="p-4">
                  <div className="space-y-3">
                    {/* Subjects Grid - Compact View */}
                    <div className="grid grid-cols-2 gap-2">
                      {report.subjectReports && report.subjectReports.length > 0 ? (
                        report.subjectReports.map((subjectReport, index) => (
                          <div 
                            key={`subject-${reportIndex}-${index}`} 
                            className={`p-3 rounded-lg border transition-colors ${
                              !isClerk() && canCommentOnSubject(subjectReport.subjectId) 
                                ? 'border-blue-200 bg-blue-50 hover:bg-blue-100 cursor-pointer' 
                                : 'border-gray-200 bg-gray-50'
                            }`}
                            onClick={!isClerk() ? () => handleSubjectComment(report, subjectReport) : undefined}
                          >
                            <div className="flex items-center justify-between mb-1">
                              <span className="text-sm font-medium text-gray-900 truncate">
                                {subjectReport.subjectName}
                              </span>
                              <div className="flex items-center space-x-1">
                                <span className={`text-xs font-bold ${subjectReport.finalMark ? 'text-purple-800 bg-purple-100 px-2 py-1 rounded' : 'text-gray-400'}`}>
                                  {subjectReport.finalMark ? `${subjectReport.finalMark.toFixed(0)}%` : 'N/A'}
                                </span>
                                {!isClerk() && canAddSubjectComments() && canCommentOnSubject(subjectReport.subjectId) && (
                                  <MessageSquare 
                                    className={`h-3 w-3 ${subjectReport.comment ? 'text-green-600 fill-current' : 'text-gray-400'}`} 
                                  />
                                )}
                              </div>
                            </div>
                            <div className="flex items-center justify-between text-xs">
                              <span className="flex items-center space-x-1">
                                <span className="font-semibold text-blue-700">CW:</span>
                                <span className={`font-bold ${subjectReport.courseworkMark ? 'text-blue-800 bg-blue-100 px-1 rounded' : 'text-gray-400'}`}>
                                  {subjectReport.courseworkMark ? `${subjectReport.courseworkMark.toFixed(0)}%` : 'N/A'}
                                </span>
                              </span>
                              <span className="flex items-center space-x-1">
                                <span className="font-semibold text-green-700">EX:</span>
                                <span className={`font-bold ${subjectReport.examMark ? 'text-green-800 bg-green-100 px-1 rounded' : 'text-gray-400'}`}>
                                  {subjectReport.examMark ? `${subjectReport.examMark.toFixed(0)}%` : 'N/A'}
                                </span>
                              </span>
                            </div>
                          </div>
                        ))
                      ) : (
                        <div className="col-span-2 text-sm text-gray-500 text-center py-4">
                          No subjects assigned
                        </div>
                      )}
                    </div>

                    {/* Overall Comment Section - Only for Class Teachers supervising this student's class */}
                    {!isClerk() && isClassTeacher() && isSupervisingStudentClass(report.form, report.section) && (
                      <div className="border-t border-gray-200 pt-3">
                        <div className="flex items-center justify-between mb-2">
                          <div className="text-sm">
                            <span className="font-medium text-gray-900">Overall Comment: </span>
                            {report.overallComment ? (
                              <span className="text-green-600">✓ Added</span>
                            ) : (
                              <span className="text-gray-400">Not added</span>
                            )}
                          </div>
                        </div>
                        <div className="flex space-x-2">
                          {canAddOverallComments() && !report.finalized && (
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => handleOverallComment(report)}
                              className="flex-1"
                            >
                              <MessageSquare className="h-3 w-3 mr-1" />
                              Comment
                            </Button>
                          )}
                          {canAddOverallComments() && !report.finalized && (
                            <Button
                              size="sm"
                              onClick={() => finalizeReport(report.id)}
                              className="flex-1"
                            >
                              <CheckCircle className="h-3 w-3 mr-1" />
                              Finalize
                            </Button>
                          )}
                        </div>
                      </div>
                    )}
                  </div>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {/* Subject Comment Modal */}
      <Modal
        isOpen={isCommentModalOpen}
        onClose={() => setIsCommentModalOpen(false)}
        title="Add Subject Comment"
      >
        <div className="space-y-4">
          <div>
            <p className="text-sm text-gray-600">
              Student: {selectedReport?.studentName}
            </p>
            <p className="text-sm text-gray-600">
              Subject: {selectedSubjectReport?.subjectName}
            </p>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Comment</label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Enter your comment for this subject..."
              rows={4}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div className="flex justify-end space-x-3">
            <Button variant="secondary" onClick={() => setIsCommentModalOpen(false)}>
              Cancel
            </Button>
            <Button onClick={saveSubjectComment}>
              Save Comment
            </Button>
          </div>
        </div>
      </Modal>

      {/* Overall Comment Modal */}
      <Modal
        isOpen={isOverallModalOpen}
        onClose={() => setIsOverallModalOpen(false)}
        title="Add Overall Comment"
      >
        <div className="space-y-4">
          <div>
            <p className="text-sm text-gray-600">
              Student: {selectedReport?.studentName}
            </p>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Overall Comment</label>
            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Enter overall comment for this student..."
              rows={4}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          <div className="flex justify-end space-x-3">
            <Button variant="secondary" onClick={() => setIsOverallModalOpen(false)}>
              Cancel
            </Button>
            <Button onClick={saveOverallComment}>
              Save Comment
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default ReportsPage;