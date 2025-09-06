import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Brain, 
  Upload, 
  Sparkles, 
  BarChart, 
  FileText, 
  BookOpen, 
  Clock, 
  TrendingUp,
  Users,
  Target
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import { useAuth } from '../../hooks/useAuth';
import { aiService } from '../../services/aiService';
// Temporary type definitions to fix the import issue
interface AiUsageStats {
  teacherId: number;
  usageStats: Array<[string, number]>;
  monthlyTokens: number;
  monthlyCost: number;
}

interface UsageLimits {
  monthlyTokens: number;
  maxMonthlyTokens: number;
  monthlyCost: number;
  maxMonthlyCost: number;
  tokenUsagePercent: number;
  costUsagePercent: number;
  tokenWarning: boolean;
  costWarning: boolean;
}

interface AiGeneratedContent {
  id: number;
  teacherId: number;
  teacherName: string;
  subjectId: number;
  subjectName: string;
  title: string;
  description?: string;
  type: string;
  contentData: string;
  markingScheme?: string;
  topicFocus?: string;
  difficultyLevel: string;
  academicYear: string;
  formLevel?: string;
  estimatedDuration?: number;
  totalMarks?: number;
  published: boolean;
  usageCount: number;
  aiModelVersion: string;
  createdAt: string;
  updatedAt: string;
}

interface AiResource {
  id: number;
  teacherId: number;
  teacherName: string;
  subjectId: number;
  subjectName: string;
  title: string;
  description?: string;
  type: string;
  fileName: string;
  fileSize: number;
  fileType: string;
  academicYear: string;
  formLevel?: string;
  processed: boolean;
  processingStatus: string;
  processingNotes?: string;
  createdAt: string;
  updatedAt: string;
}
import { ROUTES } from '../../constants';

