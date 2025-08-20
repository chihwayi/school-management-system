import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { 
  HomeIcon, 
  BuildingOfficeIcon, 
  UsersIcon, 
  ChartBarIcon,
  CogIcon,
  ArrowRightOnRectangleIcon
} from '@heroicons/react/24/outline';
import { useAuth } from '../hooks/useAuth';
import toast from 'react-hot-toast';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const navigation = [
    { name: 'Dashboard', href: '/', icon: HomeIcon },
    { name: 'Schools', href: '/schools', icon: BuildingOfficeIcon },
    { name: 'Users', href: '/users', icon: UsersIcon },
    { name: 'Analytics', href: '/analytics', icon: ChartBarIcon },
    { name: 'Settings', href: '/settings', icon: CogIcon },
  ];

  const handleLogout = () => {
    logout();
    toast.success('Logged out successfully');
    navigate('/login');
  };

  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <div className="hidden md:flex md:flex-shrink-0">
        <div className="flex flex-col w-64">
          <div className="flex flex-col flex-grow pt-5 overflow-y-auto bg-white border-r border-gray-200">
                               <div className="flex items-center flex-shrink-0 px-4">
                     <h1 className="text-xl font-semibold text-gray-900">
                       School Management Admin
                     </h1>
                   </div>
                   <div className="flex items-center px-4 py-2 border-b border-gray-200">
                     <div className="flex items-center space-x-3">
                       <div className="h-8 w-8 bg-blue-100 rounded-full flex items-center justify-center">
                         <span className="text-sm font-medium text-blue-600">
                           {user?.username?.charAt(0).toUpperCase()}
                         </span>
                       </div>
                       <div>
                         <p className="text-sm font-medium text-gray-900">{user?.username}</p>
                         <p className="text-xs text-gray-500 capitalize">{user?.role}</p>
                       </div>
                     </div>
                   </div>
            <div className="mt-5 flex-grow flex flex-col">
              <nav className="flex-1 px-2 pb-4 space-y-1">
                {navigation.map((item) => {
                  const isActive = location.pathname === item.href;
                  return (
                    <Link
                      key={item.name}
                      to={item.href}
                      className={`${
                        isActive
                          ? 'bg-gray-100 text-gray-900'
                          : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                      } group flex items-center px-2 py-2 text-sm font-medium rounded-md`}
                    >
                      <item.icon
                        className={`${
                          isActive ? 'text-gray-500' : 'text-gray-400 group-hover:text-gray-500'
                        } mr-3 flex-shrink-0 h-6 w-6`}
                        aria-hidden="true"
                      />
                      {item.name}
                    </Link>
                  );
                })}
                                   </nav>
                   </div>
                   <div className="flex-shrink-0 p-4 border-t border-gray-200">
                     <button
                       onClick={handleLogout}
                       className="flex items-center w-full px-2 py-2 text-sm font-medium text-gray-600 rounded-md hover:bg-gray-50 hover:text-gray-900"
                     >
                       <ArrowRightOnRectangleIcon className="mr-3 h-5 w-5" />
                       Logout
                     </button>
                   </div>
                 </div>
               </div>
             </div>

      {/* Main content */}
      <div className="flex flex-col w-0 flex-1 overflow-hidden">
        <main className="flex-1 relative overflow-y-auto focus:outline-none">
          <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
              {children}
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;


