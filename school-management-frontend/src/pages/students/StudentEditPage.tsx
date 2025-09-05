import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ArrowLeft, User } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { studentService } from '../../services/studentService';
import { Card, Button } from '../../components/ui';
import StudentEditForm from '../../components/forms/StudentEditForm';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import type { Student } from '../../types';

const StudentEditPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const studentId = parseInt(id!);

  const { data: student, isLoading, error: fetchError } = useQuery({
    queryKey: ['student', studentId],
    queryFn: () => studentService.getStudentById(studentId),
    enabled: !!studentId,
  });

  const handleSubmit = async (data: Partial<Student>) => {
    if (!student) return;

    setIsSubmitting(true);
    setError(null);

    try {
      await studentService.updateStudent(studentId, data);
      toast.success('Student updated successfully');
      navigate(`/app/students/${studentId}`);
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Failed to update student';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate(`/app/students/${studentId}`);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <LoadingSpinner />
      </div>
    );
  }

  if (fetchError || !student) {
    return (
      <div className="container mx-auto px-4 py-8">
        <Card>
          <div className="p-6 text-center">
            <h2 className="text-xl font-semibold text-red-600 mb-4">Error Loading Student</h2>
            <p className="text-gray-600 mb-4">
              {fetchError ? 'Failed to load student information' : 'Student not found'}
            </p>
            <Button onClick={() => navigate('/app/students')} variant="outline">
              Back to Students
            </Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="mb-6">
        <Button
          onClick={() => navigate(`/app/students/${studentId}`)}
          variant="outline"
          className="mb-4"
        >
          <ArrowLeft className="h-4 w-4 mr-2" />
          Back to Student Details
        </Button>
        
        <div className="flex items-center">
          <User className="h-6 w-6 text-gray-400 mr-2" />
          <h1 className="text-2xl font-bold text-gray-900">
            Edit Student: {student.firstName} {student.lastName}
          </h1>
        </div>
      </div>

      <StudentEditForm
        student={student}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isLoading={isSubmitting}
        error={error || undefined}
      />
    </div>
  );
};

export default StudentEditPage;
