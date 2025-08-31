import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-hot-toast';
import { 
  Users, 
  GraduationCap, 
  DollarSign, 
  FileText, 
  AlertCircle, 
  CheckCircle,
  ArrowLeft,
  Eye,
  CreditCard
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';

interface ChildInfo {
  id: number;
  name: string;
  studentId: string;
  form: string;
  section: string;
  level: string;
  whatsappNumber: string;
  relationship: string;
  isPrimaryGuardian: boolean;
}

interface FinancialData {
  studentId: number;
  totalPaid: number;
  totalBalance: number;
  studentName: string;
  paymentHistory: any[];
  isFeesPaid: boolean;
}

const ParentChildrenPage: React.FC = () => {
  const navigate = useNavigate();
  const [children, setChildren] = useState<ChildInfo[]>([]);
  const [childrenFinancialData, setChildrenFinancialData] = useState<FinancialData[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadChildrenData();
  }, []);

  const loadChildrenData = async () => {
    try {
      const mobileNumber = localStorage.getItem('mobileNumber');
      
      if (!mobileNumber) {
        toast.error('No mobile number found. Please login again.');
        navigate('/parent/login');
        return;
      }

      const [childrenResponse] = await Promise.all([
        fetch(`/api/parent/children?mobileNumber=${encodeURIComponent(mobileNumber)}`)
      ]);
      
      const childrenData = await childrenResponse.json();
      setChildren(childrenData || []);
      
      if (childrenData && childrenData.length > 0) {
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
      console.error('Error loading children data:', error);
      toast.error('Failed to load children data');
    } finally {
      setLoading(false);
    }
  };

  const getChildFinancialData = (childId: number) => {
    return childrenFinancialData.find(data => data.studentId === childId);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading children data...</p>
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
                onClick={() => navigate('/parent')}
                className="mr-4"
              >
                <ArrowLeft className="h-4 w-4 mr-2" />
                Back to Dashboard
              </Button>
              <Users className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <h1 className="text-xl font-semibold text-gray-900">All Children</h1>
                <p className="text-sm text-gray-600">{children.length} children registered</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {children.length === 0 ? (
          <Card className="p-8 text-center">
            <Users className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">No Children Found</h2>
            <p className="text-gray-600 mb-4">No children are currently registered under your account.</p>
            <Button onClick={() => navigate('/parent')}>Back to Dashboard</Button>
          </Card>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {children.map((child) => {
              const financialData = getChildFinancialData(child.id);
              const balance = financialData?.totalBalance || 0;
              const isFeesPaid = financialData?.isFeesPaid || false;
              
              return (
                <Card key={child.id} className="p-6">
                  <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                      <h2 className="text-xl font-semibold text-gray-900 mb-1">{child.name}</h2>
                      <p className="text-gray-600 mb-2">
                        {child.studentId} • {child.form} {child.section} • {child.level}
                      </p>
                      <p className="text-sm text-gray-500">
                        {child.relationship} {child.isPrimaryGuardian && '(Primary)'}
                      </p>
                    </div>
                    <div className="text-right">
                      <p className="text-sm text-gray-600">Account Balance</p>
                      <p className={`text-lg font-bold ${balance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                        ${balance.toFixed(2)}
                      </p>
                      {isFeesPaid && (
                        <span className="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800 mt-1">
                          <CheckCircle className="h-3 w-3 mr-1" />
                          Fees Paid
                        </span>
                      )}
                    </div>
                  </div>

                  {/* Financial Summary */}
                  <div className="bg-gray-50 rounded-lg p-4 mb-4">
                    <h3 className="text-sm font-medium text-gray-900 mb-2">Financial Summary</h3>
                    <div className="grid grid-cols-2 gap-4 text-sm">
                      <div>
                        <span className="text-gray-600">Total Paid:</span>
                        <span className="ml-2 font-medium">${financialData?.totalPaid?.toFixed(2) || '0.00'}</span>
                      </div>
                      <div>
                        <span className="text-gray-600">Outstanding:</span>
                        <span className={`ml-2 font-medium ${balance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                          ${balance.toFixed(2)}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Quick Actions */}
                  <div className="flex space-x-2">
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => navigate(`/parent/finance/${child.id}`)}
                    >
                      <CreditCard className="h-4 w-4 mr-2" />
                      Finance
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => navigate(`/parent/reports/${child.id}`)}
                      disabled={!isFeesPaid}
                    >
                      <FileText className="h-4 w-4 mr-2" />
                      Reports
                    </Button>
                    <Button
                      variant="outline"
                      size="sm"
                      className="flex-1"
                      onClick={() => navigate(`/parent/profile/${child.id}`)}
                    >
                      <Eye className="h-4 w-4 mr-2" />
                      Profile
                    </Button>
                  </div>

                  {/* Warning for unpaid fees */}
                  {balance > 0 && (
                    <div className="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
                      <p className="text-sm text-red-700">
                        <AlertCircle className="h-4 w-4 inline mr-1" />
                        Outstanding balance of ${balance.toFixed(2)}. Please settle to access reports.
                      </p>
                    </div>
                  )}
                </Card>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};

export default ParentChildrenPage;
