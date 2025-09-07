import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft, FileText, User } from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import StudentReports from '../../components/parent/StudentReports';
import { parentService } from '../../services/parentService';
import { toast } from 'react-hot-toast';

const ParentReportsPage: React.FC = () => {
  const navigate = useNavigate();
  const { childId } = useParams();
  const [studentName, setStudentName] = useState<string>('');
  const [canAccessReports, setCanAccessReports] = useState<boolean>(false);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (childId) {
      loadStudentInfo();
    }
  }, [childId]);

  const loadStudentInfo = async () => {
    try {
      setLoading(true);
      // Get student finance status to check if fees are paid
      const financeStatus = await parentService.checkFeesStatus(parseInt(childId!));
      
      // Extract student name from finance status or use a default
      const name = financeStatus.studentName || `Student ${childId}`;
      setStudentName(name);
      
      // Check if fees are paid (no outstanding balance)
      const feesPaid = financeStatus.isFeesPaid || false;
      setCanAccessReports(feesPaid);
      
    } catch (error) {
      console.error('Error loading student info:', error);
      toast.error('Failed to load student information');
      setStudentName(`Student ${childId}`);
      setCanAccessReports(false);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading student information...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center">
              <Button
                variant="ghost"
                size="sm"
                onClick={() => navigate('/parent/dashboard')}
                className="mr-4"
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Dashboard
              </Button>
              <FileText className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <h1 className="text-xl font-semibold text-gray-900">Student Reports</h1>
                <p className="text-sm text-gray-600">{studentName}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {!canAccessReports ? (
          <Card className="p-8 text-center">
            <User className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">Access Restricted</h2>
            <p className="text-gray-600 mb-4">
              Please settle all outstanding fees to view {studentName}'s academic reports.
            </p>
            <Button onClick={() => navigate('/parent/dashboard')}>Back to Dashboard</Button>
          </Card>
        ) : (
          <StudentReports 
            studentId={parseInt(childId!)}
            studentName={studentName}
            canAccessReports={canAccessReports}
          />
        )}
      </div>
    </div>
  );
};

export default ParentReportsPage;
