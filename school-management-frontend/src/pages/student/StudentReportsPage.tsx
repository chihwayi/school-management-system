import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Card, Button, Badge } from '../../components/ui';
import { ArrowLeft, FileText, Download, Eye, Lock, CheckCircle } from 'lucide-react';
import { toast } from 'react-hot-toast';

interface Report {
  id: number;
  title: string;
  term: string;
  academicYear: string;
  overallGrade: string;
  isPublished: boolean;
  canAccess: boolean;
}

const StudentReportsPage: React.FC = () => {
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

  const { data: reports, isLoading, error } = useQuery({
    queryKey: ['student-reports', mobileNumber],
    queryFn: async () => {
      if (!mobileNumber) return [];
      const response = await fetch(`/api/student/reports?mobileNumber=${encodeURIComponent(mobileNumber)}`);
      if (!response.ok) {
        throw new Error('Failed to fetch reports');
      }
      return response.json();
    },
    enabled: !!mobileNumber
  });

  const availableReports = reports?.filter((r: Report) => r.canAccess) || [];
  const publishedReports = reports?.filter((r: Report) => r.isPublished) || [];

  if (error) {
    toast.error('Failed to load reports');
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
          <h1 className="text-3xl font-bold text-gray-900">My Reports</h1>
          <p className="text-gray-600 mt-2">View your academic reports and progress</p>
        </div>

        {/* Report Stats */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <FileText className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Total Reports</p>
                <p className="text-2xl font-bold text-blue-600">{reports?.length || 0}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <CheckCircle className="h-8 w-8 text-green-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Published</p>
                <p className="text-2xl font-bold text-green-600">{publishedReports.length}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <Eye className="h-8 w-8 text-purple-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Available</p>
                <p className="text-2xl font-bold text-purple-600">{availableReports.length}</p>
              </div>
            </div>
          </Card>
        </div>

        {/* Reports List */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Academic Reports</h2>
            <div className="text-sm text-gray-600">
              {availableReports.length} of {reports?.length || 0} reports available
            </div>
          </div>

          {isLoading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="mt-4 text-gray-600">Loading reports...</p>
            </div>
          ) : reports?.length === 0 ? (
            <div className="text-center py-8">
              <FileText className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No reports available</p>
              <p className="text-sm text-gray-400 mt-2">Reports will appear here when published</p>
            </div>
          ) : (
            <div className="space-y-4">
              {reports?.map((report: Report) => (
                <div key={report.id} className="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
                  <div className="flex items-center justify-between">
                    <div className="flex-1">
                      <div className="flex items-center space-x-3 mb-2">
                        {report.canAccess ? (
                          <FileText className="h-5 w-5 text-blue-600" />
                        ) : (
                          <Lock className="h-5 w-5 text-gray-400" />
                        )}
                        <h3 className="text-lg font-semibold text-gray-900">{report.title}</h3>
                        <div className="flex space-x-2">
                          {report.isPublished && (
                            <Badge variant="success">Published</Badge>
                          )}
                          {!report.isPublished && (
                            <Badge variant="secondary">Draft</Badge>
                          )}
                          {!report.canAccess && (
                            <Badge variant="destructive">Locked</Badge>
                          )}
                        </div>
                      </div>
                      
                      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm text-gray-600">
                        <div>
                          <span className="font-medium">Term:</span> {report.term}
                        </div>
                        <div>
                          <span className="font-medium">Academic Year:</span> {report.academicYear}
                        </div>
                        <div>
                          <span className="font-medium">Overall Grade:</span> {report.overallGrade}
                        </div>
                      </div>
                    </div>
                    
                    <div className="ml-4 flex space-x-2">
                      {report.canAccess ? (
                        <>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              // TODO: Implement report viewing
                              toast('Report viewing feature coming soon!', {
                                icon: '👁️',
                                duration: 3000
                              });
                            }}
                          >
                            <Eye className="h-4 w-4 mr-1" />
                            View
                          </Button>
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => {
                              // TODO: Implement report download
                              toast('Report download feature coming soon!', {
                                icon: '📥',
                                duration: 3000
                              });
                            }}
                          >
                            <Download className="h-4 w-4 mr-1" />
                            Download
                          </Button>
                        </>
                      ) : (
                        <div className="text-center">
                          <Lock className="h-4 w-4 text-gray-400 mx-auto mb-1" />
                          <p className="text-xs text-gray-500">Fees Required</p>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Fee Notice */}
        {reports?.some((r: Report) => !r.canAccess) && (
          <Card className="p-6 mt-6 bg-yellow-50 border-yellow-200">
            <div className="flex items-center">
              <Lock className="h-5 w-5 text-yellow-600 mr-3" />
              <div>
                <h3 className="text-sm font-medium text-yellow-800">Some reports are locked</h3>
                <p className="text-sm text-yellow-700 mt-1">
                  Please ensure all fees are paid to access all reports. Contact the school administration if you have any questions.
                </p>
              </div>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};

export default StudentReportsPage;
