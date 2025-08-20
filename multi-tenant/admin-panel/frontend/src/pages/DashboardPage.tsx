import React, { useState, useEffect } from 'react';
import { 
  BuildingOfficeIcon, 
  UsersIcon, 
  CurrencyDollarIcon,
  ChartBarIcon 
} from '@heroicons/react/24/outline';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

interface DashboardStats {
  totalSchools: number;
  activeSchools: number;
  totalRevenue: number;
  monthlyRevenue: number;
  revenueGrowth: number;
  schoolsByPlan: { plan: string; count: number }[];
}

const DashboardPage: React.FC = () => {
  const [stats, setStats] = useState<DashboardStats>({
    totalSchools: 0,
    activeSchools: 0,
    totalRevenue: 0,
    monthlyRevenue: 0,
    revenueGrowth: 0,
    schoolsByPlan: []
  });

  useEffect(() => {
    // TODO: Fetch dashboard stats from API
    // For now, using mock data
    setStats({
      totalSchools: 12,
      activeSchools: 10,
      totalRevenue: 15000,
      monthlyRevenue: 1200,
      revenueGrowth: 15.5,
      schoolsByPlan: [
        { plan: 'Basic', count: 5 },
        { plan: 'Premium', count: 4 },
        { plan: 'Enterprise', count: 3 }
      ]
    });
  }, []);

  const cards = [
    {
      name: 'Total Schools',
      value: stats.totalSchools,
      icon: BuildingOfficeIcon,
      color: 'bg-blue-500',
      change: '+2 this month'
    },
    {
      name: 'Active Schools',
      value: stats.activeSchools,
      icon: UsersIcon,
      color: 'bg-green-500',
      change: '95% active rate'
    },
    {
      name: 'Monthly Revenue',
      value: `$${stats.monthlyRevenue.toLocaleString()}`,
      icon: CurrencyDollarIcon,
      color: 'bg-yellow-500',
      change: `+${stats.revenueGrowth}% from last month`
    },
    {
      name: 'Total Revenue',
      value: `$${stats.totalRevenue.toLocaleString()}`,
      icon: ChartBarIcon,
      color: 'bg-purple-500',
      change: 'All time'
    }
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Overview of your multi-tenant school management system
        </p>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {cards.map((card) => (
          <div
            key={card.name}
            className="bg-white overflow-hidden shadow rounded-lg"
          >
            <div className="p-5">
              <div className="flex items-center">
                <div className="flex-shrink-0">
                  <card.icon className={`h-6 w-6 text-white ${card.color}`} />
                </div>
                <div className="ml-5 w-0 flex-1">
                  <dl>
                    <dt className="text-sm font-medium text-gray-500 truncate">
                      {card.name}
                    </dt>
                    <dd className="text-lg font-medium text-gray-900">
                      {card.value}
                    </dd>
                  </dl>
                </div>
              </div>
            </div>
            <div className="bg-gray-50 px-5 py-3">
              <div className="text-sm">
                <span className="text-green-600 font-medium">{card.change}</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
        {/* Schools by Plan */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Schools by Plan
          </h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={stats.schoolsByPlan}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="plan" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="count" fill="#3B82F6" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Recent Activity */}
        <div className="bg-white shadow rounded-lg p-6">
          <h3 className="text-lg font-medium text-gray-900 mb-4">
            Recent Activity
          </h3>
          <div className="space-y-4">
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0">
                <div className="h-8 w-8 bg-green-100 rounded-full flex items-center justify-center">
                  <BuildingOfficeIcon className="h-4 w-4 text-green-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  New school registered
                </p>
                <p className="text-sm text-gray-500">
                  St. Mary's High School joined the platform
                </p>
              </div>
              <div className="text-sm text-gray-500">2 hours ago</div>
            </div>
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0">
                <div className="h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center">
                  <CurrencyDollarIcon className="h-4 w-4 text-blue-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  Payment received
                </p>
                <p className="text-sm text-gray-500">
                  Central High School renewed premium plan
                </p>
              </div>
              <div className="text-sm text-gray-500">1 day ago</div>
            </div>
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0">
                <div className="h-8 w-8 bg-yellow-100 rounded-full flex items-center justify-center">
                  <UsersIcon className="h-4 w-4 text-yellow-600" />
                </div>
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900">
                  User activity spike
                </p>
                <p className="text-sm text-gray-500">
                  50% increase in daily active users
                </p>
              </div>
              <div className="text-sm text-gray-500">3 days ago</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default DashboardPage;


