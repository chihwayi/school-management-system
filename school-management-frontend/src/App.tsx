import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import { Toaster } from 'react-hot-toast';

// Common Components
import ThemeProvider from './components/common/ThemeProvider';
import LoadingSpinner from './components/common/LoadingSpinner';
import MainLayout from './components/layout/MainLayout';
import ProtectedRoute from './components/common/ProtectedRoute';

// Auth & Setup Pages
import LoginPage from './pages/auth/LoginPage';
import StudentParentLoginPage from './pages/auth/StudentParentLoginPage';
import SchoolSetupPage from './pages/setup/SchoolSetupPage';

// Dashboard Pages
import DashboardPage from './pages/dashboard/DashboardPage';
import StudentDashboard from './pages/student/StudentDashboard';
import StudentFinancePage from './pages/student/StudentFinancePage';
import StudentAssignmentsPage from './pages/student/StudentAssignmentsPage';
import StudentReportsPage from './pages/student/StudentReportsPage';
import StudentReportDetailPage from './pages/student/StudentReportDetailPage';
import ParentDashboard from './pages/parent/ParentDashboard';
import ParentChildrenPage from './pages/parent/ParentChildrenPage';
import ParentFinancePage from './pages/parent/ParentFinancePage';
import ParentReportsPage from './pages/parent/ParentReportsPage';
import ParentProfilePage from './pages/parent/ParentProfilePage';

// Management Pages
import { StudentsPage, StudentDetailPage, StudentSubjectAssignmentPage } from './pages/students';
import StudentEditPage from './pages/students/StudentEditPage';
import { TeachersPage, TeacherDetailPage, TeacherSubjectAssignmentPage } from './pages/teachers';
import { ClassesPage, ClassDetailPage } from './pages/classes';
import { SubjectsPage, SubjectDetailPage } from './pages/subjects';
import { AssessmentsPage, AssessmentDetailPage } from './pages/assessments';
import { AttendancePage, AttendanceDetailPage } from './pages/attendance';
import ReportsPage from './pages/reports/ReportsPage';
import PrintReportsPage from './pages/reports/PrintReportsPage';
import { ReportDetailPage } from './pages/reports';
import { GuardiansPage, GuardianDetailPage } from './pages/guardians';
import { FeePaymentPage, PaymentStatusPage, FinancialReportsPage, FeeSettingsPage } from './pages/fees';
import SectionsPage from './pages/sections/SectionsPage';
import { UserManagementPage } from './pages/users';

// AI Pages
import { 
  AiDashboardPage, 
  AiResourcesPage, 
  AiGeneratePage, 
  AiContentPage, 
  AiAnalyticsPage 
} from './pages/ai';
import AiTemplatesPage from './pages/ai/AiTemplatesPage';
import AiStudentContentPage from './pages/ai/AiStudentContentPage';
import AiProvidersPage from './pages/ai/AiProvidersPage';

// Hooks
import { useAuth } from './hooks/useAuth';

// Create a client instance
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      staleTime: 5 * 60 * 1000, // 5 minutes
      gcTime: 10 * 60 * 1000, // 10 minutes
      refetchOnWindowFocus: false,
      refetchOnMount: false,
      refetchOnReconnect: false,
    },
  },
});

