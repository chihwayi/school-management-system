import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Card, Button, Badge } from '../../components/ui';
import { ArrowLeft, DollarSign, Receipt, AlertTriangle, CheckCircle } from 'lucide-react';
import { studentFinanceService } from '../../services/studentFinanceService';
import { getCurrentAcademicYear, getCurrentTerm } from '../../utils';
import { toast } from 'react-hot-toast';
import type { StudentFinanceData, PaymentRecord } from '../../services/studentFinanceService';

const StudentFinancePage: React.FC = () => {
  const navigate = useNavigate();
  const [mobileNumber, setMobileNumber] = useState<string>('');

  // Get mobile number from localStorage
  useEffect(() => {
    const storedMobile = localStorage.getItem('mobileNumber');
    if (storedMobile) {
      setMobileNumber(storedMobile);
    } else {
      toast.error('No mobile number found. Please login again.');
      // Use setTimeout to avoid setState during render
      setTimeout(() => navigate('/student/login'), 100);
    }
  }, [navigate]);

  const { data: financeData, isLoading, error } = useQuery({
    queryKey: ['student-finance', mobileNumber],
    queryFn: () => {
      if (!mobileNumber) return null;
      return studentFinanceService.getStudentFinance(mobileNumber);
    },
    enabled: !!mobileNumber
  });

  const totalPaid = financeData?.totalPaid || 0;
  const totalBalance = financeData?.totalBalance || 0;
  const isFeesPaid = financeData?.isFeesPaid || false;
  const paymentHistory = financeData?.paymentHistory || [];



  if (error) {
    toast.error('Failed to load financial information');
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
          <h1 className="text-3xl font-bold text-gray-900">Financial Statement</h1>
          <p className="text-gray-600 mt-2">View your payment history and outstanding balances</p>
        </div>

        {/* Financial Summary */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <Card className="p-6">
            <div className="flex items-center">
              <DollarSign className="h-8 w-8 text-green-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Total Paid</p>
                <p className="text-2xl font-bold text-green-600">${totalPaid.toFixed(2)}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              <AlertTriangle className="h-8 w-8 text-red-600 mr-3" />
              <div>
                <p className="text-sm font-medium text-gray-600">Outstanding Balance</p>
                <p className="text-2xl font-bold text-red-600">${totalBalance.toFixed(2)}</p>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center">
              {isFeesPaid ? (
                <CheckCircle className="h-8 w-8 text-green-600 mr-3" />
              ) : (
                <AlertTriangle className="h-8 w-8 text-yellow-600 mr-3" />
              )}
              <div>
                <p className="text-sm font-medium text-gray-600">Payment Status</p>
                <Badge variant={isFeesPaid ? "success" : "warning"}>
                  {isFeesPaid ? "Fees Paid" : "Outstanding"}
                </Badge>
              </div>
            </div>
          </Card>
        </div>

        {/* Payment History */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Payment History</h2>
            <Receipt className="h-6 w-6 text-gray-400" />
          </div>

          {isLoading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="text-gray-500 mt-2">Loading payment history...</p>
            </div>
                     ) : paymentHistory && paymentHistory.length > 0 ? (
             <div className="space-y-4">
               {paymentHistory.map((payment) => (
                 <div key={payment.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                   <div>
                     <h3 className="font-medium text-gray-900">
                       {payment.term} - {payment.academicYear} ({payment.month})
                     </h3>
                     <p className="text-sm text-gray-600">
                       Payment Date: {new Date(payment.paymentDate).toLocaleDateString()}
                     </p>
                     <p className="text-xs text-gray-500">
                       Status: {payment.paymentStatus}
                     </p>
                   </div>
                   <div className="text-right">
                     <p className="font-medium text-green-600">
                       ${payment.amountPaid.toFixed(2)}
                     </p>
                     <p className="text-sm text-gray-600">
                       Fee: ${payment.monthlyFeeAmount.toFixed(2)}
                     </p>
                     {payment.balance > 0 && (
                       <p className="text-sm text-red-600">
                         Balance: ${payment.balance.toFixed(2)}
                       </p>
                     )}
                   </div>
                 </div>
               ))}
             </div>
          ) : (
            <div className="text-center py-8">
              <Receipt className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No payment history available</p>
            </div>
          )}
        </Card>

        {/* Important Notice */}
        {!isFeesPaid && (
          <Card className="p-6 mt-6 border-l-4 border-red-500">
            <div className="flex items-start">
              <AlertTriangle className="h-6 w-6 text-red-600 mr-3 mt-1" />
              <div>
                <h3 className="font-medium text-red-900">Outstanding Fees</h3>
                <p className="text-red-700 mt-1">
                  You have outstanding fees of ${totalBalance.toFixed(2)}. Please contact the school administration 
                  to arrange payment. Access to reports and other features may be restricted until fees are paid.
                </p>
              </div>
            </div>
          </Card>
        )}
      </div>
    </div>
  );
};

export default StudentFinancePage;
