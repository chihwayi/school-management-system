import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { 
  ArrowLeft, 
  CreditCard, 
  DollarSign, 
  Calendar, 
  CheckCircle, 
  AlertCircle,
  TrendingUp,
  TrendingDown,
  Receipt
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { parentService } from '../../services/parentService';
import { toast } from 'react-hot-toast';

interface PaymentRecord {
  id: number;
  date: string;
  amount: number;
  academicYear: string;
  month: string;
  term: string;
  status: string;
}

interface StudentFinanceStatus {
  studentId: number;
  studentName: string;
  totalPaid: number;
  totalBalance: number;
  isFeesPaid: boolean;
  paymentHistory: PaymentRecord[];
}

const ParentFinancePage: React.FC = () => {
  const navigate = useNavigate();
  const { childId } = useParams();
  const [financeData, setFinanceData] = useState<StudentFinanceStatus | null>(null);
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    if (childId) {
      loadFinanceData();
    }
  }, [childId]);

  const loadFinanceData = async () => {
    try {
      setLoading(true);
      const data = await parentService.checkFeesStatus(parseInt(childId!));
      setFinanceData(data);
    } catch (error) {
      console.error('Error loading finance data:', error);
      toast.error('Failed to load financial information');
    } finally {
      setLoading(false);
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'FULL_PAYMENT':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      case 'PARTIAL_PAYMENT':
        return <AlertCircle className="h-4 w-4 text-yellow-600" />;
      default:
        return <Receipt className="h-4 w-4 text-gray-600" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'FULL_PAYMENT':
        return 'bg-green-100 text-green-800';
      case 'PARTIAL_PAYMENT':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading financial information...</p>
        </div>
      </div>
    );
  }

  if (!financeData) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <Card className="p-8 text-center">
            <AlertCircle className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">Unable to Load Financial Data</h2>
            <p className="text-gray-600 mb-4">There was an error loading the financial information.</p>
            <Button onClick={() => navigate('/parent/dashboard')}>Back to Dashboard</Button>
          </Card>
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
              <CreditCard className="h-8 w-8 text-blue-600 mr-3" />
              <div>
                <h1 className="text-xl font-semibold text-gray-900">Financial Details</h1>
                <p className="text-sm text-gray-600">{financeData.studentName}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Financial Summary Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <div className="p-2 bg-green-100 rounded-lg">
                <DollarSign className="h-6 w-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Total Paid</p>
                <p className="text-2xl font-bold text-green-600">{formatCurrency(financeData.totalPaid)}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className={`p-2 rounded-lg ${financeData.totalBalance > 0 ? 'bg-red-100' : 'bg-green-100'}`}>
                {financeData.totalBalance > 0 ? (
                  <TrendingDown className="h-6 w-6 text-red-600" />
                ) : (
                  <TrendingUp className="h-6 w-6 text-green-600" />
                )}
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Outstanding Balance</p>
                <p className={`text-2xl font-bold ${financeData.totalBalance > 0 ? 'text-red-600' : 'text-green-600'}`}>
                  {formatCurrency(financeData.totalBalance)}
                </p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <div className={`p-2 rounded-lg ${financeData.isFeesPaid ? 'bg-green-100' : 'bg-yellow-100'}`}>
                {financeData.isFeesPaid ? (
                  <CheckCircle className="h-6 w-6 text-green-600" />
                ) : (
                  <AlertCircle className="h-6 w-6 text-yellow-600" />
                )}
              </div>
              <div className="ml-4">
                <p className="text-sm font-medium text-gray-600">Payment Status</p>
                <p className={`text-2xl font-bold ${financeData.isFeesPaid ? 'text-green-600' : 'text-yellow-600'}`}>
                  {financeData.isFeesPaid ? 'Fully Paid' : 'Outstanding'}
                </p>
              </div>
            </div>
          </Card>
        </div>

        {/* Payment History */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Payment History</h2>
            <div className="text-sm text-gray-600">
              {financeData.paymentHistory.length} payment(s) recorded
            </div>
          </div>

          {financeData.paymentHistory.length === 0 ? (
            <div className="text-center py-8">
              <Receipt className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No payment history available</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Amount
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Academic Year
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Term
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {financeData.paymentHistory.map((payment) => (
                    <tr key={payment.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {formatDate(payment.date)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {formatCurrency(payment.amount)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {payment.academicYear}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {payment.term}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          {getStatusIcon(payment.status)}
                          <span className={`ml-2 px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(payment.status)}`}>
                            {payment.status.replace('_', ' ')}
                          </span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default ParentFinancePage;
