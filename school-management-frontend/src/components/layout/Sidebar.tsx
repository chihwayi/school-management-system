import React, { Fragment, useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Dialog, Transition } from '@headlessui/react';
import {
    X,
    Home,
    Users,
    GraduationCap,
    BookOpen,
    FileText,
    ClipboardList,
    UserCheck,
    BarChart3,
    Shield,
    DollarSign,
    Brain,
    Upload,
    Sparkles,
    BarChart,
    Plus,
    Copy,
    ChevronDown,
    ChevronRight,
    Settings
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { ROUTES } from '../../constants';
import { cn } from '../../utils';
import { getImageUrl } from '../../utils/imageUtils';

interface SidebarProps {
    isOpen: boolean;
    onClose: () => void;
}

const Sidebar: React.FC<SidebarProps> = ({ isOpen, onClose }) => {
    const location = useLocation();
    const { isAdmin, isClerk, isTeacher, isClassTeacher, school, theme } = useAuth();
    const [expandedItems, setExpandedItems] = useState<string[]>([]);

    // Auto-expand AI Assistant when on AI pages
    useEffect(() => {
        if (location.pathname.startsWith('/app/ai') && !expandedItems.includes('AI Assistant')) {
            setExpandedItems([...expandedItems, 'AI Assistant']);
        }
    }, [location.pathname, expandedItems]);
    

    
    const primaryColor = theme?.primaryColor || '#1F2937';

    const navigation = [
        {
            name: 'Dashboard',
            href: ROUTES.DASHBOARD,
            icon: Home,
            current: location.pathname === ROUTES.DASHBOARD,
            show: true
        },
        {
            name: 'Students',
            href: ROUTES.STUDENTS,
            icon: Users,
            current: location.pathname === ROUTES.STUDENTS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Teachers',
            href: ROUTES.TEACHERS,
            icon: GraduationCap,
            current: location.pathname === ROUTES.TEACHERS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Classes',
            href: ROUTES.CLASSES,
            icon: BookOpen,
            current: location.pathname === ROUTES.CLASSES,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Sections',
            href: ROUTES.SECTIONS,
            icon: BookOpen,
            current: location.pathname === ROUTES.SECTIONS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'User Management',
            href: '/app/users',
            icon: Shield,
            current: location.pathname === '/app/users',
            show: isAdmin()
        },
        {
            name: 'AI Provider Config',
            href: '/app/admin/ai-providers',
            icon: Settings,
            current: location.pathname === '/app/admin/ai-providers',
            show: isAdmin()
        },
        {
            name: 'Subjects',
            href: ROUTES.SUBJECTS,
            icon: FileText,
            current: location.pathname === ROUTES.SUBJECTS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Subject Assignment',
            href: '/app/students/subjects',
            icon: BookOpen,
            current: location.pathname === '/app/students/subjects',
            show: isAdmin() || isClerk()
        },
        {
            name: 'Assessments',
            href: ROUTES.ASSESSMENTS,
            icon: ClipboardList,
            current: location.pathname === ROUTES.ASSESSMENTS,
            show: isTeacher() || isClassTeacher()
        },
        {
            name: 'Attendance',
            href: ROUTES.ATTENDANCE,
            icon: UserCheck,
            current: location.pathname === ROUTES.ATTENDANCE,
            show: isClassTeacher()
        },
        {
            name: 'Student Reports',
            href: ROUTES.REPORTS,
            icon: BarChart3,
            current: location.pathname === ROUTES.REPORTS,
            show: isTeacher() || isClassTeacher()
        },
        {
            name: 'Print Reports',
            href: '/app/reports/print',
            icon: FileText,
            current: location.pathname === '/app/reports/print',
            show: isAdmin() || isClerk()
        },
        {
            name: 'Guardians',
            href: ROUTES.GUARDIANS,
            icon: Shield,
            current: location.pathname === ROUTES.GUARDIANS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Fee Payment',
            href: ROUTES.FEES_PAYMENT,
            icon: DollarSign,
            current: location.pathname === ROUTES.FEES_PAYMENT,
            show: isClerk()
        },
        {
            name: 'Payment Status',
            href: ROUTES.FEES_STATUS,
            icon: Users,
            current: location.pathname === ROUTES.FEES_STATUS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'Financial Reports',
            href: ROUTES.FEES_REPORTS,
            icon: BarChart3,
            current: location.pathname === ROUTES.FEES_REPORTS,
            show: isAdmin()
        },
        {
            name: 'Fee Settings',
            href: ROUTES.FEES_SETTINGS,
            icon: DollarSign,
            current: location.pathname === ROUTES.FEES_SETTINGS,
            show: isAdmin() || isClerk()
        },
        {
            name: 'AI Assistant',
            href: ROUTES.AI_DASHBOARD,
            icon: Brain,
            current: location.pathname.startsWith('/app/ai'),
            show: isTeacher() || isClassTeacher(), // Only visible to teachers
            children: [
                { name: 'Dashboard', href: ROUTES.AI_DASHBOARD, current: location.pathname === ROUTES.AI_DASHBOARD },
                { name: 'AI Providers', href: ROUTES.AI_PROVIDERS, current: location.pathname === ROUTES.AI_PROVIDERS, show: isTeacher() || isClassTeacher() },
                { name: 'Resources', href: ROUTES.AI_RESOURCES, current: location.pathname === ROUTES.AI_RESOURCES, show: isTeacher() || isClassTeacher() },
                { name: 'Generate Content', href: ROUTES.AI_GENERATE, current: location.pathname === ROUTES.AI_GENERATE, show: isTeacher() || isClassTeacher() },
                { name: 'My Content', href: ROUTES.AI_CONTENT, current: location.pathname === ROUTES.AI_CONTENT, show: isTeacher() || isClassTeacher() },
                { name: 'Templates', href: ROUTES.AI_TEMPLATES, current: location.pathname === ROUTES.AI_TEMPLATES, show: isTeacher() || isClassTeacher() },
                { name: 'Analytics', href: ROUTES.AI_ANALYTICS, current: location.pathname === ROUTES.AI_ANALYTICS, show: isTeacher() || isClassTeacher() },
                { name: 'Learning Resources', href: ROUTES.AI_STUDENT_CONTENT, current: location.pathname === ROUTES.AI_STUDENT_CONTENT, show: isTeacher() || isClassTeacher() }
            ]
        }
    ];

    const visibleNavigation = navigation.filter(item => item.show);

    const SidebarContent = () => (
        <div className="flex flex-col h-full max-h-screen bg-gray-900">
            {/* School Logo/Brand */}
            <div 
                className="flex items-center justify-center h-16 px-4"
                style={{ backgroundColor: primaryColor }}
            >
                <div className="flex items-center">
                    {theme?.logoPath ? (
                        <img
                            src={getImageUrl(theme.logoPath) || ''}
                            alt={school?.name || 'School Logo'}
                            className="h-8 w-8 rounded-full object-cover"
                            onError={(e) => {
                                console.error('Failed to load sidebar logo:', theme.logoPath);
                                e.currentTarget.style.display = 'none';
                            }}
                        /> 
                    ) : (
                        <div 
                            className="h-8 w-8 rounded-full flex items-center justify-center"
                            style={{ backgroundColor: theme?.secondaryColor || '#374151' }}
                        >
                            <span className="text-white font-bold text-sm">
                                {school?.name?.charAt(0) || 'S'}
                            </span>
                        </div>
                    )}
                    <div className="ml-3">
                        <h2 className="text-white text-sm font-medium truncate">
                            {school?.name || 'School Management'}
                        </h2>
                    </div>
                </div>
            </div>

            {/* Navigation */}
            <nav className="flex-1 px-4 py-4 space-y-1 overflow-y-auto sidebar-scroll">
                {visibleNavigation.map((item) => (
                    <div key={item.name}>
                        {item.children ? (
                            // Nested navigation item
                            <div>
                                <button
                                    onClick={() => {
                                        const isExpanded = expandedItems.includes(item.name);
                                        if (isExpanded) {
                                            setExpandedItems(expandedItems.filter(name => name !== item.name));
                                        } else {
                                            setExpandedItems([...expandedItems, item.name]);
                                        }
                                    }}
                                    className={cn(
                                        'group flex items-center justify-between w-full px-3 py-2 text-sm font-medium rounded-md transition-colors',
                                        item.current
                                            ? 'text-white'
                                            : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                                    )}
                                    style={item.current ? { backgroundColor: primaryColor } : {}}
                                >
                                    <div className="flex items-center">
                                        <item.icon
                                            className={cn(
                                                'mr-3 h-5 w-5 flex-shrink-0',
                                                item.current
                                                    ? 'text-white'
                                                    : 'text-gray-400 group-hover:text-white'
                                            )}
                                        />
                                        {item.name}
                                    </div>
                                    {expandedItems.includes(item.name) ? (
                                        <ChevronDown className="h-4 w-4 text-gray-400" />
                                    ) : (
                                        <ChevronRight className="h-4 w-4 text-gray-400" />
                                    )}
                                </button>
                                
                                {expandedItems.includes(item.name) && (
                                    <div className="ml-6 mt-1 space-y-1">
                                        {item.children
                                            .filter(child => child.show !== false)
                                            .map((child) => (
                                                <Link
                                                    key={child.name}
                                                    to={child.href}
                                                    className={cn(
                                                        'group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                                                        child.current
                                                            ? 'text-white'
                                                            : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                                                    )}
                                                    style={child.current ? { backgroundColor: primaryColor } : {}}
                                                    onClick={onClose}
                                                >
                                                    {child.name}
                                                </Link>
                                            ))}
                                    </div>
                                )}
                            </div>
                        ) : (
                            // Regular navigation item
                            <Link
                                to={item.href}
                                className={cn(
                                    'group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                                    item.current
                                        ? 'text-white'
                                        : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                                )}
                                style={item.current ? { backgroundColor: primaryColor } : {}}
                                onClick={onClose}
                            >
                                <item.icon
                                    className={cn(
                                        'mr-3 h-5 w-5 flex-shrink-0',
                                        item.current
                                            ? 'text-white'
                                            : 'text-gray-400 group-hover:text-white'
                                    )}
                                />
                                {item.name}
                            </Link>
                        )}
                    </div>
                ))}
            </nav>

            {/* Footer */}
            <div className="flex-shrink-0 p-4 border-t border-gray-700">
                <div className="text-xs text-gray-400 text-center">
                    © {new Date().getFullYear()} {school?.name || 'School Management'}
                </div>
            </div>
        </div>
    );

    return (
        <>
            {/* Mobile sidebar */}
            <Transition show={isOpen} as={Fragment}>
                <Dialog as="div" className="relative z-50 lg:hidden" onClose={onClose}>
                    <Transition.Child
                        as={Fragment}
                        enter="transition-opacity ease-linear duration-300"
                        enterFrom="opacity-0"
                        enterTo="opacity-100"
                        leave="transition-opacity ease-linear duration-300"
                        leaveFrom="opacity-100"
                        leaveTo="opacity-0"
                    >
                        <div className="fixed inset-0 bg-gray-600 bg-opacity-75" />
                    </Transition.Child>

                    <div className="fixed inset-0 flex z-40">
                        <Transition.Child
                            as={Fragment}
                            enter="transition ease-in-out duration-300 transform"
                            enterFrom="-translate-x-full"
                            enterTo="translate-x-0"
                            leave="transition ease-in-out duration-300 transform"
                            leaveFrom="translate-x-0"
                            leaveTo="-translate-x-full"
                        >
                            <Dialog.Panel className="relative flex w-full max-w-xs flex-1 flex-col">
                                <Transition.Child
                                    as={Fragment}
                                    enter="ease-in-out duration-300"
                                    enterFrom="opacity-0"
                                    enterTo="opacity-100"
                                    leave="ease-in-out duration-300"
                                    leaveFrom="opacity-100"
                                    leaveTo="opacity-0"
                                >
                                    <div className="absolute top-0 right-0 -mr-12 pt-2">
                                        <button
                                            type="button"
                                            className="ml-1 flex h-10 w-10 items-center justify-center rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white"
                                            onClick={onClose}
                                        >
                                            <span className="sr-only">Close sidebar</span>
                                            <X className="h-6 w-6 text-white" />
                                        </button>
                                    </div>
                                </Transition.Child>
                                <SidebarContent />
                            </Dialog.Panel>
                        </Transition.Child>
                    </div>
                </Dialog>
            </Transition>

            {/* Desktop sidebar */}
            <div className="hidden lg:fixed lg:inset-y-0 lg:z-50 lg:flex lg:w-64 lg:flex-col">
                <SidebarContent />
            </div>
        </>
    );
};

export default Sidebar;