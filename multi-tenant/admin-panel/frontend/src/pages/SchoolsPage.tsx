import React, { useState, useEffect, Fragment } from 'react';
import { 
  PlusIcon, 
  EyeIcon, 
  PauseIcon, 
  PlayIcon, 
  TrashIcon,
  UsersIcon,
  AcademicCapIcon
} from '@heroicons/react/24/outline';
import toast from 'react-hot-toast';
import schoolService, { School, SchoolCreateRequest, SchoolStats } from '../services/schoolService';

// Separate component for action buttons
const ActionButtons: React.FC<{ school: School; onView: (school: School) => void; onSuspend: (school: School) => void; onActivate: (school: School) => void; onDelete: (school: School) => void }> = ({ 
  school, 
  onView, 
  onSuspend, 
  onActivate, 
  onDelete 
}) => {
  return (
    <div className="flex justify-end space-x-1">
      <button
        key={`view-${school.id}`}
        onClick={() => onView(school)}
        className="text-blue-600 hover:text-blue-900 p-1"
        title="View School"
      >
        <EyeIcon className="h-4 w-4" />
      </button>
      {school.status === 'ACTIVE' ? (
        <button
          key={`suspend-${school.id}`}
          onClick={() => onSuspend(school)}
          className="text-orange-600 hover:text-orange-900 p-1"
          title="Suspend School"
        >
          <PauseIcon className="h-4 w-4" />
        </button>
      ) : (
        <button
          key={`activate-${school.id}`}
          onClick={() => onActivate(school)}
          className="text-green-600 hover:text-green-900 p-1"
          title="Activate School"
        >
          <PlayIcon className="h-4 w-4" />
        </button>
      )}
      <button
        key={`delete-${school.id}`}
        onClick={() => onDelete(school)}
        className="text-red-600 hover:text-red-900 p-1"
        title="Delete School"
      >
        <TrashIcon className="h-4 w-4" />
      </button>
    </div>
  );
};

