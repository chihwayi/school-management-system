import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Card, Button, Badge } from '../../components/ui';
import { ArrowLeft, FileText, Download, Calendar, TrendingUp, BookOpen } from 'lucide-react';
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

interface SubjectGrade {
  subject: string;
  grade: string;
  score: number;
  remarks: string;
}

const StudentReportDetailPage: React.FC = () => {
  const navigate = useNavigate();
  const { reportId } = useParams<{ reportId: string }>();
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

  // Mock report data - in a real app, this would come from an API
  const { data: report, isLoading } = useQuery({
    queryKey: ['student-report-detail', reportId, mobileNumber],
    queryFn: async () => {
      if (!mobileNumber || !reportId) return null;
      
      // For now, return mock data based on the report ID
      const mockReports = [
        {
          id: 1,
          title: "Term 1 Report",
          term: "Term 1",
          academicYear: "2024",
          overallGrade: "A",
          isPublished: true,
          canAccess: true,
          totalScore: 85,
          position: 3,
          totalStudents: 25,
          subjects: [
            { subject: "Mathematics", grade: "A", score: 88, remarks: "Excellent work! Keep it up." },
            { subject: "English", grade: "A-", score: 85, remarks: "Good performance. Continue reading widely." },
            { subject: "Science", grade: "B+", score: 82, remarks: "Good understanding of concepts." }
          ]
        },
        {
          id: 2,
          title: "Term 2 Report",
          term: "Term 2",
          academicYear: "2024",
          overallGrade: "B+",
          isPublished: true,
          canAccess: true,
          totalScore: 78,
          position: 5,
          totalStudents: 25,
          subjects: [
            { subject: "Mathematics", grade: "B+", score: 80, remarks: "Good work. Practice more problem-solving." },
            { subject: "English", grade: "A-", score: 85, remarks: "Excellent improvement in writing skills." },
            { subject: "Science", grade: "B", score: 75, remarks: "Need to focus more on practical work." }
          ]
        },
        {
          id: 3,
          title: "Term 3 Report",
          term: "Term 3",
          academicYear: "2024",
          overallGrade: "A-",
          isPublished: false,
          canAccess: false,
          totalScore: 0,
          position: 0,
          totalStudents: 0,
          subjects: []
        }
      ];

      const foundReport = mockReports.find(r => r.id === parseInt(reportId));
      if (!foundReport) {
        throw new Error('Report not found');
      }
      
      return foundReport;
    },
    enabled: !!mobileNumber && !!reportId
  });

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading report...</p>
        </div>
      </div>
    );
  }

  if (!report) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <FileText className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Report Not Found</h2>
          <p className="text-gray-600 mb-4">The requested report could not be found.</p>
          <Button onClick={() => navigate('/student/reports')}>Back to Reports</Button>
        </div>
      </div>
    );
  }

  if (!report.canAccess) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <FileText className="h-12 w-12 text-yellow-500 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Report Not Available</h2>
          <p className="text-gray-600 mb-4">This report is not yet available or requires fee payment.</p>
          <Button onClick={() => navigate('/student/reports')}>Back to Reports</Button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="ghost"
            onClick={() => navigate('/student/reports')}
            className="mb-4"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Reports
          </Button>
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">{report.title}</h1>
              <p className="text-gray-600 mt-2">
                {report.term} - Academic Year {report.academicYear}
              </p>
            </div>
            <div className="flex space-x-2">
              <Button
                variant="outline"
                onClick={() => {
                  toast('Report download feature coming soon!', {
                    icon: '📥',
                    duration: 3000
                  });
                }}
              >
                <Download className="h-4 w-4 mr-2" />
                Download PDF
              </Button>
            </div>
          </div>
        </div>

        {/* Report Summary */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-blue-100 rounded-lg">
                <TrendingUp className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Overall Grade</p>
                <p className="text-2xl font-bold text-blue-600">{report.overallGrade}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-green-100 rounded-lg">
                <FileText className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Score</p>
                <p className="text-2xl font-bold text-green-600">{report.totalScore}%</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-purple-100 rounded-lg">
                <BookOpen className="h-6 w-6 text-purple-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Position</p>
                <p className="text-2xl font-bold text-purple-600">
                  {report.position}/{report.totalStudents}
                </p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-yellow-100 rounded-lg">
                <Calendar className="h-6 w-6 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Status</p>
                <Badge variant="success">Published</Badge>
              </div>
            </div>
          </Card>
        </div>

        {/* Subject Grades */}
        <Card className="p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-6">Subject Performance</h2>
          
          <div className="space-y-4">
            {report.subjects.map((subject: SubjectGrade, index: number) => (
              <div key={index} className="border border-gray-200 rounded-lg p-4">
                <div className="flex items-center justify-between mb-3">
                  <h3 className="text-lg font-semibold text-gray-900">{subject.subject}</h3>
                  <div className="flex items-center space-x-3">
                    <span className="text-2xl font-bold text-blue-600">{subject.grade}</span>
                    <span className="text-lg font-medium text-gray-600">{subject.score}%</span>
                  </div>
                </div>
                
                <div className="bg-gray-50 rounded-lg p-3">
                  <p className="text-sm text-gray-700">
                    <span className="font-medium">Teacher's Remarks:</span> {subject.remarks}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </Card>

        {/* Class Performance */}
        <Card className="p-6 mt-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">Class Performance</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">Your Position</h3>
              <p className="text-3xl font-bold text-blue-600">
                {report.position}<span className="text-lg text-gray-600">/{report.totalStudents}</span>
              </p>
              <p className="text-sm text-gray-600 mt-1">
                You are in the top {Math.round((report.position / report.totalStudents) * 100)}% of your class
              </p>
            </div>
            
            <div>
              <h3 className="text-lg font-medium text-gray-900 mb-2">Performance Trend</h3>
              <div className="flex items-center space-x-2">
                <TrendingUp className="h-5 w-5 text-green-600" />
                <span className="text-green-600 font-medium">Good Progress</span>
              </div>
              <p className="text-sm text-gray-600 mt-1">
                Keep up the excellent work!
              </p>
            </div>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default StudentReportDetailPage;









