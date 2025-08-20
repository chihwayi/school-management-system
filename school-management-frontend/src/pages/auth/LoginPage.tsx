import React, { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { LoginForm } from '../../components/forms';
import { useAuth } from '../../hooks/useAuth';
import type { LoginRequest } from '../../types';
import LoadingSpinner  from '../../components/common/LoadingSpinner';
import TenantSelector from '../../components/common/TenantSelector';
import { toast } from 'react-hot-toast';
import { getImageUrl } from '../../utils/imageUtils';
import { getCurrentTenant } from '../../utils/tenant';

const LoginPage: React.FC = () => {
  const { isAuthenticated, isSchoolConfigured, isLoading, login, school, theme } = useAuth();
  const [loginLoading, setLoginLoading] = useState(false);
  const [loginError, setLoginError] = useState<string>('');

  const handleLogin = async (data: LoginRequest) => {
    try {
      setLoginLoading(true);
      setLoginError('');
      console.log('LoginPage: Starting login process');
      await login(data);
      console.log('LoginPage: Login successful');
    } catch (error: any) {
      console.error('LoginPage: Login failed:', error);
      const errorMessage = error.message || 'Login failed - please check your credentials';
      setLoginError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoginLoading(false);
    }
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Check tenant first
  const currentTenant = getCurrentTenant();
  if (!currentTenant) {
    return <Navigate to="/select-school" replace />;
  }

  // Check school configuration
  if (!isSchoolConfigured) {
    return <Navigate to="/setup" replace />;
  }

  if (isAuthenticated) {
    return <Navigate to="/app" replace />;
  }

  const backgroundStyle = theme?.backgroundPath 
    ? { backgroundImage: `url(${getImageUrl(theme.backgroundPath)})`, backgroundSize: 'cover', backgroundPosition: 'center' }
    : {};

  const primaryColor = theme?.primaryColor || '#3B82F6';

  return (
    <div 
      className="min-h-screen flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8"
      style={{
        ...backgroundStyle,
        backgroundColor: theme?.backgroundPath ? 'transparent' : '#F9FAFB'
      }}
    >
      {theme?.backgroundPath && (
        <div className="absolute inset-0 bg-black bg-opacity-40"></div>
      )}
      <div className="max-w-md w-full space-y-8 relative z-10">
        <div className="text-center">
          {theme?.logoPath && (
            <img
              src={getImageUrl(theme.logoPath)}
              alt={school?.name || 'School Logo'}
              className="mx-auto h-16 w-auto mb-4"
              onError={(e) => {
                console.error('Failed to load logo:', theme.logoPath);
                e.currentTarget.style.display = 'none';
              }}
            />
          )}
          <h2 
            className="mt-6 text-center text-3xl font-extrabold"
            style={{ color: theme?.backgroundPath ? 'white' : '#111827' }}
          >
            Sign in to {school?.name || 'your school'}
          </h2>
          <p 
            className="mt-2 text-center text-sm"
            style={{ color: theme?.backgroundPath ? 'rgba(255,255,255,0.8)' : '#6B7280' }}
          >
            Enter your credentials to access the school management system
          </p>
        </div>
        
        <div className="mt-8 space-y-6">
          <div 
            className="bg-transparent rounded-lg p-8"
            style={{
              border: `3px solid ${primaryColor}`,
              color: 'white',
              textShadow: '2px 2px 4px rgba(0,0,0,0.8)'
            }}
          >
            <LoginForm 
              onSubmit={handleLogin}
              isLoading={loginLoading}
              error={loginError}
            />
          </div>
        </div>
        
        <div className="text-center">
          <p 
            className="text-xs"
            style={{ color: theme?.backgroundPath ? 'rgba(255,255,255,0.7)' : '#6B7280' }}
          >
            Contact your school administrator for login credentials
          </p>
          {school?.contactEmail && (
            <p 
              className="text-xs mt-1"
              style={{ color: theme?.backgroundPath ? 'rgba(255,255,255,0.7)' : '#6B7280' }}
            >
              Email: {school.contactEmail}
            </p>
          )}
        </div>
      </div>
    </div>
  );
};

export default LoginPage;