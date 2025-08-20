import React, { useState, useEffect } from 'react';
import { getCurrentTenant, redirectToTenant } from '../utils/tenant';
import { Button } from '../components/ui/Button';
import { School } from 'lucide-react';

interface SchoolData {
  schoolId: string;
  name: string;
  subdomain: string;
  status: string;
}

const SchoolSelectionPage: React.FC = () => {
  const [schools, setSchools] = useState<SchoolData[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchSchools();
  }, []);

  const fetchSchools = async () => {
    try {
      setLoading(true);
      const response = await fetch('http://localhost:8081/api/admin/schools?page=0&size=50');
      const data = await response.json();
      setSchools(data || []);
    } catch (error) {
      console.error('Failed to fetch schools:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSchoolSelect = (subdomain: string) => {
    // Redirect to the school's setup page
    window.location.href = `http://${subdomain}.localhost:3000/setup`;
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <div className="mx-auto h-16 w-16 bg-blue-600 rounded-full flex items-center justify-center mb-6">
            <School className="h-8 w-8 text-white" />
          </div>
          <h2 className="text-3xl font-extrabold text-gray-900">
            Select a School
          </h2>
          <p className="mt-2 text-sm text-gray-600">
            Choose your school to access the management system
          </p>
        </div>

        <div className="bg-white rounded-lg shadow-lg p-6">
          {loading ? (
            <div className="text-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p className="mt-2 text-gray-600">Loading schools...</p>
            </div>
          ) : schools.length > 0 ? (
            <div className="space-y-3">
              <h3 className="text-lg font-medium text-gray-900 mb-4">Available Schools</h3>
              {schools.map((school) => (
                <Button
                  key={school.schoolId}
                  variant="outline"
                  onClick={() => handleSchoolSelect(school.subdomain)}
                  className="w-full justify-start p-4 h-auto"
                >
                  <div className="text-left">
                    <div className="font-medium text-gray-900">{school.name}</div>
                    <div className="text-sm text-gray-500">
                      {school.subdomain}.localhost:3000
                    </div>
                  </div>
                </Button>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <School className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <h3 className="text-lg font-medium text-gray-900 mb-2">No Schools Available</h3>
              <p className="text-gray-600 mb-4">
                No schools have been created yet. Create one in the Admin Panel.
              </p>
              <Button
                onClick={() => window.open('http://localhost:5173', '_blank')}
                className="bg-blue-600 hover:bg-blue-700"
              >
                Open Admin Panel
              </Button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SchoolSelectionPage;