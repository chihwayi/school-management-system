import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Select } from '../../components/ui';
import { 
  Users, 
  FileText, 
  DollarSign, 
  GraduationCap,
  Calendar,
  TrendingUp,
  AlertCircle,
  CheckCircle,
  User
} from 'lucide-react';
import { toast } from 'react-hot-toast';

interface ParentInfo {
  id: number;
  name: string;
  whatsappNumber: string;
  children: ChildInfo[];
}

interface ChildInfo {
  id: number;
  name: string;
  studentId: string;
  form: string;
  section: string;
  level: string;
  balance: number;
  feesPaid: boolean;
  reports: Report[];
}

interface Report {
  id: number;
  term: string;
  academicYear: string;
  overallGrade: string;
  isPublished: boolean;
  canAccess: boolean;
}

const ParentDashboard: React.FC = () => {
  const navigate = useNavigate();
  const [parentInfo, setParentInfo] = useState<ParentInfo | null>(null);
  const [children, setChildren] = useState<ChildInfo[]>([]);
  const [selectedChild, setSelectedChild] = useState<ChildInfo | null>(null);
  const [childrenFinancialData, setChildrenFinancialData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadParentData();
  }, []);

  const loadParentData = async () => {
    try {
      // Get mobile number from localStorage (stored during login)
      const mobileNumber = localStorage.getItem('mobileNumber');
      
      if (!mobileNumber) {
        toast.error('No mobile number found. Please login again.');
        navigate('/parent/login');
        return;
      }

      const [profileResponse, childrenResponse] = await Promise.all([
        fetch(`/api/parent/profile?mobileNumber=${encodeURIComponent(mobileNumber)}`),
        fetch(`/api/parent/children?mobileNumber=${encodeURIComponent(mobileNumber)}`)
      ]);
      
      const profileData = await profileResponse.json();
      const childrenData = await childrenResponse.json();
      
      setParentInfo(profileData);
      setChildren(childrenData || []);
      
      if (childrenData && childrenData.length > 0) {
        setSelectedChild(childrenData[0]);
        
        // Load financial data for each child
        const financialPromises = childrenData.map((child: any) => {
          if (!child.whatsappNumber) {
            console.warn(`No WhatsApp number for child ${child.name}`);
            return Promise.resolve(null);
          }
          return fetch(`/api/student/finance?mobileNumber=${encodeURIComponent(child.whatsappNumber)}`)
            .then(res => res.json())
            .catch((error) => {
              console.error(`Error fetching financial data for ${child.name}:`, error);
              return null;
            });
        });
        
        const financialData = await Promise.all(financialPromises);
        setChildrenFinancialData(financialData.filter(data => data !== null));
      }
    } catch (error) {
      console.error('Error loading parent data:', error);
      toast.error('Failed to load parent data');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('studentParentToken');
    localStorage.removeItem('userType');
    localStorage.removeItem('mobileNumber');
    navigate('/parent/login');
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

  if (!parentInfo) {
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

  const totalBalance = childrenFinancialData.reduce((sum, data) => sum + (data?.totalBalance || 0), 0);
  const childrenWithUnpaidFees = childrenFinancialData.filter(data => data && data.totalBalance > 0);
  // Mock reports data - in a real app, this would come from an API
  const availableReports = selectedChild ? [
    {
      id: 1,
      term: "Term 1",
      academicYear: "2024",
      overallGrade: "A",
      canAccess: true
    },
    {
      id: 2,
      term: "Term 2", 
      academicYear: "2024",
      overallGrade: "B+",
      canAccess: true
    }
  ] : [];

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center">
              <Users className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <h1 className="text-xl font-semibold text-gray-900">Parent Portal</h1>
                <p className="text-sm text-gray-600">Welcome back, {parentInfo.name}</p>
              </div>
            </div>
            <div className="flex items-center space-x-4">
              <div className="text-right">
                <p className="text-sm font-medium text-gray-900">{children.length} Children</p>
                <p className="text-xs text-gray-600">Registered</p>
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                Logout
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Child Selector */}
        {children.length > 1 && (
          <Card className="p-6 mb-8">
            <div className="flex items-center justify-between">
              <h2 className="text-lg font-semibold text-gray-900">Select Child</h2>
              <Select
                value={selectedChild?.id?.toString() || ''}
                onChange={(e) => {
                  const child = children.find(c => c.id?.toString() === e.target.value);
                  setSelectedChild(child || null);
                }}
                options={children.map(child => ({
                  value: child.id?.toString() || '',
                  label: `${child.name} (${child.form} ${child.section})`
                }))}
                className="w-64"
              />
            </div>
          </Card>
        )}

        {/* Quick Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-blue-100 rounded-lg">
                <GraduationCap className="h-6 w-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Children</p>
                <p className="text-2xl font-bold text-gray-900">{children.length}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-red-100 rounded-lg">
                <AlertCircle className="h-6 w-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Unpaid Fees</p>
                <p className="text-2xl font-bold text-gray-900">{childrenWithUnpaidFees.length}</p>
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
                <p className="text-sm font-medium text-gray-600">Total Balance</p>
                <p className="text-2xl font-bold text-gray-900">
                  ${totalBalance.toFixed(2)}
                </p>
              </div>
            </div>
          </Card>
        </div>

        {selectedChild && (
          <>
            {/* Selected Child Info */}
            <Card className="p-6 mb-8">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">{selectedChild.name}</h2>
                  <p className="text-gray-600">
                    {selectedChild.studentId} • {selectedChild.form} {selectedChild.section} • {selectedChild.level}
                  </p>
                </div>
                <div className="text-right">
                  <p className="text-sm text-gray-600">Account Balance</p>
                  {(() => {
                    const childFinancialData = childrenFinancialData.find(data => data?.studentId === selectedChild.id);
                    const balance = childFinancialData?.totalBalance || 0;
                    const isFeesPaid = childFinancialData?.isFeesPaid || false;
                    
                    return (
                      <>
                        <p className={`text-lg font-bold ${balance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                          ${balance.toFixed(2)}
                        </p>
                        {isFeesPaid && (
                          <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
                            <CheckCircle className="h-3 w-3 mr-1" />
                            Fees Paid
                          </span>
                        )}
                      </>
                    );
                  })()}
                </div>
              </div>
            </Card>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Available Reports */}
              <Card className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-semibold text-gray-900">Available Reports</h2>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => navigate(`/parent/reports/${selectedChild.id}`)}
                  >
                    View All
                  </Button>
                </div>
                
                {availableReports.length === 0 ? (
                  <div className="text-center py-4">
                    <FileText className="h-12 w-12 text-gray-300 mx-auto mb-2" />
                    <p className="text-gray-500">No reports available</p>
                    {(() => {
                      const childFinancialData = childrenFinancialData.find(data => data?.studentId === selectedChild.id);
                      const isFeesPaid = childFinancialData?.isFeesPaid || false;
                      
                      return !isFeesPaid ? (
                        <p className="text-sm text-red-500 mt-2">
                          Please pay fees to access reports
                        </p>
                      ) : null;
                    })()}
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
                          onClick={() => navigate(`/parent/reports/${selectedChild.id}/${report.id}`)}
                        >
                          View
                        </Button>
                      </div>
                    ))}
                  </div>
                )}
              </Card>

              {/* Financial Summary */}
              <Card className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <h2 className="text-lg font-semibold text-gray-900">Financial Summary</h2>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => navigate(`/parent/finance/${selectedChild.id}`)}
                  >
                    View Details
                  </Button>
                </div>
                
                <div className="space-y-4">
                  {(() => {
                    const childFinancialData = childrenFinancialData.find(data => data?.studentId === selectedChild.id);
                    const balance = childFinancialData?.totalBalance || 0;
                    const isFeesPaid = childFinancialData?.isFeesPaid || false;
                    
                    return (
                      <>
                        <div className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                          <span className="text-gray-600">Current Balance</span>
                          <span className={`font-semibold ${balance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                            ${balance.toFixed(2)}
                          </span>
                        </div>
                        
                        <div className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                          <span className="text-gray-600">Fee Status</span>
                          <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${
                            isFeesPaid 
                              ? 'bg-green-100 text-green-800' 
                              : 'bg-red-100 text-red-800'
                          }`}>
                            {isFeesPaid ? (
                              <>
                                <CheckCircle className="h-3 w-3 mr-1" />
                                Paid
                              </>
                            ) : (
                              <>
                                <AlertCircle className="h-3 w-3 mr-1" />
                                Unpaid
                              </>
                            )}
                          </span>
                        </div>
                        
                        {balance > 0 && (
                          <div className="p-3 bg-red-50 border border-red-200 rounded-lg">
                            <p className="text-sm text-red-700">
                              <AlertCircle className="h-4 w-4 inline mr-1" />
                              Outstanding balance of ${balance.toFixed(2)}. 
                              Please settle to access reports.
                            </p>
                          </div>
                        )}
                      </>
                    );
                  })()}
                </div>
              </Card>
            </div>
          </>
        )}

        {/* Quick Actions */}
        <Card className="p-6 mt-8">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => navigate('/parent/children')}
            >
              <Users className="h-6 w-6 mb-2" />
              <span>View All Children</span>
            </Button>
            
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => selectedChild && navigate(`/parent/reports/${selectedChild.id}`)}
              disabled={!selectedChild}
            >
              <FileText className="h-6 w-6 mb-2" />
              <span>View Reports</span>
            </Button>
            
            <Button
              variant="outline"
              className="h-16 flex flex-col items-center justify-center"
              onClick={() => selectedChild && navigate(`/parent/finance/${selectedChild.id}`)}
              disabled={!selectedChild}
            >
              <DollarSign className="h-6 w-6 mb-2" />
              <span>Financial Details</span>
            </Button>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default ParentDashboard;