const SchoolsPage: React.FC = () => {
  const [schools, setSchools] = useState<School[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [schoolStats, setSchoolStats] = useState<{ [key: string]: SchoolStats }>({});
  const [createLoading, setCreateLoading] = useState(false); // Force re-render
  const [newSchool, setNewSchool] = useState<SchoolCreateRequest>({
    name: '',
    subdomain: '',
    planType: 'TRIAL',
    adminEmail: '',
    adminUsername: '',
    adminPassword: '',
    contactPerson: '',
    contactPhone: '',
    address: '',
    timezone: 'UTC',
    currency: 'USD',
    language: 'en',
    primaryColor: '#3B82F6',
    secondaryColor: '#1E40AF',
    notes: ''
  });

  const fetchSchools = async () => {
    try {
      setLoading(true);
      const schoolsData = await schoolService.getAllSchools(0, 50);
      setSchools(schoolsData);
      
      // Fetch stats for each school
      const statsPromises = schoolsData.map(async (school) => {
        try {
          const stats = await schoolService.getSchoolStats(school.id);
          return { schoolId: school.id, stats };
        } catch (error) {
          console.warn(`Failed to fetch stats for school ${school.id}:`, error);
          return { schoolId: school.id, stats: null };
        }
      });
      
      const statsResults = await Promise.all(statsPromises);
      const statsMap: { [key: string]: SchoolStats } = {};
      statsResults.forEach(({ schoolId, stats }) => {
        if (stats) statsMap[schoolId] = stats;
      });
      setSchoolStats(statsMap);
      
    } catch (error) {
      console.error('Failed to fetch schools:', error);
      toast.error('Failed to load schools');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSchools();
  }, []);

  const getStatusColor = (status: string) => {
    switch (status.toLowerCase()) {
      case 'active':
        return 'bg-green-100 text-green-800';
      case 'suspended':
        return 'bg-red-100 text-red-800';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPlanColor = (plan: string) => {
    switch (plan.toLowerCase()) {
      case 'enterprise':
        return 'bg-purple-100 text-purple-800';
      case 'premium':
        return 'bg-blue-100 text-blue-800';
      case 'basic':
        return 'bg-gray-100 text-gray-800';
      case 'trial':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // Helper function to generate subdomain from school name
  const generateSubdomain = (name: string) => {
    return name
      .toLowerCase()
      .replace(/[^a-z0-9\s]/g, '') // Remove special characters
      .replace(/\s+/g, '-') // Replace spaces with hyphens
      .replace(/-+/g, '-') // Replace multiple hyphens with single
      .replace(/^-|-$/g, ''); // Remove leading/trailing hyphens
  };

  const handleCreateSchool = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newSchool.name || !newSchool.adminEmail) {
      toast.error('Please fill in required fields');
      return;
    }

    // Auto-generate subdomain if not provided
    const schoolData = {
      ...newSchool,
      subdomain: newSchool.subdomain || generateSubdomain(newSchool.name),
      adminUsername: newSchool.adminUsername || newSchool.adminEmail,
      adminPassword: newSchool.adminPassword || 'TempPass123!'
    };

    try {
      setCreateLoading(true);
      const createdSchool = await schoolService.createSchool(schoolData);
      toast.success(`${createdSchool.name} created successfully!`);
      setShowCreateModal(false);
      setNewSchool({
        name: '',
        subdomain: '',
        planType: 'TRIAL',
        adminEmail: '',
        adminUsername: '',
        adminPassword: '',
        contactPerson: '',
        contactPhone: '',
        address: '',
        timezone: 'UTC',
        currency: 'USD',
        language: 'en',
        primaryColor: '#3B82F6',
        secondaryColor: '#1E40AF',
        notes: ''
      });
      fetchSchools(); // Refresh the list
    } catch (error: any) {
      console.error('Failed to create school:', error);
      toast.error(error.response?.data?.message || 'Failed to create school');
    } finally {
      setCreateLoading(false);
    }
  };

  const handleDeleteSchool = async (school: School) => {
    if (!confirm(`Are you sure you want to delete ${school.name}? This action cannot be undone and will delete all school data.`)) {
      return;
    }

    try {
      await schoolService.deleteSchool(school.id);
      toast.success(`${school.name} deleted successfully`);
      fetchSchools(); // Refresh the list
    } catch (error: any) {
      console.error('Failed to delete school:', error);
      toast.error(error.response?.data?.message || 'Failed to delete school');
    }
  };

  const handleSuspendSchool = async (school: School) => {
    const reason = prompt(`Why are you suspending ${school.name}?`);
    if (!reason) return;

    try {
      await schoolService.suspendSchool(school.id, reason);
      toast.success(`${school.name} suspended successfully`);
      fetchSchools(); // Refresh the list
    } catch (error: any) {
      console.error('Failed to suspend school:', error);
      toast.error(error.response?.data?.message || 'Failed to suspend school');
    }
  };

  const handleActivateSchool = async (school: School) => {
    try {
      await schoolService.activateSchool(school.id);
      toast.success(`${school.name} activated successfully`);
      fetchSchools(); // Refresh the list
    } catch (error: any) {
      console.error('Failed to activate school:', error);
      toast.error(error.response?.data?.message || 'Failed to activate school');
    }
  };

  const handleViewSchool = (school: School) => {
    // Generate the URL based on the school subdomain
    const url = `http://${school.slug}.localhost:3000`;
    window.open(url, '_blank');
  };

  const handleSendWelcomeEmail = async (school: School) => {
    try {
      await schoolService.sendWelcomeEmail(school.id);
      toast.success(`Welcome email sent to ${school.name}`);
    } catch (error: any) {
      console.error('Failed to send welcome email:', error);
      toast.error(error.response?.data?.message || 'Failed to send welcome email');
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-semibold text-gray-900">Schools</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage all schools in your multi-tenant system
          </p>
        </div>
        <button
          onClick={() => setShowCreateModal(true)}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          <PlusIcon className="h-4 w-4 mr-2" />
          Add School
        </button>
      </div>

      {/* Schools Table */}
      <div className="bg-white shadow overflow-hidden sm:rounded-md">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  School
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Domain
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Plan
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Users
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Created
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {schools.map((school) => {
                const stats = schoolStats[school.id];
                return (
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
                            {school.contactEmail}
                          </div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900">{school.slug}.localhost</div>
                      <div className="text-sm text-gray-500">DB: {school.databaseName}</div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getPlanColor(school.planType)}`}>
                        {school.planType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusColor(school.status)}`}>
                        {school.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {stats ? (
                        <div>
                          <div key={`students-${school.id}`} className="flex items-center">
                            <UsersIcon className="h-4 w-4 mr-1 text-gray-400" />
                            {stats.studentCount} students
                          </div>
                          <div key={`teachers-${school.id}`} className="flex items-center text-gray-500">
                            <AcademicCapIcon className="h-4 w-4 mr-1" />
                            {stats.teacherCount} teachers
                          </div>
                        </div>
                      ) : (
                        <div className="text-gray-400">Loading...</div>
                      )}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(school.createdAt).toLocaleDateString()}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <ActionButtons
                        school={school}
                        onView={handleViewSchool}
                        onSuspend={handleSuspendSchool}
                        onActivate={handleActivateSchool}
                        onDelete={handleDeleteSchool}
                      />
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create School Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-10 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">
                Create New School
              </h3>
              <form onSubmit={handleCreateSchool} className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    School Name *
                  </label>
                  <input
                    type="text"
                    required
                    value={newSchool.name}
                    onChange={(e) => setNewSchool({...newSchool, name: e.target.value})}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    placeholder="Enter school name"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Subdomain (auto-generated)
                  </label>
                  <input
                    type="text"
                    value={newSchool.subdomain || generateSubdomain(newSchool.name)}
                    onChange={(e) => setNewSchool({...newSchool, subdomain: e.target.value})}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    placeholder="school-name"
                  />
                  <p className="text-xs text-gray-500 mt-1">URL will be: {(newSchool.subdomain || generateSubdomain(newSchool.name))}.localhost</p>
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Admin Email *
                  </label>
                  <input
                    type="email"
                    required
                    value={newSchool.adminEmail}
                    onChange={(e) => setNewSchool({...newSchool, adminEmail: e.target.value})}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    placeholder="admin@school.com"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Contact Phone
                  </label>
                  <input
                    type="tel"
                    value={newSchool.contactPhone || ''}
                    onChange={(e) => setNewSchool({...newSchool, contactPhone: e.target.value})}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    placeholder="+1 (555) 123-4567"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700">
                    Address
                  </label>
                  <input
                    type="text"
                    value={newSchool.address || ''}
                    onChange={(e) => setNewSchool({...newSchool, address: e.target.value})}
                    className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                    placeholder="School address"
                  />
                </div>
                <div className="bg-green-50 border border-green-200 rounded-md p-3">
                  <div className="flex items-center">
                    <div className="flex-shrink-0">
                      <svg className="h-5 w-5 text-green-400" viewBox="0 0 20 20" fill="currentColor">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <div className="ml-3">
                      <h3 className="text-sm font-medium text-green-800">
                        1-Month Free Trial
                      </h3>
                      <div className="mt-1 text-sm text-green-700">
                        <p>New schools start with a 30-day free trial. Plans can be configured later.</p>
                      </div>
                    </div>
                  </div>
                </div>
                <div className="flex justify-end space-x-3">
                  <button
                    type="button"
                    onClick={() => setShowCreateModal(false)}
                    className="px-4 py-2 text-sm font-medium text-gray-700 bg-gray-100 border border-gray-300 rounded-md hover:bg-gray-200"
                    disabled={createLoading}
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    disabled={createLoading}
                    className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 disabled:opacity-50"
                  >
                    {createLoading ? 'Creating...' : 'Create School'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SchoolsPage;
