import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Card, Button, Badge } from '../../components/ui';
import { ArrowLeft, BookOpen, Calendar, Clock, CheckCircle, AlertTriangle } from 'lucide-react';
import { toast } from 'react-hot-toast';

interface Assignment {
  id: number;
  title: string;
  subject: string;
  dueDate: string;
  status: string;
  description: string;
}

const StudentAssignmentsPage: React.FC = () => {
  const navigate = useNavigate();
  const [mobileNumber, setMobileNumber] = useState<string>('');

  // Get mobile number from localStorage
  useEffect(() => {
    const storedMobile = localStorage.getItem('mobileNumber');
    if (storedMobile) {
      setMobileNumber(storedMobile);
    } else {
      toast.error('No mobile number found. Please login again.');
      setTimeout(() => navigate('/student/login'), 100);
    }
  }, [navigate]);

  const { data: assignments, isLoading, error } = useQuery({
    queryKey: ['student-assignments', mobileNumber],
    queryFn: async () => {
      if (!mobileNumber) return [];
      const response = await fetch(`/api/student/assignments?mobileNumber=${encodeURIComponent(mobileNumber)}`);
      if (!response.ok) {
        throw new Error('Failed to fetch assignments');
      }
      return response.json();
    },
    enabled: !!mobileNumber
  });

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'pending':
        return <Badge variant="warning">Pending</Badge>;
      case 'overdue':
        return <Badge variant="destructive">Overdue</Badge>;
      case 'submitted':
        return <Badge variant="success">Submitted</Badge>;
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'pending':
        return <Clock className="h-4 w-4 text-yellow-600" />;
      case 'overdue':
        return <AlertTriangle className="h-4 w-4 text-red-600" />;
      case 'submitted':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      default:
        return <BookOpen className="h-4 w-4 text-gray-600" />;
    }
  };

  if (error) {
    toast.error('Failed to load assignments');
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="ghost"
            onClick={() => navigate('/student')}
            className="mb-4"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Dashboard
          </Button>
          <h1 className="text-3xl font-bold text-gray-900">My Assignments</h1>
          <p className="text-gray-600 mt-2">View and manage your academic assignments</p>
        </div>

        {/* Assignment Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <Clock className="h-8 w-8 text-yellow-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Pending</p>
                <p className="text-2xl font-bold text-yellow-600">
                  {assignments?.filter((a: Assignment) => a.status === 'pending').length || 0}
                </p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <AlertTriangle className="h-8 w-8 text-red-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Overdue</p>
                <p className="text-2xl font-bold text-red-600">
                  {assignments?.filter((a: Assignment) => a.status === 'overdue').length || 0}
                </p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <CheckCircle className="h-8 w-8 text-green-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Completed</p>
                <p className="text-2xl font-bold text-green-600">
                  {assignments?.filter((a: Assignment) => a.status === 'submitted').length || 0}
                </p>
              </div>
            </div>
          </Card>
        </div>

        {/* Assignments List */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">All Assignments</h2>
            <div className="text-sm text-gray-600">
              Total: {assignments?.length || 0} assignments
            </div>
          </div>

          {isLoading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading assignments...</p>
            </div>
          ) : assignments?.length === 0 ? (
            <div className="text-center py-8">
              <BookOpen className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No assignments available</p>
              <p className="text-sm text-gray-400 mt-2">Check back later for new assignments</p>
            </div>
          ) : (
            <div className="space-y-4">
              {assignments?.map((assignment: Assignment) => (
                <div key={assignment.id} className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="flex items-center space-x-3 mb-2">
                        {getStatusIcon(assignment.status)}
                        <h3 className="text-lg font-semibold text-gray-900">{assignment.title}</h3>
                        {getStatusBadge(assignment.status)}
                      </div>
                      
                      <p className="text-sm font-medium text-gray-600 mb-2">
                        Subject: {assignment.subject}
                      </p>
                      
                      <p className="text-gray-700 mb-3">
                        {assignment.description}
                      </p>
                      
                      <div className="flex items-center space-x-4 text-sm text-gray-500">
                        <div className="flex items-center">
                          <Calendar className="h-4 w-4 mr-1" />
                          <span>Due: {new Date(assignment.dueDate).toLocaleDateString()}</span>
                        </div>
                        <div className="flex items-center">
                          <Clock className="h-4 w-4 mr-1" />
                          <span>
                            {new Date(assignment.dueDate) > new Date() 
                              ? `${Math.ceil((new Date(assignment.dueDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24))} days remaining`
                              : `${Math.ceil((new Date().getTime() - new Date(assignment.dueDate).getTime()) / (1000 * 60 * 60 * 24))} days overdue`
                            }
                          </span>
                        </div>
                      </div>
                    </div>
                    
                    <div className="ml-4">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => {
                          // TODO: Implement assignment submission
                          toast('Assignment submission feature coming soon!', {
                            icon: '📝',
                            duration: 3000
                          });
                        }}
                        disabled={assignment.status === 'submitted'}
                      >
                        {assignment.status === 'submitted' ? 'Submitted' : 'Submit'}
                      </Button>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default StudentAssignmentsPage;
