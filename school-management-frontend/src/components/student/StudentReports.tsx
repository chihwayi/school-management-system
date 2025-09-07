import React, { useState, useEffect } from 'react';
import { Card, Button, Select } from '../ui';
import { 
  FileText, 
  Download, 
  Eye, 
  Calendar, 
  GraduationCap,
  AlertCircle,
  CheckCircle,
  X,
  Filter,
  Search
} from 'lucide-react';
import { studentService, type StudentReport } from '../../services/studentService';

interface StudentReportsProps {
  studentId: number;
  studentName: string;
  canAccessReports: boolean;
}

const StudentReports: React.FC<StudentReportsProps> = ({ 
  studentId, 
  studentName, 
  canAccessReports 
}) => {
  const [reports, setReports] = useState<StudentReport[]>([]);
  const [filteredReports, setFilteredReports] = useState<StudentReport[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedReport, setSelectedReport] = useState<StudentReport | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterTerm, setFilterTerm] = useState('');
  const [filterYear, setFilterYear] = useState('');

  useEffect(() => {
    if (canAccessReports) {
      loadReports();
    } else {
      setLoading(false);
    }
  }, [studentId, canAccessReports]);

  useEffect(() => {
    filterReports();
  }, [reports, searchTerm, filterTerm, filterYear]);

  const loadReports = async () => {
    try {
      setLoading(true);
      const reportsData = await studentService.getStudentReports(studentId);
      // Filter to show only published (finalized) reports
      const publishedReports = reportsData.filter(report => report.finalized);
      setReports(publishedReports);
    } catch (error) {
      console.error('Error loading reports:', error);
    } finally {
      setLoading(false);
    }
  };

  const filterReports = () => {
    let filtered = reports;

    // Filter by search term
    if (searchTerm) {
      filtered = filtered.filter(report =>
        report.term.toLowerCase().includes(searchTerm.toLowerCase()) ||
        report.academicYear.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }

    // Filter by term
    if (filterTerm) {
      filtered = filtered.filter(report => report.term === filterTerm);
    }

    // Filter by year
    if (filterYear) {
      filtered = filtered.filter(report => report.academicYear === filterYear);
    }

    setFilteredReports(filtered);
  };

  const handleViewReport = async (report: StudentReport) => {
    try {
      setSelectedReport(report);
      // For now, we'll show a simple report view instead of PDF
      // In a real implementation, this would load the actual PDF
    } catch (error) {
      console.error('Error loading report:', error);
      alert('Failed to load report. Please try again.');
    }
  };

  const handleClosePdf = () => {
    setSelectedReport(null);
  };

  const handleDownloadReport = async (report: StudentReport) => {
    try {
      // Create a professional text report for download
      let reportContent = "";
      
      // Header
      reportContent += "=".repeat(60) + "\n";
      reportContent += "           PEPPERMINT CHRISTIAN SCHOOL\n";
      reportContent += "              ACADEMIC REPORT CARD\n";
      reportContent += "=".repeat(60) + "\n\n";
      
      // Student Information
      reportContent += `Student Name: ${report.studentName}\n`;
      reportContent += `Form: ${report.form} ${report.section}\n`;
      reportContent += `Term: ${report.term} ${report.academicYear}\n`;
      reportContent += `Report Date: ${report.createdAt ? new Date(report.createdAt).toLocaleDateString() : 'N/A'}\n\n`;
      
      // Subject Results
      if (report.subjectReports && report.subjectReports.length > 0) {
        reportContent += "SUBJECT RESULTS:\n";
        reportContent += "-".repeat(60) + "\n";
        reportContent += "Subject".padEnd(20) + "Coursework".padEnd(12) + "Exam".padEnd(8) + "Total".padEnd(8) + "Grade\n";
        reportContent += "-".repeat(60) + "\n";
        
        report.subjectReports.forEach(subject => {
          const totalMark = subject.courseworkMark && subject.examMark 
            ? Math.round((subject.courseworkMark + subject.examMark) / 2)
            : subject.finalMark;
          const grade = totalMark ? (totalMark >= 80 ? 'A' : totalMark >= 70 ? 'B' : totalMark >= 60 ? 'C' : totalMark >= 50 ? 'D' : 'F') : 'N/A';
          
          reportContent += subject.subjectName.padEnd(20) + 
                          (subject.courseworkMark || 'N/A').toString().padEnd(12) + 
                          (subject.examMark || 'N/A').toString().padEnd(8) + 
                          (totalMark || 'N/A').toString().padEnd(8) + 
                          grade + "\n";
        });
        reportContent += "\n";
        
        // Teacher Comments
        reportContent += "TEACHER COMMENTS:\n";
        reportContent += "-".repeat(60) + "\n";
        report.subjectReports.forEach(subject => {
          if (subject.comment) {
            reportContent += `${subject.subjectName}:\n`;
            reportContent += `  ${subject.comment}\n\n`;
          }
        });
      }
      
      // Class Teacher Comment
      if (report.overallComment) {
        reportContent += "CLASS TEACHER'S COMMENT:\n";
        reportContent += "-".repeat(60) + "\n";
        reportContent += report.overallComment + "\n\n";
      }
      
      // Footer
      reportContent += "=".repeat(60) + "\n";
      reportContent += "Peppermint Christian School\n";
      reportContent += "123 Castle larger • +263778886543\n";
      reportContent += `Report Status: ${report.finalized ? 'Official Report' : 'Draft Report'}\n`;
      reportContent += "=".repeat(60) + "\n";
      
      // Create and download the file
      const blob = new Blob([reportContent], { type: 'text/plain' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `${studentName}_${report.term}_${report.academicYear}_Report.txt`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error('Error downloading report:', error);
      alert('Failed to download report. Please try again.');
    }
  };

  const getGradeColor = (mark: number | undefined) => {
    if (!mark) return 'text-gray-600 bg-gray-100';
    if (mark >= 90) return 'text-green-600 bg-green-100';
    if (mark >= 80) return 'text-blue-600 bg-blue-100';
    if (mark >= 70) return 'text-yellow-600 bg-yellow-100';
    if (mark >= 60) return 'text-orange-600 bg-orange-100';
    return 'text-red-600 bg-red-100';
  };

  const getUniqueTerms = () => {
    const terms = [...new Set(reports.map(report => report.term))];
    return terms.sort();
  };

  const getUniqueYears = () => {
    const years = [...new Set(reports.map(report => report.academicYear))];
    return years.sort((a, b) => b.localeCompare(a));
  };

  if (!canAccessReports) {
    return (
      <Card className="p-6">
        <div className="text-center py-8">
          <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">Reports Not Available</h3>
          <p className="text-gray-600 mb-4">
            You need to clear your outstanding balance to access your reports.
          </p>
          <p className="text-sm text-gray-500">
            Please contact the school administration to resolve any outstanding fees.
          </p>
        </div>
      </Card>
    );
  }

  return (
    <>
      <Card className="p-6">
        <div className="flex items-center justify-between mb-6">
          <div className="flex items-center">
            <FileText className="h-6 w-6 text-blue-600 mr-3" />
            <div>
              <h2 className="text-xl font-semibold text-gray-900">My Reports</h2>
              <p className="text-sm text-gray-600">View your academic progress reports</p>
            </div>
          </div>
          <div className="text-sm text-gray-500">
            {filteredReports.length} of {reports.length} reports
          </div>
        </div>

        {/* Search and Filters */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search reports..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <Select
            value={filterTerm}
            onChange={(e) => setFilterTerm(e.target.value)}
            options={[
              { value: '', label: 'All Terms' },
              ...getUniqueTerms().map(term => ({ value: term, label: term }))
            ]}
          />
          
          <Select
            value={filterYear}
            onChange={(e) => setFilterYear(e.target.value)}
            options={[
              { value: '', label: 'All Years' },
              ...getUniqueYears().map(year => ({ value: year, label: year }))
            ]}
          />
        </div>

        {/* Reports List */}
        {loading ? (
          <div className="text-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">Loading reports...</p>
          </div>
        ) : filteredReports.length === 0 ? (
          <div className="text-center py-8">
            <FileText className="h-12 w-12 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">No reports available</p>
            <p className="text-sm text-gray-400 mt-2">
              {reports.length === 0 ? 'Reports will appear here when published' : 'No reports match your search criteria'}
            </p>
          </div>
        ) : (
          <div className="space-y-4">
            {filteredReports.map((report) => (
              <div key={report.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                <div className="flex items-center justify-between">
                  <div className="flex-1">
                    <h4 className="text-lg font-medium text-gray-900">
                      {report.term} Report - {report.academicYear}
                    </h4>
                  </div>

                  <div className="flex items-center space-x-2 ml-4">
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleViewReport(report)}
                    >
                      <Eye className="h-4 w-4 mr-1" />
                      View
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      onClick={() => handleDownloadReport(report)}
                    >
                      <Download className="h-4 w-4 mr-1" />
                      Download
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* Report Viewer Modal */}
      {selectedReport && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg shadow-xl max-w-5xl max-h-[90vh] w-full mx-4 flex flex-col">
            {/* Header */}
            <div className="flex items-center justify-between p-4 border-b">
              <div className="flex items-center">
                <FileText className="h-5 w-5 text-blue-600 mr-2" />
                <h2 className="text-lg font-semibold text-gray-900">
                  {studentName} - {selectedReport.term} Report {selectedReport.academicYear}
                </h2>
              </div>
              <Button
                variant="outline"
                size="sm"
                onClick={handleClosePdf}
                className="p-2"
              >
                <X className="h-4 w-4" />
              </Button>
            </div>

            {/* Report Content */}
            <div className="flex-1 overflow-auto p-6 bg-white">
              <div className="max-w-5xl mx-auto">
                {/* Background Watermark */}
                <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 opacity-5 pointer-events-none z-0">
                  <img 
                    src="http://localhost:8080/api/uploads/background_5c3e3739-500f-43b1-bf30-eec19577e033_logo.png" 
                    alt="Background" 
                    className="max-w-96 max-h-96 object-contain"
                    onError={(e) => {
                      e.currentTarget.style.display = 'none';
                    }}
                  />
                </div>

                {/* School Header - Exact match to clerk design */}
                <div className="flex justify-between items-start mb-5 border-b-4 border-purple-600 pb-4 relative z-10">
                  <div className="text-center">
                    <img 
                      src="http://localhost:8080/api/uploads/logo_551f87cc-0d1a-4231-a135-f21481406a55_logo.png" 
                      alt="School Logo" 
                      className="max-h-20 max-w-30 mx-auto"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                      }}
                    />
                    <div className="text-xs font-bold mt-1">Peppermint Christian School</div>
                  </div>
                  <div className="flex-1 text-center mx-5">
                    <h1 className="text-2xl font-bold text-purple-600 mb-2 uppercase">
                      Peppermint Christian School
                    </h1>
                    <h2 className="text-lg font-bold text-purple-600 uppercase">
                      Student's Monthly Progress Report
                    </h2>
                  </div>
                  <div className="text-center">
                    <img 
                      src="http://localhost:8080/api/uploads/ministry_logo_074e3b7d-aa76-4068-a6ea-00fce1b8dbe3_Ministry-Health-Child-Care-MoHCC-1200x600.jpg" 
                      alt="Ministry Logo" 
                      className="max-h-20 max-w-30 mx-auto"
                      onError={(e) => {
                        e.currentTarget.style.display = 'none';
                      }}
                    />
                    <div className="text-xs font-bold mt-1">Ministry of Primary &<br />Secondary Education</div>
                  </div>
                </div>

                {/* Student Info Grid - Exact match to clerk design */}
                <div className="grid grid-cols-3 gap-4 mb-5 text-sm relative z-10">
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">Name of Student:</span> 
                    <span className="ml-2">{selectedReport.studentName}</span>
                  </div>
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">Form:</span> 
                    <span className="ml-2">{selectedReport.form} {selectedReport.section}</span>
                  </div>
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">Subjects Recorded:</span> 
                    <span className="ml-2">{selectedReport.subjectReports?.length || 0}</span>
                  </div>
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">No. Passed:</span> 
                    <span className="ml-2">
                      {selectedReport.subjectReports?.filter(sr => 
                        (sr.courseworkMark && sr.courseworkMark >= 50) || 
                        (sr.examMark && sr.examMark >= 50)
                      ).length || 0}
                    </span>
                  </div>
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">Year:</span> 
                    <span className="ml-2">{selectedReport.academicYear}</span>
                  </div>
                  <div className="border-b border-gray-300 pb-1">
                    <span className="text-purple-600 font-bold">Month:</span> 
                    <span className="ml-2">{selectedReport.term}</span>
                  </div>
                </div>

                {/* Subject Reports Table - Exact match to clerk design */}
                {selectedReport.subjectReports && selectedReport.subjectReports.length > 0 ? (
                  <div className="relative z-10">
                    <table className="w-full border-collapse border border-gray-800 text-xs my-5">
                      <thead>
                        <tr>
                          <th rowSpan={2} className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">
                            SUBJECT
                          </th>
                          <th colSpan={2} className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">
                            COURSE WORK MARK
                          </th>
                          <th colSpan={2} className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">
                            EXAM MARK
                          </th>
                          <th rowSpan={2} className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">
                            SUBJECT T'R's COMMENT
                          </th>
                          <th rowSpan={2} className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">
                            TEACHER'S SIGNATURE
                          </th>
                        </tr>
                        <tr>
                          <th className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">%</th>
                          <th className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">GR</th>
                          <th className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">%</th>
                          <th className="border border-gray-800 bg-purple-600 text-white p-1 font-bold text-center">GR</th>
                        </tr>
                      </thead>
                      <tbody>
                        {selectedReport.subjectReports.map((subject, index) => {
                          // Correct grade calculation matching clerk design
                          const cwGrade = subject.courseworkMark && subject.courseworkMark >= 80 ? 'A' : 
                                         subject.courseworkMark >= 70 ? 'B' : 
                                         subject.courseworkMark >= 60 ? 'C' : 
                                         subject.courseworkMark >= 50 ? 'D' : 
                                         subject.courseworkMark ? 'F' : '-';
                          
                          const examGrade = subject.examMark && subject.examMark >= 80 ? 'A' : 
                                           subject.examMark >= 70 ? 'B' : 
                                           subject.examMark >= 60 ? 'C' : 
                                           subject.examMark >= 50 ? 'D' : 
                                           subject.examMark ? 'F' : '-';
                          
                          return (
                            <tr key={index}>
                              <td className="border border-gray-800 p-1 text-left">
                                {subject.subjectName}
                              </td>
                              <td className="border border-gray-800 p-1 text-center">
                                {subject.courseworkMark !== null && subject.courseworkMark !== undefined ? Math.round(subject.courseworkMark) : '-'}
                              </td>
                              <td className="border border-gray-800 p-1 text-center">
                                {subject.courseworkMark ? cwGrade : '-'}
                              </td>
                              <td className="border border-gray-800 p-1 text-center">
                                {subject.examMark !== null && subject.examMark !== undefined ? Math.round(subject.examMark) : '-'}
                              </td>
                              <td className="border border-gray-800 p-1 text-center">
                                {subject.examMark ? examGrade : '-'}
                              </td>
                              <td className="border border-gray-800 p-1 text-left max-w-32 break-words">
                                {subject.comment || <span className="text-gray-500">No comment</span>}
                              </td>
                              <td className="border border-gray-800 p-1 text-center">
                                {subject.teacherSignatureUrl ? (
                                  <img 
                                    src={`http://localhost:8080${subject.teacherSignatureUrl}`}
                                    alt="Teacher Signature"
                                    className="max-h-4 max-w-10 mx-auto"
                                    onError={(e) => {
                                      e.currentTarget.style.display = 'none';
                                    }}
                                  />
                                ) : (
                                  <span className="text-gray-500 text-xs">No signature</span>
                                )}
                              </td>
                            </tr>
                          );
                        })}
                      </tbody>
                    </table>
                  </div>
                ) : (
                  <div className="text-center py-8">
                    <FileText className="h-12 w-12 text-gray-300 mx-auto mb-4" />
                    <p className="text-gray-500">No subject results available for this report.</p>
                  </div>
                )}

                {/* Footer Section - Exact match to clerk design */}
                <div className="mt-8 grid grid-cols-3 gap-5 relative z-10">
                  <div className="col-span-2 space-y-4">
                    {/* Form Teacher's Comments */}
                    <div className="border-b border-gray-800 pb-2 min-h-6">
                      <span className="text-purple-600 font-bold text-sm">Form Teacher's Comments:</span><br />
                      <span className="text-xs">{selectedReport.overallComment || ''}</span>
                    </div>
                    
                    {/* Form Teacher's Signature */}
                    <div className="border-b border-gray-800 pb-2 min-h-6">
                      <span className="text-purple-600 font-bold text-sm">Form Teacher's Signature:</span>
                      {selectedReport.classTeacherSignatureUrl ? (
                        <img 
                          src={`http://localhost:8080${selectedReport.classTeacherSignatureUrl}`}
                          alt="Form Teacher Signature"
                          className="max-h-5 max-w-15 mt-1"
                          onError={(e) => {
                            e.currentTarget.style.display = 'none';
                          }}
                        />
                      ) : (
                        <span className="text-gray-500 text-xs ml-2">No signature uploaded</span>
                      )}
                    </div>
                    
                    {/* Principal's Signature */}
                    <div className="border-b border-gray-800 pb-2 min-h-6">
                      <span className="text-purple-600 font-bold text-sm">Principal's Signature:</span>
                      <img 
                        src="http://localhost:8080/uploads/signatures/signature_3_403f84f5-7431-4b1f-8b22-dd23908e90d3.png"
                        alt="Principal Signature"
                        className="max-h-5 max-w-15 mt-1"
                        onError={(e) => {
                          e.currentTarget.style.display = 'none';
                        }}
                      />
                    </div>
                    
                    {/* Parent's Signature */}
                    <div className="border-b border-gray-800 pb-2 min-h-6">
                      <span className="text-purple-600 font-bold text-sm">Parent's Signature:</span>
                      <span className="text-gray-800 text-xs ml-2">___________________________</span>
                    </div>
                  </div>
                  
                  {/* School Stamp */}
                  <div className="col-span-1">
                    <div className="border-2 border-gray-800 text-center p-6 font-bold text-sm bg-transparent h-32 flex items-center justify-center">
                      <div>
                        <div className="text-lg font-bold mb-1">SCHOOL</div>
                        <div className="text-lg font-bold">STAMP</div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="p-4 border-t bg-gray-50">
              <div className="text-center">
                <p className="text-sm text-gray-600">
                  Peppermint Christian School • 123 Castle larger • +263778886543
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  {selectedReport.studentName} - {selectedReport.term} {selectedReport.academicYear} • 
                  {selectedReport.finalized ? ' Official Report' : ' Draft Report'}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default StudentReports;
