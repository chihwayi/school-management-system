import React, { useState, useEffect } from 'react';
import { getCurrentTenant, redirectToTenant } from '../../utils/tenant';
import { Button } from '../ui/Button';
import { Select } from '../ui/Select';

interface School {
  schoolId: string;
  name: string;
  subdomain: string;
  status: string;
}

export const TenantSelector: React.FC = () => {
  const [currentTenant, setCurrentTenant] = useState<string | null>(null);
  const [schools, setSchools] = useState<School[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const tenant = getCurrentTenant();
    setCurrentTenant(tenant);
    
    // Fetch available schools from admin API
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

  const handleTenantChange = (subdomain: string) => {
    if (subdomain && subdomain !== currentTenant) {
      redirectToTenant(subdomain);
    }
  };

  if (!currentTenant) {
    return (
      <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-4">
        <h3 className="text-lg font-semibold text-yellow-800 mb-2">
          Select a School
        </h3>
        <p className="text-yellow-700 mb-4">
          You need to select a school to access the system.
        </p>
        
        {loading ? (
          <div className="text-center">Loading schools...</div>
        ) : (
          <div className="space-y-2">
            {schools.length > 0 ? (
              schools.map((school) => (
                <Button
                  key={school.schoolId}
                  variant="outline"
                  onClick={() => handleTenantChange(school.subdomain)}
                  className="w-full justify-start"
                >
                  <div className="text-left">
                    <div className="font-medium">{school.name}</div>
                    <div className="text-sm text-gray-500">
                      {school.subdomain}.localhost:3000
                    </div>
                  </div>
                </Button>
              ))
            ) : (
              <div className="text-center text-gray-500">
                No schools available. Create one in the{' '}
                <a 
                  href="http://localhost:5173" 
                  className="text-blue-600 hover:underline"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  Admin Panel
                </a>
              </div>
            )}
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-4">
      <div className="flex items-center justify-between">
        <div>
          <div className="text-sm font-medium text-blue-800">
            Current School: {currentTenant.replace('_', '-')}
          </div>
          <div className="text-xs text-blue-600">
            {window.location.hostname}
          </div>
        </div>
        
        <Select
          value={currentTenant}
          onChange={(e) => handleTenantChange(e.target.value)}
          className="text-sm"
        >
          <option value="">Switch School...</option>
          {schools.map((school) => (
            <option key={school.schoolId} value={school.subdomain}>
              {school.name}
            </option>
          ))}
        </Select>
      </div>
    </div>
  );
};

export default TenantSelector;