const AiDashboardPage: React.FC = () => {
  const { theme } = useAuth();
  const [usageStats, setUsageStats] = useState<AiUsageStats | null>(null);
  const [usageLimits, setUsageLimits] = useState<UsageLimits | null>(null);
  const [recentContent, setRecentContent] = useState<AiGeneratedContent[]>([]);
  const [recentResources, setRecentResources] = useState<AiResource[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [stats, limits, content, resources] = await Promise.all([
          aiService.getTeacherUsageStats(),
          aiService.getUsageLimits(),
          aiService.getTeacherGeneratedContent(),
          aiService.getTeacherResources()
        ]);

        setUsageStats(stats);
        setUsageLimits(limits);
        setRecentContent(content.slice(0, 5)); // Get latest 5
        setRecentResources(resources.slice(0, 5)); // Get latest 5
      } catch (error) {
        console.error('Failed to fetch AI dashboard data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const quickActions = [
    {
      title: 'Upload Resources',
      description: 'Upload syllabus, textbooks, past papers, and other materials',
      icon: Upload,
      href: ROUTES.AI_RESOURCES,
      color: 'bg-blue-500'
    },
    {
      title: 'Generate Content',
      description: 'Create notes, exams, and practice questions',
      icon: Sparkles,
      href: ROUTES.AI_GENERATE,
      color: 'bg-purple-500'
    },
    {
      title: 'View Content',
      description: 'Browse and manage your generated content',
      icon: FileText,
      href: ROUTES.AI_CONTENT,
      color: 'bg-green-500'
    },
    {
      title: 'Analytics',
      description: 'View usage statistics and performance metrics',
      icon: BarChart,
      href: ROUTES.AI_ANALYTICS,
      color: 'bg-orange-500'
    }
  ];

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">AI Assistant Dashboard</h1>
          <p className="text-gray-600">Create, manage, and analyze AI-generated educational content</p>
        </div>
        <div className="flex items-center space-x-2">
          <Brain className="h-8 w-8 text-purple-500" />
        </div>
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {quickActions.map((action) => (
          <Link key={action.title} to={action.href}>
            <Card className="p-6 hover:shadow-lg transition-shadow cursor-pointer">
              <div className="flex items-center space-x-4">
                <div className={`p-3 rounded-lg ${action.color}`}>
                  <action.icon className="h-6 w-6 text-white" />
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900">{action.title}</h3>
                  <p className="text-sm text-gray-600">{action.description}</p>
                </div>
              </div>
            </Card>
          </Link>
        ))}
      </div>

      {/* Usage Warnings */}
      {usageLimits && (usageLimits.tokenWarning || usageLimits.costWarning) && (
        <Card className="p-6 border-l-4 border-yellow-400 bg-yellow-50">
          <div className="flex items-center space-x-3">
            <div className="p-2 rounded-full bg-yellow-100">
              <Target className="h-5 w-5 text-yellow-600" />
            </div>
            <div>
              <h3 className="text-lg font-semibold text-yellow-800">Usage Warning</h3>
              <div className="text-sm text-yellow-700">
                {usageLimits.tokenWarning && (
                  <p>⚠️ Token usage: {usageLimits.tokenUsagePercent.toFixed(1)}% of monthly limit</p>
                )}
                {usageLimits.costWarning && (
                  <p>⚠️ Cost usage: {usageLimits.costUsagePercent.toFixed(1)}% of monthly budget</p>
                )}
              </div>
            </div>
          </div>
        </Card>
      )}

      {/* Stats Overview */}
      {usageStats && usageLimits && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <Card className="p-6">
            <div className="flex items-center space-x-4">
              <div className="p-3 rounded-lg bg-blue-100">
                <TrendingUp className="h-6 w-6 text-blue-600" />
              </div>
              <div className="flex-1">
                <p className="text-sm text-gray-600">Monthly Tokens</p>
                <p className="text-2xl font-bold text-gray-900">
                  {usageStats.monthlyTokens.toLocaleString()}
                </p>
                <div className="mt-2">
                  <div className="flex justify-between text-xs text-gray-500">
                    <span>Usage</span>
                    <span>{usageLimits.tokenUsagePercent.toFixed(1)}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                    <div 
                      className={`h-2 rounded-full ${usageLimits.tokenWarning ? 'bg-yellow-500' : 'bg-blue-500'}`}
                      style={{ width: `${Math.min(usageLimits.tokenUsagePercent, 100)}%` }}
                    ></div>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Limit: {usageLimits.maxMonthlyTokens.toLocaleString()} tokens
                  </p>
                </div>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center space-x-4">
              <div className="p-3 rounded-lg bg-green-100">
                <Target className="h-6 w-6 text-green-600" />
              </div>
              <div className="flex-1">
                <p className="text-sm text-gray-600">Monthly Cost</p>
                <p className="text-2xl font-bold text-gray-900">
                  ${(usageStats.monthlyCost / 100).toFixed(2)}
                </p>
                <div className="mt-2">
                  <div className="flex justify-between text-xs text-gray-500">
                    <span>Usage</span>
                    <span>{usageLimits.costUsagePercent.toFixed(1)}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-2 mt-1">
                    <div 
                      className={`h-2 rounded-full ${usageLimits.costWarning ? 'bg-yellow-500' : 'bg-green-500'}`}
                      style={{ width: `${Math.min(usageLimits.costUsagePercent, 100)}%` }}
                    ></div>
                  </div>
                  <p className="text-xs text-gray-500 mt-1">
                    Budget: ${(usageLimits.maxMonthlyCost / 100).toFixed(2)}
                  </p>
                </div>
              </div>
            </div>
          </Card>

          <Card className="p-6">
            <div className="flex items-center space-x-4">
              <div className="p-3 rounded-lg bg-purple-100">
                <Users className="h-6 w-6 text-purple-600" />
              </div>
              <div>
                <p className="text-sm text-gray-600">Operations</p>
                <p className="text-2xl font-bold text-gray-900">
                  {usageStats.usageStats.reduce((sum, [, count]) => sum + count, 0)}
                </p>
              </div>
            </div>
          </Card>
        </div>
      )}

      {/* Recent Content */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Recent Generated Content */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Recent Generated Content</h3>
            <Link to={ROUTES.AI_CONTENT}>
              <Button variant="outline" size="sm">View All</Button>
            </Link>
          </div>
          
          {recentContent.length > 0 ? (
            <div className="space-y-3">
              {recentContent.map((content) => (
                <div key={content.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div className="flex items-center space-x-3">
                    <Sparkles className="h-4 w-4 text-purple-500" />
                    <div>
                      <p className="font-medium text-gray-900">{content.title}</p>
                      <p className="text-sm text-gray-600">{content.subjectName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-gray-600">{formatDate(content.createdAt)}</p>
                    <p className="text-xs text-gray-500">{content.type}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <Sparkles className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No content generated yet</p>
              <Link to={ROUTES.AI_GENERATE}>
                <Button className="mt-2">Generate Your First Content</Button>
              </Link>
            </div>
          )}
        </Card>

        {/* Recent Uploaded Resources */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-lg font-semibold text-gray-900">Recent Uploaded Resources</h3>
            <Link to={ROUTES.AI_RESOURCES}>
              <Button variant="outline" size="sm">View All</Button>
            </Link>
          </div>
          
          {recentResources.length > 0 ? (
            <div className="space-y-3">
              {recentResources.map((resource) => (
                <div key={resource.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div className="flex items-center space-x-3">
                    <BookOpen className="h-4 w-4 text-blue-500" />
                    <div>
                      <p className="font-medium text-gray-900">{resource.title}</p>
                      <p className="text-sm text-gray-600">{resource.subjectName}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-gray-600">{formatFileSize(resource.fileSize)}</p>
                    <p className="text-xs text-gray-500">{resource.type}</p>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="text-center py-8">
              <Upload className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No resources uploaded yet</p>
              <Link to={ROUTES.AI_RESOURCES}>
                <Button className="mt-2">Upload Your First Resource</Button>
              </Link>
            </div>
          )}
        </Card>
      </div>

      {/* Getting Started Guide */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Getting Started with AI Assistant</h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="text-center">
            <div className="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-3">
              <span className="text-blue-600 font-bold">1</span>
            </div>
            <h4 className="font-medium text-gray-900 mb-2">Upload Resources</h4>
            <p className="text-sm text-gray-600">Start by uploading syllabus, textbooks, and past papers to train the AI</p>
          </div>
          <div className="text-center">
            <div className="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-3">
              <span className="text-purple-600 font-bold">2</span>
            </div>
            <h4 className="font-medium text-gray-900 mb-2">Generate Content</h4>
            <p className="text-sm text-gray-600">Use AI to create notes, exams, and practice questions based on your resources</p>
          </div>
          <div className="text-center">
            <div className="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-3">
              <span className="text-green-600 font-bold">3</span>
            </div>
            <h4 className="font-medium text-gray-900 mb-2">Share & Use</h4>
            <p className="text-sm text-gray-600">Publish content for students and track usage analytics</p>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AiDashboardPage;
