import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button } from '../../components/ui';
import { 
  GraduationCap, 
  FileText, 
  BookOpen, 
  DollarSign, 
  Calendar,
  Clock,
  CheckCircle,
  AlertCircle,
  TrendingUp,
  User
} from 'lucide-react';
import { toast } from 'react-hot-toast';

interface StudentInfo {
  id: number;
  name: string;
  studentId: string;
  form: string;
  section: string;
  level: string;
  whatsappNumber: string;
  feesPaid: boolean;
  balance: number;
}

interface Assignment {
  id: number;
  title: string;
  subject: string;
  dueDate: string;
  status: 'pending' | 'submitted' | 'overdue';
  description: string;
}

interface Report {
  id: number;
  term: string;
  academicYear: string;
  overallGrade: string;
  isPublished: boolean;
  canAccess: boolean;
}

const StudentDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [studentInfo, setStudentInfo] = useState<StudentInfo | null>(null);
  const [assignments, setAssignments] = useState<Assignment[]>([]);
  const [reports, setReports] = useState<Report[]>([]);
  const [financialData, setFinancialData] = useState<any>(null);
  const [subjects, setSubjects] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadStudentData();
  }, []);

  const loadStudentData = async () => {
    try {
      // Get mobile number from localStorage (stored during login)
      const mobileNumber = localStorage.getItem('mobileNumber');
      
      if (!mobileNumber) {
        toast.error('No mobile number found. Please login again.');
        navigate('/student/login');
        return;
      }

      const [studentData, assignmentsData, reportsData, financialData, subjectsData] = await Promise.all([
        fetch(`/api/student/profile?mobileNumber=${encodeURIComponent(mobileNumber)}`).then(res => res.json()),
        fetch(`/api/student/assignments?mobileNumber=${encodeURIComponent(mobileNumber)}`).then(res => res.json()),
        fetch(`/api/student/reports?mobileNumber=${encodeURIComponent(mobileNumber)}`).then(res => res.json()),
        fetch(`/api/student/finance?mobileNumber=${encodeURIComponent(mobileNumber)}`).then(res => res.json()),
        fetch(`/api/student/subjects?mobileNumber=${encodeURIComponent(mobileNumber)}`).then(res => res.json())
      ]);

      setStudentInfo(studentData);
      setAssignments(assignmentsData);
      setReports(reportsData);
      setFinancialData(financialData);
      setSubjects(subjectsData);
    } catch (error) {
      console.error('Error loading student data:', error);
      toast.error('Failed to load student data');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('studentParentToken');
    localStorage.removeItem('userType');
    localStorage.removeItem('mobileNumber');
    navigate('/student/login');
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading your dashboard...</p>
        </div>
      </div>
    );
  }

  if (!studentInfo) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <AlertCircle className="h-12 w-12 text-red-500 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-900 mb-2">Access Denied</h2>
          <p className="text-gray-600 mb-4">You don't have permission to access this dashboard.</p>
          <Button onClick={handleLogout}>Back to Login</Button>
        </div>
      </div>
    );
  }

  const pendingAssignments = assignments.filter(a => a.status === 'pending');
  const overdueAssignments = assignments.filter(a => a.status === 'overdue');
  const availableReports = reports.filter(r => r.canAccess);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center">
              <GraduationCap className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <h1 className="text-xl font-semibold text-gray-900">Student Portal</h1>
                <p className="text-sm text-gray-600">Welcome back, {studentInfo.name}</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <div className="text-right">
                <p className="text-sm font-medium text-gray-900">{studentInfo.studentId}</p>
                <p className="text-xs text-gray-600">{studentInfo.form} {studentInfo.section}</p>
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                Logout
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-blue-100 rounded-lg">
                <BookOpen className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Pending Assignments</p>
                <p className="text-2xl font-bold text-gray-900">{pendingAssignments.length}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-red-100 rounded-lg">
                <AlertCircle className="h-6 w-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Overdue</p>
                <p className="text-2xl font-bold text-gray-900">{overdueAssignments.length}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-green-100 rounded-lg">
                <FileText className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Available Reports</p>
                <p className="text-2xl font-bold text-gray-900">{availableReports.length}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-yellow-100 rounded-lg">
                <DollarSign className="h-6 w-6 text-yellow-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Account Balance</p>
                <p className="text-2xl font-bold text-gray-900">
                  ${studentInfo.balance.toFixed(2)}
                </p>
              </div>
            </div>
          </Card>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* Recent Assignments */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Recent Assignments</h2>
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => navigate('/student/assignments')}
              >
                View All
              </Button>
            </div>
            
            {assignments.length === 0 ? (
              <p className="text-gray-500 text-center py-4">No assignments available</p>
            ) : (
              <div className="space-y-3">
                {assignments.slice(0, 5).map((assignment) => (
                  <div key={assignment.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div className="flex-1">
                      <h3 className="font-medium text-gray-900">{assignment.title}</h3>
                      <p className="text-sm text-gray-600">{assignment.subject}</p>
                      <div className="flex items-center mt-1">
                        <Calendar className="h-3 w-3 text-gray-400 mr-1" />
                        <span className="text-xs text-gray-500">
                          Due: {new Date(assignment.dueDate).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                    <div className="flex items-center space-x-2">
                      {assignment.status === 'pending' && (
                        <span className="px-2 py-1 text-xs bg-yellow-100 text-yellow-800 rounded-full">
                          Pending
                        </span>
                      )}
                      {assignment.status === 'overdue' && (
                        <span className="px-2 py-1 text-xs bg-red-100 text-red-800 rounded-full">
                          Overdue
                        </span>
                      )}
                      {assignment.status === 'submitted' && (
                        <span className="px-2 py-1 text-xs bg-green-100 text-green-800 rounded-full">
                          Submitted
                        </span>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </Card>

          {/* Available Reports */}
          <Card className="p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Available Reports</h2>
              <Button 
                variant="outline" 
                size="sm"
                onClick={() => navigate('/student/reports')}
              >
                View All
              </Button>
            </div>
            
            {availableReports.length === 0 ? (
              <div className="text-center py-4">
                <FileText className="h-12 w-12 text-gray-300 mx-auto mb-2" />
                <p className="text-gray-500">No reports available</p>
                {!studentInfo.feesPaid && (
                  <p className="text-sm text-red-500 mt-2">
                    Please pay your fees to access reports
                  </p>
                )}
              </div>
            ) : (
              <div className="space-y-3">
                {availableReports.slice(0, 5).map((report) => (
                  <div key={report.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                    <div>
                      <h3 className="font-medium text-gray-900">
                        {report.term} Report - {report.academicYear}
                      </h3>
                      <p className="text-sm text-gray-600">
                        Overall Grade: {report.overallGrade}
                      </p>
                    </div>
                    <Button 
                      size="sm"
                      onClick={() => navigate(`/student/reports/${report.id}`)}
                    >
                      View
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </Card>
        </div>

        {/* Financial Summary */}
        {financialData && (
          <Card className="p-6 mt-8">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Financial Summary</h2>
              <div className="flex items-center space-x-2">
                <span className={`px-3 py-1 text-sm rounded-full ${
                  financialData.isFeesPaid 
                    ? 'bg-green-100 text-green-800' 
                    : 'bg-red-100 text-red-800'
                }`}>
                  {financialData.isFeesPaid ? 'Fees Paid' : 'Outstanding Balance'}
                </span>
              </div>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
              <div className="text-center p-4 bg-green-50 rounded-lg">
                <p className="text-sm font-medium text-gray-600">Total Paid</p>
                <p className="text-2xl font-bold text-green-600">${financialData.totalPaid.toFixed(2)}</p>
              </div>
              
              <div className="text-center p-4 bg-red-50 rounded-lg">
                <p className="text-sm font-medium text-gray-600">Outstanding Balance</p>
                <p className="text-2xl font-bold text-red-600">${financialData.totalBalance.toFixed(2)}</p>
              </div>
              
              <div className="text-center p-4 bg-blue-50 rounded-lg">
                <p className="text-sm font-medium text-gray-600">Payment History</p>
                <p className="text-2xl font-bold text-blue-600">{financialData.paymentHistory.length}</p>
              </div>
            </div>

            {/* Recent Payments */}
            <div>
              <h3 className="text-md font-semibold text-gray-900 mb-3">Recent Payments</h3>
              {financialData.paymentHistory.length === 0 ? (
                <p className="text-gray-500 text-center py-4">No payment history available</p>
              ) : (
                <div className="space-y-3">
                  {financialData.paymentHistory.slice(0, 5).map((payment: any) => (
                    <div key={payment.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div className="flex-1">
                        <h4 className="font-medium text-gray-900">
                          {payment.term} - {payment.month} {payment.academicYear}
                        </h4>
                        <p className="text-sm text-gray-600">
                          Monthly Fee: ${payment.monthlyFeeAmount.toFixed(2)}
                        </p>
                        <div className="flex items-center mt-1">
                          <Calendar className="h-3 w-3 text-gray-400 mr-1" />
                          <span className="text-xs text-gray-500">
                            Paid: {new Date(payment.paymentDate).toLocaleDateString()}
                          </span>
                        </div>
                      </div>
                      <div className="flex items-center space-x-2">
                        <div className="text-right">
                          <p className="text-sm font-medium text-gray-900">
                            ${payment.amountPaid.toFixed(2)}
                          </p>
                          <p className="text-xs text-gray-500">
                            Balance: ${payment.balance.toFixed(2)}
                          </p>
                        </div>
                        <span className={`px-2 py-1 text-xs rounded-full ${
                          payment.paymentStatus === 'FULL_PAYMENT' 
                            ? 'bg-green-100 text-green-800'
                            : payment.paymentStatus === 'PART_PAYMENT'
                            ? 'bg-yellow-100 text-yellow-800'
                            : 'bg-red-100 text-red-800'
                        }`}>
                          {payment.paymentStatus.replace('_', ' ')}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </Card>
        )}

        {/* Subjects */}
        <Card className="p-6 mt-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">My Subjects</h2>
            <div className="text-sm text-gray-600">
              {subjects.length} subjects enrolled
            </div>
          </div>
          
          {subjects.length === 0 ? (
            <div className="text-center py-8">
              <BookOpen className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No subjects enrolled</p>
              <p className="text-sm text-gray-400 mt-2">Contact your class teacher to enroll in subjects</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {subjects.map((subject: any) => (
                <div key={subject.id} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                  <div className="flex items-start justify-between mb-2">
                    <div className="flex-1">
                      <h3 className="font-semibold text-gray-900">{subject.name}</h3>
                      <p className="text-sm text-gray-600">{subject.code}</p>
                    </div>
                    <span className="px-2 py-1 text-xs bg-blue-100 text-blue-800 rounded-full">
                      {subject.level.replace('_', ' ')}
                    </span>
                  </div>
                  
                  {subject.description && (
                    <p className="text-sm text-gray-600 mb-3">
                      {subject.description}
                    </p>
                  )}
                  
                  <div className="flex items-center justify-between text-xs text-gray-500">
                    <span>Subject ID: {subject.id}</span>
                    <span className="px-2 py-1 bg-gray-100 rounded">
                      Active
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </Card>

        {/* Quick Actions */}
        <Card className="p-6 mt-8">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => navigate('/student/assignments')}
            >
              <BookOpen className="h-6 w-6 mb-2" />
              <span>View Assignments</span>
            </Button>
            
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => navigate('/student/reports')}
              disabled={!studentInfo.feesPaid}
            >
              <FileText className="h-6 w-6 mb-2" />
              <span>View Reports</span>
            </Button>
            
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => navigate('/student/finance')}
            >
              <DollarSign className="h-6 w-6 mb-2" />
              <span>Financial Statement</span>
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default StudentDashboard;
