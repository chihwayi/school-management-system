import React, { useState, useEffect } from 'react';
import { 
  ChartBarIcon,
  UsersIcon,
  BuildingOfficeIcon,
  CurrencyDollarIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  CalendarIcon,
  ArrowUpIcon,
  ArrowDownIcon
} from '@heroicons/react/24/outline';

interface AnalyticsData {
  totalSchools: number;
  totalUsers: number;
  activeSchools: number;
  monthlyRevenue: number;
  schoolGrowth: {
    current: number;
    previous: number;
    percentage: number;
  };
  userGrowth: {
    current: number;
    previous: number;
    percentage: number;
  };
  revenueGrowth: {
    current: number;
    previous: number;
    percentage: number;
  };
  planDistribution: {
    BASIC: number;
    PREMIUM: number;
    ENTERPRISE: number;
  };
  monthlyStats: {
    month: string;
    schools: number;
    users: number;
    revenue: number;
  }[];
  topSchools: {
    id: string;
    name: string;
    students: number;
    teachers: number;
    plan: string;
    revenue: number;
  }[];
}

const AnalyticsPage: React.FC = () => {
  const [analyticsData, setAnalyticsData] = useState<AnalyticsData | null>(null);
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState<'7d' | '30d' | '90d' | '1y'>('30d');

  useEffect(() => {
    // Mock analytics data - will be replaced with real API calls
    const mockData: AnalyticsData = {
      totalSchools: 2,
      totalUsers: 3,
      activeSchools: 2,
      monthlyRevenue: 450,
      schoolGrowth: {
        current: 2,
        previous: 0,
        percentage: 100
      },
      userGrowth: {
        current: 3,
        previous: 1,
        percentage: 200
      },
      revenueGrowth: {
        current: 450,
        previous: 0,
        percentage: 100
      },
      planDistribution: {
        BASIC: 0,
        PREMIUM: 1,
        ENTERPRISE: 1
      },
      monthlyStats: [
        { month: 'Jan', schools: 0, users: 1, revenue: 0 },
        { month: 'Feb', schools: 0, users: 1, revenue: 0 },
        { month: 'Mar', schools: 0, users: 1, revenue: 0 },
        { month: 'Apr', schools: 0, users: 1, revenue: 0 },
        { month: 'May', schools: 0, users: 1, revenue: 0 },
        { month: 'Jun', schools: 0, users: 1, revenue: 0 },
        { month: 'Jul', schools: 0, users: 1, revenue: 0 },
        { month: 'Aug', schools: 2, users: 3, revenue: 450 }
      ],
      topSchools: [
        {
          id: 'SCH23C2469B',
          name: 'Greenwood Elementary School',
          students: 350,
          teachers: 25,
          plan: 'PREMIUM',
          revenue: 200
        },
        {
          id: 'SCH4FF4CD7D',
          name: 'Riverside High School',
          students: 1200,
          teachers: 80,
          plan: 'ENTERPRISE',
          revenue: 250
        }
      ]
    };

    setTimeout(() => {
      setAnalyticsData(mockData);
      setLoading(false);
    }, 1000);
  }, [timeRange]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(amount);
  };

  const getGrowthIcon = (percentage: number) => {
    if (percentage > 0) {
      return <ArrowUpIcon className="h-4 w-4 text-green-500" />;
    } else if (percentage < 0) {
      return <ArrowDownIcon className="h-4 w-4 text-red-500" />;
    }
    return null;
  };

  const getGrowthColor = (percentage: number) => {
    if (percentage > 0) return 'text-green-600';
    if (percentage < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!analyticsData) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">Failed to load analytics data</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">Analytics</h1>
          <p className="mt-1 text-sm text-gray-500">
            Monitor your system performance and growth metrics
          </p>
        </div>
        <div>
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value as any)}
            className="border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
          >
            <option value="7d">Last 7 days</option>
            <option value="30d">Last 30 days</option>
            <option value="90d">Last 90 days</option>
            <option value="1y">Last year</option>
          </select>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <BuildingOfficeIcon className="h-6 w-6 text-blue-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Total Schools
                  </dt>
                  <dd className="flex items-baseline">
                    <div className="text-2xl font-semibold text-gray-900">
                      {analyticsData.totalSchools}
                    </div>
                    <div className={`ml-2 flex items-baseline text-sm ${getGrowthColor(analyticsData.schoolGrowth.percentage)}`}>
                      {getGrowthIcon(analyticsData.schoolGrowth.percentage)}
                      <span className="sr-only">
                        {analyticsData.schoolGrowth.percentage > 0 ? 'Increased' : 'Decreased'} by
                      </span>
                      {Math.abs(analyticsData.schoolGrowth.percentage)}%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <UsersIcon className="h-6 w-6 text-green-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Total Users
                  </dt>
                  <dd className="flex items-baseline">
                    <div className="text-2xl font-semibold text-gray-900">
                      {analyticsData.totalUsers}
                    </div>
                    <div className={`ml-2 flex items-baseline text-sm ${getGrowthColor(analyticsData.userGrowth.percentage)}`}>
                      {getGrowthIcon(analyticsData.userGrowth.percentage)}
                      {Math.abs(analyticsData.userGrowth.percentage)}%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <ArrowTrendingUpIcon className="h-6 w-6 text-indigo-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Active Schools
                  </dt>
                  <dd className="flex items-baseline">
                    <div className="text-2xl font-semibold text-gray-900">
                      {analyticsData.activeSchools}
                    </div>
                    <div className="ml-2 text-sm text-gray-500">
                      of {analyticsData.totalSchools}
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-5">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <CurrencyDollarIcon className="h-6 w-6 text-yellow-400" />
              </div>
              <div className="ml-5 w-0 flex-1">
                <dl>
                  <dt className="text-sm font-medium text-gray-500 truncate">
                    Monthly Revenue
                  </dt>
                  <dd className="flex items-baseline">
                    <div className="text-2xl font-semibold text-gray-900">
                      {formatCurrency(analyticsData.monthlyRevenue)}
                    </div>
                    <div className={`ml-2 flex items-baseline text-sm ${getGrowthColor(analyticsData.revenueGrowth.percentage)}`}>
                      {getGrowthIcon(analyticsData.revenueGrowth.percentage)}
                      {Math.abs(analyticsData.revenueGrowth.percentage)}%
                    </div>
                  </dd>
                </dl>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Charts Row */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Plan Distribution */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
              Plan Distribution
            </h3>
            <div className="space-y-4">
              {Object.entries(analyticsData.planDistribution).map(([plan, count]) => {
                const total = Object.values(analyticsData.planDistribution).reduce((a, b) => a + b, 0);
                const percentage = total > 0 ? (count / total) * 100 : 0;
                const colors = {
                  BASIC: 'bg-gray-400',
                  PREMIUM: 'bg-blue-400', 
                  ENTERPRISE: 'bg-purple-400'
                };
                
                return (
                  <div key={plan} className="flex items-center justify-between">
                    <div className="flex items-center">
                      <div className={`w-3 h-3 rounded-full ${colors[plan as keyof typeof colors]} mr-3`}></div>
                      <span className="text-sm font-medium text-gray-900">{plan}</span>
                    </div>
                    <div className="flex items-center">
                      <span className="text-sm text-gray-500 mr-2">{count} schools</span>
                      <span className="text-sm font-medium text-gray-900">{percentage.toFixed(0)}%</span>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        {/* Growth Chart */}
        <div className="bg-white overflow-hidden shadow rounded-lg">
          <div className="p-6">
            <h3 className="text-lg leading-6 font-medium text-gray-900 mb-4">
              Growth Trends
            </h3>
            <div className="space-y-4">
              <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                <div>
                  <p className="text-sm font-medium text-blue-900">School Growth</p>
                  <p className="text-xs text-blue-700">Month over month</p>
                </div>
                <div className="text-right">
                  <p className="text-lg font-semibold text-blue-900">+{analyticsData.schoolGrowth.percentage}%</p>
                  <p className="text-xs text-blue-700">{analyticsData.schoolGrowth.current} schools</p>
                </div>
              </div>
              
              <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                <div>
                  <p className="text-sm font-medium text-green-900">User Growth</p>
                  <p className="text-xs text-green-700">Month over month</p>
                </div>
                <div className="text-right">
                  <p className="text-lg font-semibold text-green-900">+{analyticsData.userGrowth.percentage}%</p>
                  <p className="text-xs text-green-700">{analyticsData.userGrowth.current} users</p>
                </div>
              </div>
              
              <div className="flex items-center justify-between p-3 bg-yellow-50 rounded-lg">
                <div>
                  <p className="text-sm font-medium text-yellow-900">Revenue Growth</p>
                  <p className="text-xs text-yellow-700">Month over month</p>
                </div>
                <div className="text-right">
                  <p className="text-lg font-semibold text-yellow-900">+{analyticsData.revenueGrowth.percentage}%</p>
                  <p className="text-xs text-yellow-700">{formatCurrency(analyticsData.revenueGrowth.current)}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Top Schools */}
      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        <div className="px-4 py-5 sm:px-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Top Performing Schools
          </h3>
          <p className="mt-1 max-w-2xl text-sm text-gray-500">
            Schools with highest student enrollment and revenue
          </p>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  School
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Plan
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Students
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Teachers
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Revenue
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {analyticsData.topSchools.map((school) => (
                <tr key={school.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10">
                        <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                          <span className="text-sm font-medium text-blue-600">
                            {school.name.charAt(0)}
                          </span>
                        </div>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">
                          {school.name}
                        </div>
                        <div className="text-sm text-gray-500">
                          ID: {school.id}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                      school.plan === 'ENTERPRISE' ? 'bg-purple-100 text-purple-800' :
                      school.plan === 'PREMIUM' ? 'bg-blue-100 text-blue-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {school.plan}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {school.students.toLocaleString()}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {school.teachers}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {formatCurrency(school.revenue)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Monthly Stats */}
      <div className="bg-white shadow rounded-lg">
        <div className="px-4 py-5 sm:px-6">
          <h3 className="text-lg leading-6 font-medium text-gray-900">
            Monthly Trends
          </h3>
          <p className="mt-1 max-w-2xl text-sm text-gray-500">
            Track growth patterns over time
          </p>
        </div>
        <div className="px-4 py-5 sm:p-6">
          <div className="overflow-x-auto">
            <div className="flex space-x-4">
              {analyticsData.monthlyStats.map((stat, index) => (
                <div key={stat.month} className="flex-shrink-0 text-center">
                  <div className="text-xs text-gray-500 mb-2">{stat.month}</div>
                  <div className="space-y-1">
                    <div className="w-12 bg-blue-200 rounded" style={{height: `${Math.max(stat.schools * 20, 4)}px`}}></div>
                    <div className="text-xs text-blue-600">{stat.schools}</div>
                    <div className="text-xs text-gray-400">Schools</div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPage;