// Route Guard Component
const RouteGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, isSchoolConfigured, isLoading, resetAuth } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Check school setup first, before authentication
  if (!isSchoolConfigured) {
    // If school is not configured but user is authenticated, reset auth
    if (isAuthenticated) {
      resetAuth();
    }
    return <Navigate to="/setup" replace />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

// Root Route Component - handles initial redirect
const RootRoute: React.FC = () => {
  const { isAuthenticated, isSchoolConfigured, isLoading, resetAuth } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  // Check school setup first
  if (!isSchoolConfigured) {
    // If school is not configured but user is authenticated, reset auth
    if (isAuthenticated) {
      resetAuth();
    }
    return <Navigate to="/setup" replace />;
  }

  // Then check authentication
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // If both are good, go to dashboard
  return <Navigate to="/app" replace />;
};

// Dashboard Route Component
const DashboardRoute: React.FC = () => {
  return <DashboardPage />;
};

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Routes>
              {/* Root Route - handles initial redirect */}
              <Route path="/" element={<RootRoute />} />
              
              {/* Setup Route - Should be accessible without auth */}
              <Route path="/setup" element={<SchoolSetupPage />} />
              
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/student/login" element={<StudentParentLoginPage userType="student" />} />
              <Route path="/parent/login" element={<StudentParentLoginPage userType="parent" />} />

              {/* Protected Routes */}
              <Route path="/app" element={
                <RouteGuard>
                  <MainLayout>
                    <Outlet />
                  </MainLayout>
                </RouteGuard>
              }>
                {/* Dashboard */}
                <Route index element={<DashboardRoute />} />
                <Route path="dashboard" element={<DashboardRoute />} />

                {/* Student Management */}
                <Route path="students" element={<StudentsPage />} />
                <Route path="students/:id" element={<StudentDetailPage />} />
                <Route path="students/:id/edit" element={<StudentEditPage />} />
                <Route path="students/subjects" element={<StudentSubjectAssignmentPage />} />
                <Route path="students/:id/guardians/add" element={<div>Add Guardian - Coming Soon</div>} />

                {/* Teacher Management */}
                <Route path="teachers" element={<TeachersPage />} />
                <Route path="teachers/:id" element={<TeacherDetailPage />} />
                <Route path="teachers/:id/edit" element={<TeachersPage />} />
                <Route path="teachers/subjects" element={<TeacherSubjectAssignmentPage />} />

                {/* Class Management */}
                <Route path="classes" element={<ClassesPage />} />
                <Route path="classes/:id" element={<ClassDetailPage />} />

                {/* Subject Management */}
                <Route path="subjects" element={<SubjectsPage />} />
                <Route path="subjects/:id" element={<SubjectDetailPage />} />

                {/* Assessment Management */}
                <Route path="assessments" element={<AssessmentsPage />} />
                <Route path="assessments/:id" element={<AssessmentDetailPage />} />

                {/* Attendance Management */}
                <Route path="attendance" element={<AttendancePage />} />
                <Route path="attendance/:date" element={<AttendanceDetailPage />} />

                {/* Report Management */}
                <Route path="reports" element={<ReportsPage />} />
                <Route path="reports/print" element={<PrintReportsPage />} />
                <Route path="reports/:id" element={<ReportDetailPage />} />

                {/* Guardian Management */}
                <Route path="guardians" element={<GuardiansPage />} />
                <Route path="guardians/:id" element={<GuardianDetailPage />} />

                {/* Fee Management */}
                <Route path="fees/payment" element={<FeePaymentPage />} />
                <Route path="fees/status" element={<PaymentStatusPage />} />
                <Route path="fees/reports" element={<FinancialReportsPage />} />
                <Route path="fees/settings" element={<FeeSettingsPage />} />

                {/* Sections Management */}
                <Route path="sections" element={<SectionsPage />} />
                
                {/* User Management */}
                <Route path="users" element={<UserManagementPage />} />

                {/* AI Assistant Routes - Only for Teachers */}
                <Route path="ai" element={<ProtectedRoute element={<AiDashboardPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/providers" element={<ProtectedRoute element={<AiProvidersPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/resources" element={<ProtectedRoute element={<AiResourcesPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/generate" element={<ProtectedRoute element={<AiGeneratePage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/content" element={<ProtectedRoute element={<AiContentPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/analytics" element={<ProtectedRoute element={<AiAnalyticsPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/templates" element={<ProtectedRoute element={<AiTemplatesPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />
                <Route path="ai/student-content" element={<ProtectedRoute element={<AiStudentContentPage />} roles={['ROLE_TEACHER', 'ROLE_CLASS_TEACHER']} />} />

              </Route>

              {/* Student Portal Routes */}
              <Route path="/student" element={<StudentDashboard />} />
              <Route path="/student/dashboard" element={<StudentDashboard />} />
              <Route path="/student/finance" element={<StudentFinancePage />} />
              <Route path="/student/assignments" element={<StudentAssignmentsPage />} />
              <Route path="/student/reports" element={<StudentReportsPage />} />
              <Route path="/student/reports/:reportId" element={<StudentReportDetailPage />} />

              {/* Parent Portal Routes */}
              <Route path="/parent" element={<ParentDashboard />} />
              <Route path="/parent/dashboard" element={<ParentDashboard />} />
              <Route path="/parent/children" element={<ParentChildrenPage />} />
              <Route path="/parent/finance/:childId" element={<ParentFinancePage />} />
              <Route path="/parent/reports/:childId" element={<ParentReportsPage />} />
              <Route path="/parent/profile/:childId" element={<ParentProfilePage />} />

              {/* Catch all - redirect to dashboard */}
              <Route path="*" element={<Navigate to="/app" replace />} />
            </Routes>

            {/* Global Components */}
            <Toaster
              position="top-right"
              toastOptions={{
                duration: 4000,
                style: {
                  background: '#363636',
                  color: '#fff',
                },
                success: {
                  style: {
                    background: '#22c55e',
                  },
                },
                error: {
                  style: {
                    background: '#ef4444',
                  },
                },
              }}
            />
          </div>
        </Router>
        <ReactQueryDevtools initialIsOpen={false} />
      </ThemeProvider>
    </QueryClientProvider>
  );
};

export default App;