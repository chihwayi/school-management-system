import React, { useState, useEffect } from 'react';
import { 
  BarChart, 
  TrendingUp, 
  Users, 
  Target, 
  Clock, 
  DollarSign,
  Activity,
  Calendar,
  PieChart,
  BarChart3
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Select from '../../components/ui/Select';
import { useAuth } from '../../hooks/useAuth';
import { aiService } from '../../services/aiService';
// Temporary type definitions to fix the import issue
interface AiUsageStats {
  teacherId: number;
  usageStats: Array<[string, number]>;
  monthlyTokens: number;
  monthlyCost: number;
}

interface AiMonthlyUsage {
  teacherId: number;
  period: string;
  totalTokens: number;
  totalCost: number;
}

const AiAnalyticsPage: React.FC = () => {
  const { theme } = useAuth();
  const [usageStats, setUsageStats] = useState<AiUsageStats | null>(null);
  const [monthlyUsage, setMonthlyUsage] = useState<AiMonthlyUsage | null>(null);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('30');

  useEffect(() => {
    const fetchAnalytics = async () => {
      try {
        const [stats, monthly] = await Promise.all([
          aiService.getTeacherUsageStats(),
          aiService.getMonthlyUsageStats()
        ]);
        setUsageStats(stats);
        setMonthlyUsage(monthly);
      } catch (error) {
        console.error('Failed to fetch analytics:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchAnalytics();
  }, []);

  const formatCurrency = (cents: number) => {
    return `$${(cents / 100).toFixed(2)}`;
  };

  const formatNumber = (num: number) => {
    return num.toLocaleString();
  };

  const getOperationColor = (operation: string) => {
    const colors: { [key: string]: string } = {
      'UPLOAD_RESOURCE': 'bg-blue-500',
      'GENERATE_CONTENT': 'bg-purple-500',
      'PROCESS_RESOURCE': 'bg-green-500',
      'ANALYZE_CONTENT': 'bg-orange-500',
      'SUMMARIZE_TEXT': 'bg-pink-500'
    };
    return colors[operation] || 'bg-gray-500';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">AI Analytics</h1>
          <p className="text-gray-600">Monitor your AI usage and performance metrics</p>
        </div>
        <div className="flex items-center space-x-4">
          <Select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="w-32"
            options={[
              { value: '7', label: 'Last 7 days' },
              { value: '30', label: 'Last 30 days' },
              { value: '90', label: 'Last 90 days' },
              { value: '365', label: 'Last year' }
            ]}
            placeholder="Select time range"
          />
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <Card className="p-6">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-blue-100 rounded-lg">
              <TrendingUp className="h-6 w-6 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Tokens Used</p>
              <p className="text-2xl font-bold text-gray-900">
                {usageStats ? formatNumber(usageStats.monthlyTokens) : '0'}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-6">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-green-100 rounded-lg">
              <DollarSign className="h-6 w-6 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Cost</p>
              <p className="text-2xl font-bold text-gray-900">
                {usageStats ? formatCurrency(usageStats.monthlyCost) : '$0.00'}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-6">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-purple-100 rounded-lg">
              <Activity className="h-6 w-6 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Operations</p>
              <p className="text-2xl font-bold text-gray-900">
                {usageStats ? formatNumber(usageStats.usageStats.reduce((sum, [, count]) => sum + count, 0)) : '0'}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-6">
          <div className="flex items-center space-x-4">
            <div className="p-3 bg-orange-100 rounded-lg">
              <Clock className="h-6 w-6 text-orange-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Avg Response Time</p>
              <p className="text-2xl font-bold text-gray-900">2.3s</p>
            </div>
          </div>
        </Card>
      </div>

      {/* Usage Breakdown */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Operations by Type */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Operations by Type</h3>
            <PieChart className="h-5 w-5 text-gray-400" />
          </div>
          
          {usageStats && usageStats.usageStats.length > 0 ? (
            <div className="space-y-4">
              {usageStats.usageStats.map(([operation, count]) => (
                <div key={operation} className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className={`w-3 h-3 rounded-full ${getOperationColor(operation)}`}></div>
                    <span className="text-sm font-medium text-gray-700">
                      {operation.replace('_', ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())}
                    </span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <div className="w-24 bg-gray-200 rounded-full h-2">
                      <div 
                        className="bg-blue-600 h-2 rounded-full" 
                        style={{ 
                          width: `${(count / usageStats.usageStats.reduce((sum, [, c]) => sum + c, 0)) * 100}%` 
                        }}
                      ></div>
                    </div>
                    <span className="text-sm font-medium text-gray-900 w-8 text-right">
                      {count}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <BarChart3 className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No usage data available</p>
            </div>
          )}
        </Card>

        {/* Monthly Trends */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Monthly Trends</h3>
            <TrendingUp className="h-5 w-5 text-gray-400" />
          </div>
          
          {monthlyUsage ? (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="text-center p-4 bg-blue-50 rounded-lg">
                  <p className="text-sm text-gray-600">Tokens Used</p>
                  <p className="text-2xl font-bold text-blue-600">
                    {formatNumber(monthlyUsage.totalTokens)}
                  </p>
                </div>
                <div className="text-center p-4 bg-green-50 rounded-lg">
                  <p className="text-sm text-gray-600">Total Cost</p>
                  <p className="text-2xl font-bold text-green-600">
                    {formatCurrency(monthlyUsage.totalCost)}
                  </p>
                </div>
              </div>
              
              <div className="mt-4">
                <p className="text-sm text-gray-600 mb-2">Period: {monthlyUsage.period}</p>
                <div className="w-full bg-gray-200 rounded-full h-2">
                  <div 
                    className="bg-gradient-to-r from-blue-500 to-purple-500 h-2 rounded-full" 
                    style={{ width: '75%' }}
                  ></div>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center py-8">
              <Calendar className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No monthly data available</p>
            </div>
          )}
        </Card>
      </div>

      {/* Detailed Analytics */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Cost Analysis */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Cost Analysis</h3>
          <div className="space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Content Generation</span>
              <span className="text-sm font-medium text-gray-900">
                {usageStats ? formatCurrency(Math.round(usageStats.monthlyCost * 0.6)) : '$0.00'}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Resource Processing</span>
              <span className="text-sm font-medium text-gray-900">
                {usageStats ? formatCurrency(Math.round(usageStats.monthlyCost * 0.3)) : '$0.00'}
              </span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-gray-600">Analysis & Summarization</span>
              <span className="text-sm font-medium text-gray-900">
                {usageStats ? formatCurrency(Math.round(usageStats.monthlyCost * 0.1)) : '$0.00'}
              </span>
            </div>
            <div className="border-t pt-3">
              <div className="flex justify-between items-center">
                <span className="text-sm font-medium text-gray-900">Total</span>
                <span className="text-sm font-bold text-gray-900">
                  {usageStats ? formatCurrency(usageStats.monthlyCost) : '$0.00'}
                </span>
              </div>
            </div>
          </div>
        </Card>

        {/* Performance Metrics */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Performance Metrics</h3>
          <div className="space-y-4">
            <div>
              <div className="flex justify-between items-center mb-1">
                <span className="text-sm text-gray-600">Success Rate</span>
                <span className="text-sm font-medium text-gray-900">98.5%</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-green-500 h-2 rounded-full" style={{ width: '98.5%' }}></div>
              </div>
            </div>
            
            <div>
              <div className="flex justify-between items-center mb-1">
                <span className="text-sm text-gray-600">Average Response Time</span>
                <span className="text-sm font-medium text-gray-900">2.3s</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-blue-500 h-2 rounded-full" style={{ width: '85%' }}></div>
              </div>
            </div>
            
            <div>
              <div className="flex justify-between items-center mb-1">
                <span className="text-sm text-gray-600">Content Quality Score</span>
                <span className="text-sm font-medium text-gray-900">4.8/5</span>
              </div>
              <div className="w-full bg-gray-200 rounded-full h-2">
                <div className="bg-purple-500 h-2 rounded-full" style={{ width: '96%' }}></div>
              </div>
            </div>
          </div>
        </Card>

        {/* Usage Recommendations */}
        <Card className="p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Recommendations</h3>
          <div className="space-y-3">
            <div className="p-3 bg-blue-50 rounded-lg">
              <h4 className="text-sm font-medium text-blue-900 mb-1">Optimize Token Usage</h4>
              <p className="text-xs text-blue-700">
                Consider using shorter prompts to reduce token consumption and costs.
              </p>
            </div>
            
            <div className="p-3 bg-green-50 rounded-lg">
              <h4 className="text-sm font-medium text-green-900 mb-1">Batch Processing</h4>
              <p className="text-xs text-green-700">
                Process multiple resources together to improve efficiency.
              </p>
            </div>
            
            <div className="p-3 bg-purple-50 rounded-lg">
              <h4 className="text-sm font-medium text-purple-900 mb-1">Content Reuse</h4>
              <p className="text-xs text-purple-700">
                Reuse generated content across multiple classes to maximize value.
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Export Options */}
      <Card className="p-6">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-lg font-semibold text-gray-900">Export Analytics</h3>
            <p className="text-sm text-gray-600">Download your usage data for further analysis</p>
          </div>
          <div className="flex space-x-3">
            <Button variant="outline">
              <BarChart className="h-4 w-4 mr-2" />
              Export CSV
            </Button>
            <Button variant="outline">
              <TrendingUp className="h-4 w-4 mr-2" />
              Export Report
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AiAnalyticsPage;
