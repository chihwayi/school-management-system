import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'react-hot-toast';
import { Button, Input, Select } from '../ui';
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
  studentSubjectId = 0,
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

  // Load existing assessments when all filters are set
  useEffect(() => {
    if (selectedAssignment && selectedTerm && selectedYear) {
      loadExistingAssessments();
    }
  }, [selectedAssignment, selectedTerm, selectedYear]);

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
      const subjectId = selectedAssignment.subject?.id;
      if (!subjectId) return;

      console.log('Loading existing assessments for:', {
        subjectId,
        term: selectedTerm,
        year: selectedYear
      });

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
      
      console.log('Loaded existing assessments:', allAssessments);
      setExistingAssessments(allAssessments);
    } catch (error) {
      console.error('Error loading existing assessments:', error);
    }
  };

  const filterAvailableStudents = () => {
    const subjectId = selectedAssignment.subject?.id;
    
    console.log('Filtering students with:', {
      totalStudents: students.length,
      existingAssessments: existingAssessments.length,
      watchedType,
      selectedTerm,
      selectedYear
    });
    
    const availableStudents = students.filter(student => {
      // Get all assessments for this student, subject, term, and year
      const studentAssessments = existingAssessments.filter((assessment: any) => 
        assessment.studentId === student.id &&
        assessment.subjectId === subjectId &&
        assessment.term === selectedTerm &&
        assessment.academicYear === selectedYear
      );
      
      console.log(`Student ${student.firstName} ${student.lastName} has ${studentAssessments.length} assessments:`, studentAssessments);
      
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
        console.log(`Student ${student.firstName} ${student.lastName}: HIDDEN - Completed all assessment types`);
        return false;
      }
      
      // If an assessment type is selected, check if student has completed that specific type
      if (watchedType) {
        const hasCompletedCurrentType = studentAssessments.some((assessment: any) => 
          assessment.type === watchedType
        );
        
        if (hasCompletedCurrentType) {
          console.log(`Student ${student.firstName} ${student.lastName}: HIDDEN - Already completed ${watchedType}`);
          return false;
        }
      }
      
      // Student is available
      console.log(`Student ${student.firstName} ${student.lastName}: AVAILABLE - Can record assessment`);
      return true;
    });
    
    console.log('Filtering results:', {
      totalStudents: students.length,
      availableStudents: availableStudents.length
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
      const subjectId = selectedAssignment.subject?.id;
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

    const subjectId = selectedAssignment.subject?.id;
    const studentAssessments = existingAssessments.filter((assessment: any) => 
      assessment.studentId === selectedStudent.id &&
      assessment.subjectId === subjectId &&
      assessment.term === selectedTerm &&
      assessment.academicYear === selectedYear
    );

    const completedTypes = studentAssessments.map((assessment: any) => assessment.type);
    
    return [
      { value: AssessmentType.COURSEWORK, label: formatAssessmentType(AssessmentType.COURSEWORK) },
      { value: AssessmentType.FINAL_EXAM, label: formatAssessmentType(AssessmentType.FINAL_EXAM) }
    ].filter(option => !completedTypes.includes(option.value));
  };

  const [isSubmitting, setIsSubmitting] = useState(false);
  
  const handleFormSubmit = async (data: AssessmentDTO) => {
    if (isSubmitting) return;
    
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
          <Select
            label="Student *"
            options={[
              { value: '', label: filteredStudents.length > 0 ? 'Select Student' : 'No available students' },
              ...filteredStudents.map(student => ({
                value: student.id.toString(),
                label: `${student.firstName} ${student.lastName} (${student.studentId})`
              }))
            ]}
            value={selectedStudent?.id?.toString() || ''}
            onChange={(e) => handleStudentChange(e.target.value)}
            disabled={!selectedAssignment || filteredStudents.length === 0}
          />
          {filteredStudents.length === 0 && students.length > 0 && (
            <p className="text-sm text-amber-600 mt-1">
              All students have completed their assessments for this subject, term, and year.
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
