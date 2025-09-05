import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Button, Input, Select, SearchableSelect } from '../ui';
import type { AssessmentDTO, Assessment } from '../../types';
import { AssessmentType } from '../../types';
import { formatAssessmentType } from '../../utils';
import { teacherService } from '../../services/teacherService';
import { studentService } from '../../services/studentService';
import { assessmentService } from '../../services/assessmentService';

interface AssessmentFormProps {
  onSubmit: (data: AssessmentDTO) => Promise<void>;
  onCancel: () => void;
  initialData?: Assessment;
  studentSubjectId?: number;
  isLoading?: boolean;
}

const AssessmentForm: React.FC<AssessmentFormProps> = ({
  onSubmit,
  onCancel,
  initialData,
  isLoading = false
}) => {
  const [teacherAssignments, setTeacherAssignments] = useState<any[]>([]);
  const [students, setStudents] = useState<any[]>([]);
  const [filteredStudents, setFilteredStudents] = useState<any[]>([]);
  const [existingAssessments, setExistingAssessments] = useState<any[]>([]);
  
  // Step-by-step filter states
  const [selectedYear, setSelectedYear] = useState<string>('');
  const [selectedTerm, setSelectedTerm] = useState<string>('');
  const [selectedAssignment, setSelectedAssignment] = useState<any>(null);
  const [selectedStudent, setSelectedStudent] = useState<any>(null);
  
  const {
    register,
    handleSubmit,
    formState: { errors },
    reset,
    watch,
    setValue
  } = useForm<AssessmentDTO>({
    defaultValues: initialData
      ? {
          studentId: initialData.studentId,
          subjectId: initialData.subjectId,
          title: initialData.title,
          date: initialData.date,
          score: initialData.score,
          maxScore: initialData.maxScore,
          type: initialData.type,
          term: initialData.term,
          academicYear: initialData.academicYear
        }
      : {
          studentId: 0,
          subjectId: 0,
          title: '',
          date: new Date().toISOString().split('T')[0],
          score: 0,
          maxScore: 100,
          type: AssessmentType.COURSEWORK,
          term: 'Term 1',
          academicYear: new Date().getFullYear().toString()
        }
  });

  const watchedType = watch('type');
  
  useEffect(() => {
    loadTeacherAssignments();
  }, []);

  // Load existing assessments when all filters are set AND students are loaded
  useEffect(() => {
    if (selectedAssignment && selectedTerm && selectedYear && students.length > 0) {
      loadExistingAssessments();
    }
  }, [selectedAssignment, selectedTerm, selectedYear, students]);

  // Filter students when existing assessments change
  useEffect(() => {
    if (students.length > 0 && existingAssessments.length > 0) {
      filterAvailableStudents();
    } else {
      setFilteredStudents(students);
    }
  }, [students, existingAssessments, watchedType]);

  const loadTeacherAssignments = async () => {
    try {
      const assignments = await teacherService.getAssignedSubjectsAndClasses();
      setTeacherAssignments(assignments);
      
      // If editing, set up the form for editing
      if (initialData) {
        setSelectedYear(initialData.academicYear);
        setSelectedTerm(initialData.term);
        
        const matchingAssignment = assignments.find(a => 
          a.subject?.id === initialData.subjectId && 
          a.form === initialData.studentForm && 
          a.section === initialData.studentSection
        );
        if (matchingAssignment) {
          setSelectedAssignment(matchingAssignment);
          await loadStudentsForClass(initialData.studentForm, initialData.studentSection);
          setTimeout(() => {
            const student = { id: initialData.studentId };
            setSelectedStudent(student);
          }, 100);
        }
      }
    } catch (error) {
      console.error('Error loading teacher assignments:', error);
    }
  };
  
  const loadStudentsForClass = async (form: string, section: string) => {
    try {
      const studentsData = await studentService.getStudentsByClass(form, section);
      setStudents(studentsData);
      setFilteredStudents(studentsData);
    } catch (error) {
      console.error('Error loading students:', error);
    }
  };

  const loadExistingAssessments = async () => {
    try {
      // Handle both possible structures: subject.id or subjectId
      const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
      if (!subjectId) return;

      const allAssessments: any[] = [];
      
      for (const student of students) {
        try {
          const studentAssessments = await assessmentService.getStudentSubjectAssessments(
            student.id, 
            subjectId
          );
          
          // Filter assessments for the selected year and term
          const filteredAssessments = studentAssessments.filter((assessment: any) => 
            assessment.term === selectedTerm && 
            assessment.academicYear === selectedYear
          );
          
          allAssessments.push(...filteredAssessments);
        } catch (error) {
          console.error(`Error loading assessments for student ${student.id}:`, error);
        }
      }
      
      setExistingAssessments(allAssessments);
    } catch (error) {
      console.error('Error loading existing assessments:', error);
    }
  };

  const filterAvailableStudents = () => {
    // Handle both possible structures: subject.id or subjectId
    const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
    
    // Show all students but with intelligent filtering based on assessment status
    const availableStudents = students.filter(student => {
      // Get all assessments for this student, subject, term, and year
      const studentAssessments = existingAssessments.filter((assessment: any) => 
        assessment.studentId === student.id &&
        assessment.subjectId === subjectId &&
        assessment.term === selectedTerm &&
        assessment.academicYear === selectedYear
      );
      
      // Check if student has completed both Coursework and Final Exam
      const hasCompletedCoursework = studentAssessments.some((assessment: any) => 
        assessment.type === AssessmentType.COURSEWORK
      );
      const hasCompletedFinalExam = studentAssessments.some((assessment: any) => 
        assessment.type === AssessmentType.FINAL_EXAM
      );
      const hasCompletedAllTypes = hasCompletedCoursework && hasCompletedFinalExam;
      
      // If student has completed all assessment types, they should be hidden
      if (hasCompletedAllTypes) {
        return false;
      }
      
      // Student is available (they have at least one assessment type not completed)
      return true;
    });
    
    setFilteredStudents(availableStudents);
  };

  const handleYearChange = (year: string) => {
    setSelectedYear(year);
    setValue('academicYear', year);
    setSelectedTerm('');
    setValue('term', '');
    setSelectedAssignment(null);
    setValue('subjectId', 0);
    setSelectedStudent(null);
    setValue('studentId', 0);
    setStudents([]);
    setFilteredStudents([]);
    setExistingAssessments([]);
  };

  const handleTermChange = (term: string) => {
    setSelectedTerm(term);
    setValue('term', term);
    setSelectedAssignment(null);
    setValue('subjectId', 0);
    setSelectedStudent(null);
    setValue('studentId', 0);
    setStudents([]);
    setFilteredStudents([]);
    setExistingAssessments([]);
  };

  const handleAssignmentChange = (assignmentId: string) => {
    const assignment = teacherAssignments.find(a => a.id.toString() === assignmentId);
    setSelectedAssignment(assignment);
    setSelectedStudent(null);
    setValue('studentId', 0);
    setStudents([]);
    setFilteredStudents([]);
    setExistingAssessments([]);
    
    if (assignment) {
      loadStudentsForClass(assignment.form, assignment.section);
    }
  };

  const handleStudentChange = (studentId: string) => {
    const student = filteredStudents.find(s => s.id.toString() === studentId);
    setSelectedStudent(student);
    
    if (student && selectedAssignment) {
      // Handle both possible structures: subject.id or subjectId
      const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
      setValue('studentId', student.id);
      setValue('subjectId', subjectId);
    }
  };

  const getAvailableAssessmentTypes = () => {
    if (!selectedStudent || !selectedAssignment) {
      return [
        { value: AssessmentType.COURSEWORK, label: formatAssessmentType(AssessmentType.COURSEWORK) },
        { value: AssessmentType.FINAL_EXAM, label: formatAssessmentType(AssessmentType.FINAL_EXAM) }
      ];
    }

    // Handle both possible structures: subject.id or subjectId
    const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
    const studentAssessments = existingAssessments.filter((assessment: any) => 
      assessment.studentId === selectedStudent.id &&
      assessment.subjectId === subjectId &&
      assessment.term === selectedTerm &&
      assessment.academicYear === selectedYear
    );

    const completedTypes = studentAssessments.map((assessment: any) => assessment.type);
    
    const availableTypes = [
      { value: AssessmentType.COURSEWORK, label: formatAssessmentType(AssessmentType.COURSEWORK) },
      { value: AssessmentType.FINAL_EXAM, label: formatAssessmentType(AssessmentType.FINAL_EXAM) }
    ].filter(option => !completedTypes.includes(option.value));

    return availableTypes;
  };

  const getStudentAssessmentStatus = () => {
    if (!selectedStudent || !selectedAssignment) {
      return { hasAssessments: false, completedTypes: [], availableTypes: [], allCompleted: false };
    }

    // Handle both possible structures: subject.id or subjectId
    const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
    
    const studentAssessments = existingAssessments.filter((assessment: any) => 
      assessment.studentId === selectedStudent.id &&
      assessment.subjectId === subjectId &&
      assessment.term === selectedTerm &&
      assessment.academicYear === selectedYear
    );

    const completedTypes = studentAssessments.map((assessment: any) => assessment.type);
    const availableTypes = getAvailableAssessmentTypes();

    return {
      hasAssessments: studentAssessments.length > 0,
      completedTypes,
      availableTypes,
      allCompleted: availableTypes.length === 0 && studentAssessments.length > 0
    };
  };

  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const handleFormSubmit = async (data: AssessmentDTO) => {
    if (isSubmitting) return;
    
    // Check for duplicate assessment before submission
    const status = getStudentAssessmentStatus();
    if (status.allCompleted) {
      alert('All assessment types have already been completed for this student, subject, term, and year. Cannot create duplicate assessments.');
      return;
    }
    
    const availableTypes = getAvailableAssessmentTypes();
    if (availableTypes.length === 0) {
      alert('No assessment types are available for this student. All types have been completed.');
      return;
    }
    
    if (!availableTypes.some(type => type.value === data.type)) {
      alert(`Assessment type "${data.type}" has already been completed for this student. Please select a different type.`);
      return;
    }
    
    try {
      setIsSubmitting(true);
      await onSubmit(data);
      reset();
      // Reset all selections
      setSelectedYear('');
      setSelectedTerm('');
      setSelectedAssignment(null);
      setSelectedStudent(null);
      setStudents([]);
      setFilteredStudents([]);
      setExistingAssessments([]);
    } catch (error) {
      console.error('Form submission error:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  const currentYear = new Date().getFullYear();
  const academicYearOptions = [
    { value: currentYear.toString(), label: currentYear.toString() },
    { value: (currentYear + 1).toString(), label: (currentYear + 1).toString() }
  ];

  const termOptions = [
    { value: 'Term 1', label: 'Term 1' },
    { value: 'Term 2', label: 'Term 2' },
    { value: 'Term 3', label: 'Term 3' }
  ];

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-6">
      {/* Step 1: Academic Year */}
      <div>
        <Select
          label="Academic Year *"
          options={[
            { value: '', label: 'Select Academic Year' },
            ...academicYearOptions
          ]}
          value={selectedYear}
          onChange={(e) => handleYearChange(e.target.value)}
          disabled={!!initialData}
        />
      </div>

      {/* Step 2: Term (only if year is selected) */}
      {selectedYear && (
        <div>
          <Select
            label="Term *"
            options={[
              { value: '', label: 'Select Term' },
              ...termOptions
            ]}
            value={selectedTerm}
            onChange={(e) => handleTermChange(e.target.value)}
            disabled={!!initialData}
          />
        </div>
      )}

      {/* Step 3: Subject & Class (only if year and term are selected) */}
      {selectedYear && selectedTerm && (
        <div>
          <Select
            label="Subject & Class *"
            options={[
              { value: '', label: 'Select Subject & Class' },
              ...teacherAssignments.map(assignment => ({
                value: assignment.id.toString(),
                label: `${assignment.subject?.name || assignment.subjectName} - ${assignment.form} ${assignment.section}`
              }))
            ]}
            value={selectedAssignment?.id?.toString() || ''}
            onChange={(e) => handleAssignmentChange(e.target.value)}
            disabled={!!initialData}
          />
        </div>
      )}

      {/* Step 4: Student (only if subject is selected) */}
      {selectedAssignment && (
        <div>
          <SearchableSelect
            label="Student *"
            options={filteredStudents.map(student => {
              // Get assessment status for this student
              const subjectId = selectedAssignment.subject?.id || (selectedAssignment as any)?.subjectId;
              const studentAssessments = existingAssessments.filter((assessment: any) => 
                assessment.studentId === student.id &&
                assessment.subjectId === subjectId &&
                assessment.term === selectedTerm &&
                assessment.academicYear === selectedYear
              );
              
              const completedTypes = studentAssessments.map((assessment: any) => assessment.type);
              const hasCoursework = completedTypes.includes(AssessmentType.COURSEWORK);
              const hasFinalExam = completedTypes.includes(AssessmentType.FINAL_EXAM);
              
              let statusText = '';
              if (hasCoursework && hasFinalExam) {
                statusText = '✅ All assessments completed';
              } else if (hasCoursework) {
                statusText = '📝 Coursework done, Final Exam pending';
              } else if (hasFinalExam) {
                statusText = '📝 Final Exam done, Coursework pending';
              } else {
                statusText = '📝 No assessments recorded';
              }

              return {
                value: student.id.toString(),
                label: `${student.firstName} ${student.lastName}`,
                subtitle: `Student ID: ${student.studentId} | ${student.form} ${student.section} | ${statusText}`
              };
            })}
            value={selectedStudent?.id?.toString() || ''}
            onChange={handleStudentChange}
            placeholder={filteredStudents.length > 0 ? 'Search and select a student...' : 'No available students'}
            disabled={!selectedAssignment || filteredStudents.length === 0}
            searchPlaceholder="Search by name or student ID..."
            maxHeight="300px"
            showSearch={filteredStudents.length > 5}
          />
          {filteredStudents.length === 0 && students.length > 0 && (
            <p className="text-sm text-amber-600 mt-1">
              All students have completed their assessments for this subject, term, and year.
            </p>
          )}
          {filteredStudents.length > 0 && (
            <p className="text-sm text-gray-600 mt-1">
              {filteredStudents.length} student{filteredStudents.length !== 1 ? 's' : ''} available for assessment
            </p>
          )}
        </div>
      )}

      {/* Assessment Details (only if student is selected) */}
      {selectedStudent && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <Input
                label="Assessment Title *"
                placeholder="Enter assessment title"
                error={errors.title?.message}
                {...register('title', {
                  required: 'Assessment title is required',
                  minLength: {
                    value: 3,
                    message: 'Title must be at least 3 characters'
                  }
                })}
              />
            </div>

            <div>
              <Input
                label="Date *"
                type="date"
                error={errors.date?.message}
                {...register('date', {
                  required: 'Date is required'
                })}
              />
            </div>

            <div>
              <Input
                label="Score *"
                type="number"
                placeholder="Enter score"
                error={errors.score?.message}
                {...register('score', {
                  required: 'Score is required',
                  min: {
                    value: 0,
                    message: 'Score cannot be negative'
                  },
                  valueAsNumber: true
                })}
              />
            </div>

            <div>
              <Input
                label="Maximum Score *"
                type="number"
                placeholder="Enter maximum score"
                error={errors.maxScore?.message}
                {...register('maxScore', {
                  required: 'Maximum score is required',
                  min: {
                    value: 1,
                    message: 'Maximum score must be at least 1'
                  },
                  valueAsNumber: true
                })}
              />
            </div>

            <div>
              <Select
                label="Assessment Type *"
                options={getAvailableAssessmentTypes()}
                error={errors.type?.message}
                {...register('type', {
                  required: 'Assessment type is required'
                })}
              />
              {(() => {
                const status = getStudentAssessmentStatus();
                if (status.allCompleted) {
                  return (
                    <p className="text-sm text-green-600 mt-1">
                      ✅ All assessment types have been completed for this student, subject, term, and year.
                    </p>
                  );
                } else if (status.hasAssessments) {
                  const completedTypes = status.completedTypes.map(type => formatAssessmentType(type)).join(', ');
                  return (
                    <p className="text-sm text-blue-600 mt-1">
                      📝 Already completed: {completedTypes}. Only remaining types are shown above.
                    </p>
                  );
                } else {
                  return (
                    <p className="text-sm text-gray-600 mt-1">
                      📝 No assessments recorded yet. Both Coursework and Final Exam are available.
                    </p>
                  );
                }
              })()}
            </div>
          </div>

          {/* Hidden fields for form submission */}
          <input type="hidden" {...register('academicYear')} />
          <input type="hidden" {...register('term')} />
          <input type="hidden" {...register('studentId')} />
          <input type="hidden" {...register('subjectId')} />
        </>
      )}

      <div className="flex justify-end space-x-3">
        <Button
          type="button"
          variant="secondary"
          onClick={onCancel}
          disabled={isLoading}
        >
          Cancel
        </Button>
        <Button
          type="submit"
          loading={isLoading || isSubmitting}
          disabled={isSubmitting || !selectedStudent}
        >
          {initialData ? 'Update Assessment' : 'Create Assessment'}
        </Button>
      </div>
    </form>
  );
};

export default AssessmentForm;
