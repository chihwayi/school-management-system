import React, { useState } from 'react';
import { Button } from './ui/Button';
import { Select } from './ui/Select';

interface TenantSetupProps {
  onTenantSelect: (tenant: string) => void;
}

export const TenantSetup: React.FC<TenantSetupProps> = ({ onTenantSelect }) => {
  const [selectedTenant, setSelectedTenant] = useState('');
  const [customTenant, setCustomTenant] = useState('');
  const [showCustom, setShowCustom] = useState(false);

  const predefinedTenants = [
    { value: 'lincoln', label: 'Lincoln High School' },
    { value: 'washington', label: 'Washington Academy' },
    { value: 'roosevelt', label: 'Roosevelt Elementary' },
    { value: 'custom', label: 'Create New School' }
  ];

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const tenant = selectedTenant === 'custom' ? customTenant : selectedTenant;
    if (tenant) {
      onTenantSelect(tenant);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex items-center justify-center">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-6">
        <h2 className="text-2xl font-bold text-center mb-6">Select Your School</h2>
        
        <form onSubmit={handleSubmit} className="space-y-4">
          <Select
            label="Choose School"
            options={predefinedTenants}
            value={selectedTenant}
            onChange={(e) => {
              setSelectedTenant(e.target.value);
              setShowCustom(e.target.value === 'custom');
            }}
            placeholder="Select a school..."
          />

          {showCustom && (
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                School Identifier
              </label>
              <input
                type="text"
                required
                value={customTenant}
                onChange={(e) => setCustomTenant(e.target.value.toLowerCase().replace(/[^a-z0-9-]/g, ''))}
                placeholder="e.g., my-school"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
              <p className="text-xs text-gray-500 mt-1">
                Only lowercase letters, numbers, and hyphens allowed
              </p>
            </div>
          )}

          <Button
            type="submit"
            disabled={!selectedTenant || (showCustom && !customTenant)}
            className="w-full"
          >
            Continue
          </Button>
        </form>
      </div>
    </div>
  );
};