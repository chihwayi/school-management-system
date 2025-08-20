import React, { useState, useEffect } from 'react';
import { 
  CogIcon,
  ServerIcon,
  ShieldCheckIcon,
  BellIcon,
  GlobeAltIcon,
  CircleStackIcon,
  ClockIcon
} from '@heroicons/react/24/outline';
import toast from 'react-hot-toast';

interface SystemSettings {
  siteName: string;
  supportEmail: string;
  defaultTimezone: string;
  defaultCurrency: string;
  defaultLanguage: string;
  maxSchoolsPerPlan: {
    BASIC: number;
    PREMIUM: number;
    ENTERPRISE: number;
  };
  sessionTimeout: number;
  passwordPolicy: {
    minLength: number;
    requireUppercase: boolean;
    requireLowercase: boolean;
    requireNumbers: boolean;
    requireSpecialChars: boolean;
  };
  emailSettings: {
    smtpHost: string;
    smtpPort: number;
    smtpUsername: string;
    smtpPassword: string;
    fromEmail: string;
    fromName: string;
  };
  backupSettings: {
    enabled: boolean;
    frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
    retentionDays: number;
    s3Bucket?: string;
  };
}

const SettingsPage: React.FC = () => {
  const [settings, setSettings] = useState<SystemSettings>({
    siteName: 'School Management System',
    supportEmail: 'support@schoolsystem.com',
    defaultTimezone: 'UTC',
    defaultCurrency: 'USD',
    defaultLanguage: 'en',
    maxSchoolsPerPlan: {
      BASIC: 1,
      PREMIUM: 5,
      ENTERPRISE: 999
    },
    sessionTimeout: 3600,
    passwordPolicy: {
      minLength: 8,
      requireUppercase: true,
      requireLowercase: true,
      requireNumbers: true,
      requireSpecialChars: true
    },
    emailSettings: {
      smtpHost: 'smtp.gmail.com',
      smtpPort: 587,
      smtpUsername: '',
      smtpPassword: '',
      fromEmail: 'noreply@schoolsystem.com',
      fromName: 'School Management System'
    },
    backupSettings: {
      enabled: true,
      frequency: 'DAILY',
      retentionDays: 30,
      s3Bucket: ''
    }
  });

  const [activeTab, setActiveTab] = useState('general');
  const [saving, setSaving] = useState(false);
  const [testingEmail, setTestingEmail] = useState(false);

  const tabs = [
    { id: 'general', name: 'General', icon: CogIcon },
    { id: 'security', name: 'Security', icon: ShieldCheckIcon },
    { id: 'email', name: 'Email', icon: BellIcon },
    { id: 'backup', name: 'Backup', icon: CircleStackIcon },
    { id: 'system', name: 'System', icon: ServerIcon },
  ];

  const handleSaveSettings = async () => {
    try {
      setSaving(true);
      // TODO: Replace with actual API call
      await new Promise(resolve => setTimeout(resolve, 1000));
      toast.success('Settings saved successfully!');
    } catch (error) {
      console.error('Failed to save settings:', error);
      toast.error('Failed to save settings');
    } finally {
      setSaving(false);
    }
  };

  const handleTestEmail = async () => {
    try {
      setTestingEmail(true);
      // TODO: Replace with actual API call
      await new Promise(resolve => setTimeout(resolve, 2000));
      toast.success('Test email sent successfully!');
    } catch (error) {
      console.error('Failed to send test email:', error);
      toast.error('Failed to send test email');
    } finally {
      setTestingEmail(false);
    }
  };

  const renderGeneralSettings = () => (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">General Settings</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Site Name
            </label>
            <input
              type="text"
              value={settings.siteName}
              onChange={(e) => setSettings({...settings, siteName: e.target.value})}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Support Email
            </label>
            <input
              type="email"
              value={settings.supportEmail}
              onChange={(e) => setSettings({...settings, supportEmail: e.target.value})}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Default Timezone
            </label>
            <select
              value={settings.defaultTimezone}
              onChange={(e) => setSettings({...settings, defaultTimezone: e.target.value})}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="UTC">UTC</option>
              <option value="America/New_York">Eastern Time</option>
              <option value="America/Chicago">Central Time</option>
              <option value="America/Denver">Mountain Time</option>
              <option value="America/Los_Angeles">Pacific Time</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Default Currency
            </label>
            <select
              value={settings.defaultCurrency}
              onChange={(e) => setSettings({...settings, defaultCurrency: e.target.value})}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="USD">USD - US Dollar</option>
              <option value="EUR">EUR - Euro</option>
              <option value="GBP">GBP - British Pound</option>
              <option value="CAD">CAD - Canadian Dollar</option>
            </select>
          </div>
        </div>
      </div>

      <div>
        <h4 className="text-md font-medium text-gray-900 mb-4">School Limits per Plan</h4>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Basic Plan
            </label>
            <input
              type="number"
              value={settings.maxSchoolsPerPlan.BASIC}
              onChange={(e) => setSettings({
                ...settings, 
                maxSchoolsPerPlan: {
                  ...settings.maxSchoolsPerPlan,
                  BASIC: parseInt(e.target.value)
                }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Premium Plan
            </label>
            <input
              type="number"
              value={settings.maxSchoolsPerPlan.PREMIUM}
              onChange={(e) => setSettings({
                ...settings, 
                maxSchoolsPerPlan: {
                  ...settings.maxSchoolsPerPlan,
                  PREMIUM: parseInt(e.target.value)
                }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Enterprise Plan
            </label>
            <input
              type="number"
              value={settings.maxSchoolsPerPlan.ENTERPRISE}
              onChange={(e) => setSettings({
                ...settings, 
                maxSchoolsPerPlan: {
                  ...settings.maxSchoolsPerPlan,
                  ENTERPRISE: parseInt(e.target.value)
                }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
      </div>
    </div>
  );

  const renderSecuritySettings = () => (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Security Settings</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Session Timeout (seconds)
            </label>
            <input
              type="number"
              value={settings.sessionTimeout}
              onChange={(e) => setSettings({...settings, sessionTimeout: parseInt(e.target.value)})}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
      </div>

      <div>
        <h4 className="text-md font-medium text-gray-900 mb-4">Password Policy</h4>
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              Minimum Length
            </label>
            <input
              type="number"
              value={settings.passwordPolicy.minLength}
              onChange={(e) => setSettings({
                ...settings,
                passwordPolicy: {
                  ...settings.passwordPolicy,
                  minLength: parseInt(e.target.value)
                }
              })}
              className="mt-1 block w-32 border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div className="space-y-2">
            {[
              { key: 'requireUppercase', label: 'Require Uppercase Letters' },
              { key: 'requireLowercase', label: 'Require Lowercase Letters' },
              { key: 'requireNumbers', label: 'Require Numbers' },
              { key: 'requireSpecialChars', label: 'Require Special Characters' }
            ].map(({ key, label }) => (
              <div key={key} className="flex items-center">
                <input
                  type="checkbox"
                  checked={settings.passwordPolicy[key as keyof typeof settings.passwordPolicy] as boolean}
                  onChange={(e) => setSettings({
                    ...settings,
                    passwordPolicy: {
                      ...settings.passwordPolicy,
                      [key]: e.target.checked
                    }
                  })}
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label className="ml-2 block text-sm text-gray-900">
                  {label}
                </label>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );

  const renderEmailSettings = () => (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Email Settings</h3>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
          <div>
            <label className="block text-sm font-medium text-gray-700">
              SMTP Host
            </label>
            <input
              type="text"
              value={settings.emailSettings.smtpHost}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, smtpHost: e.target.value }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              SMTP Port
            </label>
            <input
              type="number"
              value={settings.emailSettings.smtpPort}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, smtpPort: parseInt(e.target.value) }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              SMTP Username
            </label>
            <input
              type="text"
              value={settings.emailSettings.smtpUsername}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, smtpUsername: e.target.value }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              SMTP Password
            </label>
            <input
              type="password"
              value={settings.emailSettings.smtpPassword}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, smtpPassword: e.target.value }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              From Email
            </label>
            <input
              type="email"
              value={settings.emailSettings.fromEmail}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, fromEmail: e.target.value }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">
              From Name
            </label>
            <input
              type="text"
              value={settings.emailSettings.fromName}
              onChange={(e) => setSettings({
                ...settings,
                emailSettings: { ...settings.emailSettings, fromName: e.target.value }
              })}
              className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>
        <div className="mt-4">
          <button
            onClick={handleTestEmail}
            disabled={testingEmail}
            className="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            {testingEmail ? 'Sending...' : 'Send Test Email'}
          </button>
        </div>
      </div>
    </div>
  );

  const renderBackupSettings = () => (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">Backup Settings</h3>
        <div className="space-y-4">
          <div className="flex items-center">
            <input
              type="checkbox"
              checked={settings.backupSettings.enabled}
              onChange={(e) => setSettings({
                ...settings,
                backupSettings: { ...settings.backupSettings, enabled: e.target.checked }
              })}
              className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
            />
            <label className="ml-2 block text-sm text-gray-900">
              Enable Automatic Backups
            </label>
          </div>
          
          {settings.backupSettings.enabled && (
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Backup Frequency
                </label>
                <select
                  value={settings.backupSettings.frequency}
                  onChange={(e) => setSettings({
                    ...settings,
                    backupSettings: { 
                      ...settings.backupSettings, 
                      frequency: e.target.value as 'DAILY' | 'WEEKLY' | 'MONTHLY' 
                    }
                  })}
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="DAILY">Daily</option>
                  <option value="WEEKLY">Weekly</option>
                  <option value="MONTHLY">Monthly</option>
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700">
                  Retention (days)
                </label>
                <input
                  type="number"
                  value={settings.backupSettings.retentionDays}
                  onChange={(e) => setSettings({
                    ...settings,
                    backupSettings: { 
                      ...settings.backupSettings, 
                      retentionDays: parseInt(e.target.value) 
                    }
                  })}
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
              <div className="sm:col-span-2">
                <label className="block text-sm font-medium text-gray-700">
                  S3 Bucket (optional)
                </label>
                <input
                  type="text"
                  value={settings.backupSettings.s3Bucket || ''}
                  onChange={(e) => setSettings({
                    ...settings,
                    backupSettings: { 
                      ...settings.backupSettings, 
                      s3Bucket: e.target.value 
                    }
                  })}
                  placeholder="my-backup-bucket"
                  className="mt-1 block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500"
                />
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );

  const renderSystemInfo = () => (
    <div className="space-y-6">
      <div>
        <h3 className="text-lg font-medium text-gray-900 mb-4">System Information</h3>
        <div className="bg-gray-50 rounded-lg p-4">
          <dl className="grid grid-cols-1 gap-x-4 gap-y-6 sm:grid-cols-2">
            <div>
              <dt className="text-sm font-medium text-gray-500">Version</dt>
              <dd className="mt-1 text-sm text-gray-900">1.0.0</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Environment</dt>
              <dd className="mt-1 text-sm text-gray-900">Production</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Database</dt>
              <dd className="mt-1 text-sm text-gray-900">MySQL 8.0</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Last Backup</dt>
              <dd className="mt-1 text-sm text-gray-900">2025-08-19 08:00 UTC</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Uptime</dt>
              <dd className="mt-1 text-sm text-gray-900">12 days, 4 hours</dd>
            </div>
            <div>
              <dt className="text-sm font-medium text-gray-500">Memory Usage</dt>
              <dd className="mt-1 text-sm text-gray-900">2.1 GB / 4.0 GB</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-semibold text-gray-900">Settings</h1>
        <p className="mt-1 text-sm text-gray-500">
          Manage your system configuration and preferences
        </p>
      </div>

      {/* Tabs */}
      <div className="bg-white shadow rounded-lg">
        <div className="border-b border-gray-200">
          <nav className="-mb-px flex space-x-8 px-6" aria-label="Tabs">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`${
                  activeTab === tab.id
                    ? 'border-blue-500 text-blue-600'
                    : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
                } whitespace-nowrap py-4 px-1 border-b-2 font-medium text-sm flex items-center`}
              >
                <tab.icon className="h-5 w-5 mr-2" />
                {tab.name}
              </button>
            ))}
          </nav>
        </div>

        <div className="p-6">
          {activeTab === 'general' && renderGeneralSettings()}
          {activeTab === 'security' && renderSecuritySettings()}
          {activeTab === 'email' && renderEmailSettings()}
          {activeTab === 'backup' && renderBackupSettings()}
          {activeTab === 'system' && renderSystemInfo()}
        </div>

        {activeTab !== 'system' && (
          <div className="bg-gray-50 px-6 py-3 flex justify-end">
            <button
              onClick={handleSaveSettings}
              disabled={saving}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
            >
              {saving ? 'Saving...' : 'Save Settings'}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default SettingsPage;
