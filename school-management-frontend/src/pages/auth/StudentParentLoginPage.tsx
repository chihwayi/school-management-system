import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Input } from '../../components/ui';
import { Phone, Lock, Eye, EyeOff, GraduationCap, Users, ArrowRight } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import { getImageUrl } from '../../utils/imageUtils';
import { studentParentAuthService } from '../../services/studentParentAuthService';

interface StudentParentLoginPageProps {
  userType: 'student' | 'parent';
}

const StudentParentLoginPage: React.FC<StudentParentLoginPageProps> = ({ userType }) => {
  const navigate = useNavigate();
  const { login, school, theme } = useAuth();
  const [step, setStep] = useState<'phone' | 'password'>('phone');
  const [isFirstTime, setIsFirstTime] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [mobileNumber, setMobileNumber] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [validationErrors, setValidationErrors] = useState<Record<string, string>>({});

  const checkFirstTimeLogin = async () => {
    if (!mobileNumber) return;
    
    try {
      setIsLoading(true);
      const response = await studentParentAuthService.checkFirstTime(mobileNumber, userType.toUpperCase() as 'STUDENT' | 'PARENT');
      setIsFirstTime(response.isFirstTime);
      setStep('password');
    } catch (error) {
      console.error('Error checking first time login:', error);
      toast.error('Failed to verify mobile number. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const handlePhoneSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!mobileNumber) {
      setValidationErrors({ mobileNumber: 'Mobile number is required' });
      return;
    }
    
    if (!/^\+?[1-9]\d{1,14}$/.test(mobileNumber.replace(/\s/g, ''))) {
      setValidationErrors({ mobileNumber: 'Please enter a valid mobile number' });
      return;
    }
    
    setValidationErrors({});
    await checkFirstTimeLogin();
  };

  const handlePasswordSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Validate password
    if (isFirstTime) {
      if (!password) {
        setValidationErrors({ password: 'Password is required' });
        return;
      }
      if (password.length < 6) {
        setValidationErrors({ password: 'Password must be at least 6 characters' });
        return;
      }
      if (!confirmPassword) {
        setValidationErrors({ confirmPassword: 'Please confirm your password' });
        return;
      }
      if (password !== confirmPassword) {
        setValidationErrors({ confirmPassword: 'Passwords do not match' });
        return;
      }
    } else {
      if (!password) {
        setValidationErrors({ password: 'Password is required' });
        return;
      }
    }
    
    setValidationErrors({});
    setIsLoading(true);
    
    try {
      let response;
      
      if (isFirstTime) {
        // First time login - create password
        response = await studentParentAuthService.firstTimeLogin({
          mobileNumber,
          password,
          userType: userType.toUpperCase() as 'STUDENT' | 'PARENT'
        });
      } else {
        // Regular login
        response = await studentParentAuthService.login({
          mobileNumber,
          password,
          userType: userType.toUpperCase() as 'STUDENT' | 'PARENT'
        });
      }
      
      // Store token
      localStorage.setItem('studentParentToken', response.token);
      localStorage.setItem('userType', userType);
      localStorage.setItem('mobileNumber', mobileNumber);
      
      toast.success(response.message);
      
      // Redirect to appropriate dashboard
      if (userType === 'student') {
        navigate('/student/dashboard');
      } else {
        navigate('/parent/dashboard');
      }
      
    } catch (error: any) {
      console.error('Login error:', error);
      toast.error(error.response?.data?.error || 'Login failed. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const goBackToPhone = () => {
    setStep('phone');
    setPassword('');
    setConfirmPassword('');
    setValidationErrors({});
  };



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
            {userType === 'student' ? 'Student Portal' : 'Parent Portal'}
          </h2>
          <p 
            className="mt-2 text-center text-sm"
            style={{ color: theme?.backgroundPath ? 'rgba(255,255,255,0.8)' : '#6B7280' }}
          >
            Sign in to {school?.name || 'your school'}
          </p>
        </div>
        
        {step === 'phone' ? (
          // Step 1: Phone Number Entry
          <Card className="p-6">
            <div className="flex items-center justify-center mb-6">
              <div className="p-3 rounded-full mr-3" style={{ backgroundColor: `${primaryColor}20` }}>
                {userType === 'student' ? (
                  <GraduationCap className="h-6 w-6" style={{ color: primaryColor }} />
                ) : (
                  <Users className="h-6 w-6" style={{ color: primaryColor }} />
                )}
              </div>
              <h2 className="text-xl font-semibold text-gray-900">
                {userType === 'student' ? 'Student Login' : 'Parent Login'}
              </h2>
            </div>

            <form onSubmit={handlePhoneSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Mobile Number
                </label>
                <div className="relative">
                  <Phone className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                  <Input
                    type="tel"
                    value={mobileNumber}
                    onChange={(e) => {
                      setMobileNumber(e.target.value);
                      if (validationErrors.mobileNumber) {
                        setValidationErrors(prev => ({ ...prev, mobileNumber: '' }));
                      }
                    }}
                    placeholder="Enter your mobile number"
                    className="pl-10"
                    error={validationErrors.mobileNumber}
                  />
                </div>
              </div>

              <Button
                type="submit"
                className="w-full"
                loading={isLoading}
              >
                Continue <ArrowRight className="h-4 w-4 ml-2" />
              </Button>
            </form>
          </Card>
        ) : (
          // Step 2: Password Entry
          <Card className="p-6">
            <div className="flex items-center justify-center mb-6">
              <div className="p-3 rounded-full mr-3" style={{ backgroundColor: `${primaryColor}20` }}>
                {userType === 'student' ? (
                  <GraduationCap className="h-6 w-6" style={{ color: primaryColor }} />
                ) : (
                  <Users className="h-6 w-6" style={{ color: primaryColor }} />
                )}
              </div>
              <div>
                <h2 className="text-xl font-semibold text-gray-900">
                  {isFirstTime ? 'Create Password' : 'Enter Password'}
                </h2>
                <p className="text-sm text-gray-600">{mobileNumber}</p>
              </div>
            </div>

            <form onSubmit={handlePasswordSubmit} className="space-y-4">
              {isFirstTime ? (
                // First time login - create password
                <>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Create Password
                    </label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        type={showPassword ? 'text' : 'password'}
                        value={password}
                        onChange={(e) => {
                          setPassword(e.target.value);
                          if (validationErrors.password) {
                            setValidationErrors(prev => ({ ...prev, password: '' }));
                          }
                        }}
                        placeholder="Create a password"
                        className="pl-10 pr-10"
                        error={validationErrors.password}
                      />
                      <button
                        type="button"
                        onClick={() => setShowPassword(!showPassword)}
                        className="absolute right-3 top-1/2 transform -translate-y-1/2"
                      >
                        {showPassword ? (
                          <EyeOff className="h-4 w-4 text-gray-400" />
                        ) : (
                          <Eye className="h-4 w-4 text-gray-400" />
                        )}
                      </button>
                    </div>
                  </div>

                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      Confirm Password
                    </label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                      <Input
                        type={showPassword ? 'text' : 'password'}
                        value={confirmPassword}
                        onChange={(e) => {
                          setConfirmPassword(e.target.value);
                          if (validationErrors.confirmPassword) {
                            setValidationErrors(prev => ({ ...prev, confirmPassword: '' }));
                          }
                        }}
                        placeholder="Confirm your password"
                        className="pl-10"
                        error={validationErrors.confirmPassword}
                      />
                    </div>
                  </div>
                </>
              ) : (
                // Regular login
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    Password
                  </label>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
                    <Input
                      type={showPassword ? 'text' : 'password'}
                      value={password}
                      onChange={(e) => {
                        setPassword(e.target.value);
                        if (validationErrors.password) {
                          setValidationErrors(prev => ({ ...prev, password: '' }));
                        }
                      }}
                      placeholder="Enter your password"
                      className="pl-10 pr-10"
                      error={validationErrors.password}
                    />
                    <button
                      type="button"
                      onClick={() => setShowPassword(!showPassword)}
                      className="absolute right-3 top-1/2 transform -translate-y-1/2"
                    >
                      {showPassword ? (
                        <EyeOff className="h-4 w-4 text-gray-400" />
                      ) : (
                        <Eye className="h-4 w-4 text-gray-400" />
                      )}
                    </button>
                  </div>
                </div>
              )}

              <Button
                type="submit"
                className="w-full"
                loading={isLoading}
              >
                {isFirstTime ? 'Create Account & Login' : 'Login'}
              </Button>

              <div className="text-center">
                <button
                  type="button"
                  onClick={goBackToPhone}
                  className="text-sm text-gray-600 hover:text-gray-800"
                >
                  ← Use different mobile number
                </button>
              </div>
            </form>
          </Card>
        )}
        
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

        {/* Back to main login */}
        <div className="text-center">
          <button
            onClick={() => navigate('/login')}
            className="text-sm font-medium hover:underline"
            style={{ color: theme?.backgroundPath ? 'rgba(255,255,255,0.9)' : '#3B82F6' }}
          >
            ← Back to Staff Login
          </button>
        </div>
      </div>
    </div>
  );
};

export default StudentParentLoginPage;
