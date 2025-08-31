import React, { useState, useEffect } from 'react';
import { 
  FileText, 
  Eye, 
  Download, 
  Share, 
  Trash2, 
  Edit, 
  Search,
  Filter,
  Calendar,
  Users,
  Target,
  Sparkles,
  MessageCircle,
  Trash,
  Copy,
  Archive,
  CheckSquare,
  Square
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Select from '../../components/ui/Select';
import Textarea from '../../components/ui/Textarea';
import Modal from '../../components/ui/Modal';
import Badge from '../../components/ui/Badge';
import { useAuth } from '../../hooks/useAuth';
import { aiService } from '../../services/aiService';
import { subjectService } from '../../services/subjectService';
// Temporary type definitions to fix the import issue
enum ContentType {
  STUDY_NOTES = 'STUDY_NOTES',
  PRACTICE_QUESTIONS = 'PRACTICE_QUESTIONS',
  QUIZ = 'QUIZ',
  MIDTERM_EXAM = 'MIDTERM_EXAM',
  FINAL_EXAM = 'FINAL_EXAM',
  ASSIGNMENT = 'ASSIGNMENT',
  REVISION_MATERIAL = 'REVISION_MATERIAL'
}

enum DifficultyLevel {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
  EXPERT = 'EXPERT'
}

const CONTENT_TYPE_LABELS = {
  [ContentType.STUDY_NOTES]: 'Study Notes',
  [ContentType.PRACTICE_QUESTIONS]: 'Practice Questions',
  [ContentType.QUIZ]: 'Quiz',
  [ContentType.MIDTERM_EXAM]: 'Midterm Exam',
  [ContentType.FINAL_EXAM]: 'Final Exam',
  [ContentType.ASSIGNMENT]: 'Assignment',
  [ContentType.REVISION_MATERIAL]: 'Revision Material'
};

const DIFFICULTY_LEVEL_LABELS = {
  [DifficultyLevel.EASY]: 'Easy',
  [DifficultyLevel.MEDIUM]: 'Medium',
  [DifficultyLevel.HARD]: 'Hard',
  [DifficultyLevel.EXPERT]: 'Expert'
};

interface AiGeneratedContent {
  id: number;
  teacherId: number;
  teacherName: string;
  subjectId: number;
  subjectName: string;
  title: string;
  description?: string;
  type: ContentType;
  contentData: string;
  markingScheme?: string;
  topicFocus?: string;
  difficultyLevel: DifficultyLevel;
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

interface Subject {
  id: number;
  name: string;
  code: string;
  description?: string;
}

const AiContentPage: React.FC = () => {
  const { theme } = useAuth();
  const [content, setContent] = useState<AiGeneratedContent[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [showContentModal, setShowContentModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedItems, setSelectedItems] = useState<number[]>([]);
  const [bulkActionMode, setBulkActionMode] = useState(false);
  const [bulkAction, setBulkAction] = useState('');
  const [showBulkModal, setShowBulkModal] = useState(false);
  const [showWhatsAppModal, setShowWhatsAppModal] = useState(false);
  const [selectedContent, setSelectedContent] = useState<AiGeneratedContent | null>(null);
  const [filters, setFilters] = useState({
    search: '',
    subjectId: '',
    contentType: '',
    difficultyLevel: '',
    published: ''
  });
  const [editForm, setEditForm] = useState({
    title: '',
    description: '',
    published: false
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [contentData, subjectsData] = await Promise.all([
          aiService.getTeacherGeneratedContent(),
          subjectService.getAllSubjects()
        ]);
        setContent(contentData);
        setSubjects(subjectsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handlePublish = async (contentId: number) => {
    try {
      const updatedContent = await aiService.publishContent(contentId);
      setContent(prev => 
        prev.map(c => c.id === contentId ? updatedContent : c)
      );
    } catch (error) {
      console.error('Failed to publish content:', error);
      alert('Failed to publish content. Please try again.');
    }
  };

  const handleViewContent = (content: AiGeneratedContent) => {
    setSelectedContent(content);
    setShowContentModal(true);
  };

  const handleEditContent = (content: AiGeneratedContent) => {
    setSelectedContent(content);
    setEditForm({
      title: content.title,
      description: content.description || '',
      published: content.published
    });
    setShowEditModal(true);
  };

  const handleUpdateContent = async () => {
    if (!selectedContent || !editForm.title) {
      alert('Please fill in all required fields');
      return;
    }

    try {
      const updatedContent = await aiService.updateContent(
        selectedContent.id,
        editForm.title,
        editForm.description,
        editForm.published
      );

      setContent(content.map(c => c.id === selectedContent.id ? updatedContent : c));
      setShowEditModal(false);
      setSelectedContent(null);
    } catch (error) {
      console.error('Failed to update content:', error);
      alert('Failed to update content. Please try again.');
    }
  };

  const handleDeleteContent = (content: AiGeneratedContent) => {
    setSelectedContent(content);
    setShowDeleteModal(true);
  };

  const handleConfirmDeleteContent = async () => {
    if (!selectedContent) return;

    try {
      await aiService.deleteContent(selectedContent.id);
      setContent(content.filter(c => c.id !== selectedContent.id));
      setShowDeleteModal(false);
      setSelectedContent(null);
    } catch (error) {
      console.error('Failed to delete content:', error);
      alert('Failed to delete content. Please try again.');
    }
  };

  const handleExportContent = async (content: AiGeneratedContent, format: string) => {
    try {
      await aiService.exportContent(content.id, format);
    } catch (error) {
      console.error('Failed to export content:', error);
      alert('Failed to export content. Please try again.');
    }
  };

  const handleWhatsAppShare = (content: AiGeneratedContent) => {
    setSelectedContent(content);
    setShowWhatsAppModal(true);
  };

  const handleShareToClass = async (form: string, section: string) => {
    if (!selectedContent) return;
    
    try {
      await aiService.shareContentToClass(selectedContent.id, form, section);
      alert('Content shared successfully to class!');
      setShowWhatsAppModal(false);
      setSelectedContent(null);
    } catch (error) {
      console.error('Failed to share content:', error);
      alert('Failed to share content. Please try again.');
    }
  };

  // Bulk Operations
  const handleSelectItem = (contentId: number) => {
    setSelectedItems(prev => 
      prev.includes(contentId) 
        ? prev.filter(id => id !== contentId)
        : [...prev, contentId]
    );
  };

  const handleSelectAll = () => {
    if (selectedItems.length === filteredContent.length) {
      setSelectedItems([]);
    } else {
      setSelectedItems(filteredContent.map(c => c.id));
    }
  };

  const handleBulkAction = async () => {
    if (selectedItems.length === 0) {
      alert('Please select at least one item');
      return;
    }

    if (!bulkAction) {
      alert('Please select an action');
      return;
    }

    try {
      switch (bulkAction) {
        case 'publish':
          await Promise.all(selectedItems.map(id => aiService.publishContent(id)));
          break;
        case 'unpublish':
          // Note: This would need a corresponding backend endpoint
          await Promise.all(selectedItems.map(id => aiService.updateContent(id, '', '', false)));
          break;
        case 'delete':
          await Promise.all(selectedItems.map(id => aiService.deleteContent(id)));
          break;
        case 'export':
          await Promise.all(selectedItems.map(id => aiService.exportContent(id, 'pdf')));
          break;
        default:
          alert('Invalid action');
          return;
      }

      // Refresh data
      const updatedContent = await aiService.getTeacherGeneratedContent();
      setContent(updatedContent);
      setSelectedItems([]);
      setBulkAction('');
      setShowBulkModal(false);
    } catch (error) {
      console.error('Bulk operation failed:', error);
      alert('Bulk operation failed. Please try again.');
    }
  };

  const filteredContent = content.filter(item => {
    const matchesSearch = item.title.toLowerCase().includes(filters.search.toLowerCase()) ||
                         item.description?.toLowerCase().includes(filters.search.toLowerCase());
    const matchesSubject = !filters.subjectId || item.subjectId.toString() === filters.subjectId;
    const matchesType = !filters.contentType || item.type === filters.contentType;
    const matchesDifficulty = !filters.difficultyLevel || item.difficultyLevel === filters.difficultyLevel;
    const matchesPublished = filters.published === '' || 
                           (filters.published === 'true' && item.published) ||
                           (filters.published === 'false' && !item.published);

    return matchesSearch && matchesSubject && matchesType && matchesDifficulty && matchesPublished;
  });

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
          <h1 className="text-2xl font-bold text-gray-900">AI Content Management</h1>
          <p className="text-gray-600">Manage and organize your AI-generated educational content</p>
        </div>
        <div className="flex items-center space-x-2">
          {selectedItems.length > 0 && (
            <div className="flex items-center space-x-2">
              <span className="text-sm text-gray-600">
                {selectedItems.length} selected
              </span>
              <Button 
                variant="outline" 
                onClick={() => setShowBulkModal(true)}
                disabled={selectedItems.length === 0}
              >
                Bulk Actions
              </Button>
            </div>
          )}
          <Button variant="outline">
            <Download className="h-4 w-4 mr-2" />
            Export All
          </Button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <FileText className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Content</p>
              <p className="text-xl font-bold text-gray-900">{content.length}</p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <Share className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Published</p>
              <p className="text-xl font-bold text-gray-900">
                {content.filter(c => c.published).length}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Users className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Usage</p>
              <p className="text-xl font-bold text-gray-900">
                {content.reduce((sum, c) => sum + c.usageCount, 0)}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <Target className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Subjects</p>
              <p className="text-xl font-bold text-gray-900">
                {new Set(content.map(c => c.subjectId)).size}
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Filters */}
      <Card className="p-6">
        <div className="grid grid-cols-1 md:grid-cols-6 gap-4">
          <div className="md:col-span-2">
            <label className="block text-sm font-medium text-gray-700 mb-2">Search</label>
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
              <Input
                value={filters.search}
                onChange={(e) => setFilters({ ...filters, search: e.target.value })}
                placeholder="Search content..."
                className="pl-10"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Subject</label>
            <Select
              value={filters.subjectId}
              onChange={(e) => setFilters({ ...filters, subjectId: e.target.value })}
              options={[
                { value: '', label: 'All Subjects' },
                ...(subjects || []).map((subject) => ({
                  value: subject.id.toString(),
                  label: subject.name
                }))
              ]}
              placeholder="All Subjects"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Type</label>
            <Select
              value={filters.contentType}
              onChange={(e) => setFilters({ ...filters, contentType: e.target.value })}
              options={[
                { value: '', label: 'All Types' },
                ...Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({
                  value,
                  label
                }))
              ]}
              placeholder="All Types"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Difficulty</label>
            <Select
              value={filters.difficultyLevel}
              onChange={(e) => setFilters({ ...filters, difficultyLevel: e.target.value })}
              options={[
                { value: '', label: 'All Levels' },
                ...Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({
                  value,
                  label
                }))
              ]}
              placeholder="All Levels"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Status</label>
            <Select
              value={filters.published}
              onChange={(e) => setFilters({ ...filters, published: e.target.value })}
              options={[
                { value: '', label: 'All' },
                { value: 'true', label: 'Published' },
                { value: 'false', label: 'Draft' }
              ]}
              placeholder="All"
            />
          </div>
        </div>
      </Card>

      {/* Content List */}
      <Card className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="flex items-center space-x-4">
            <h3 className="text-lg font-semibold text-gray-900">
              Content ({filteredContent.length})
            </h3>
            {filteredContent.length > 0 && (
              <div className="flex items-center space-x-2">
                <input
                  type="checkbox"
                  checked={selectedItems.length === filteredContent.length && filteredContent.length > 0}
                  onChange={handleSelectAll}
                  className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                />
                <span className="text-sm text-gray-600">Select All</span>
              </div>
            )}
          </div>
          
          {/* Bulk Actions Toolbar */}
          {selectedItems.length > 0 && (
            <div className="flex items-center space-x-2 bg-blue-50 p-3 rounded-lg">
              <span className="text-sm font-medium text-blue-900">
                {selectedItems.length} item(s) selected
              </span>
              <Select
                value={bulkAction}
                onChange={(e) => setBulkAction(e.target.value)}
                options={[
                  { value: '', label: 'Choose Action' },
                  { value: 'publish', label: 'Publish Selected' },
                  { value: 'unpublish', label: 'Unpublish Selected' },
                  { value: 'delete', label: 'Delete Selected' },
                  { value: 'export', label: 'Export Selected' },
                  { value: 'archive', label: 'Archive Selected' }
                ]}
                className="w-48"
              />
              <Button
                size="sm"
                onClick={() => setShowBulkModal(true)}
                disabled={!bulkAction}
                className="bg-blue-600 hover:bg-blue-700 text-white"
              >
                Apply
              </Button>
              <Button
                size="sm"
                variant="outline"
                onClick={() => {
                  setSelectedItems([]);
                  setBulkAction('');
                }}
              >
                Cancel
              </Button>
            </div>
          )}
        </div>
        
        {filteredContent.length > 0 ? (
          <div className="space-y-4">
            {filteredContent.map((item) => (
              <div key={item.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-4">
                  <input
                    type="checkbox"
                    checked={selectedItems.includes(item.id)}
                    onChange={() => handleSelectItem(item.id)}
                    className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                                    <div className="p-2 bg-purple-100 rounded-lg">
                    <Sparkles className="h-5 w-5 text-purple-600" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900">{item.title}</h4>
                    <p className="text-sm text-gray-600">{item.subjectName}</p>
                    <div className="flex items-center space-x-2 mt-1">
                      <Badge variant="outline">{CONTENT_TYPE_LABELS[item.type]}</Badge>
                      <Badge variant="outline">{DIFFICULTY_LEVEL_LABELS[item.difficultyLevel]}</Badge>
                      {item.published && (
                        <Badge variant="outline" className="text-green-600 border-green-200">
                          Published
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="text-sm text-gray-600">{formatDate(item.createdAt)}</p>
                    <p className="text-xs text-gray-500">Used {item.usageCount} times</p>
                  </div>
                  
                  <div className="flex items-center space-x-2">
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleViewContent(item)}
                      title="View"
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleExportContent(item, 'pdf')}
                      title="Export PDF"
                    >
                      <Download className="h-4 w-4" />
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleWhatsAppShare(item)}
                      title="Share via WhatsApp"
                      className="text-green-600 hover:text-green-700"
                    >
                      <MessageCircle className="h-4 w-4" />
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleEditContent(item)}
                      title="Edit"
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                    {!item.published && (
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handlePublishContent(item)}
                        title="Publish"
                      >
                        <Share className="h-4 w-4" />
                      </Button>
                    )}
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleDeleteContent(item)}
                      title="Delete"
                      className="text-red-600 hover:text-red-700"
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <FileText className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No content found</h3>
            <p className="text-gray-500 mb-4">
              {filters.search || filters.subjectId || filters.contentType || filters.difficultyLevel || filters.published
                ? 'Try adjusting your filters to see more content'
                : 'Start by generating some AI content'
              }
            </p>
          </div>
        )}
      </Card>

      {/* Content View Modal */}
      <Modal
        isOpen={showContentModal}
        onClose={() => setShowContentModal(false)}
        title={selectedContent?.title || 'Generated Content'}
        size="lg"
      >
        {selectedContent && (
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="font-medium">Subject:</span> {selectedContent.subjectName}
              </div>
              <div>
                <span className="font-medium">Type:</span> {CONTENT_TYPE_LABELS[selectedContent.type]}
              </div>
              <div>
                <span className="font-medium">Difficulty:</span> {DIFFICULTY_LEVEL_LABELS[selectedContent.difficultyLevel]}
              </div>
              <div>
                <span className="font-medium">Usage Count:</span> {selectedContent.usageCount}
              </div>
              <div>
                <span className="font-medium">Created:</span> {formatDate(selectedContent.createdAt)}
              </div>
              <div>
                <span className="font-medium">Status:</span> 
                <Badge variant="outline" className={`ml-2 ${selectedContent.published ? 'text-green-600 border-green-200' : 'text-yellow-600 border-yellow-200'}`}>
                  {selectedContent.published ? 'Published' : 'Draft'}
                </Badge>
              </div>
            </div>

            {selectedContent.description && (
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Description</h4>
                <p className="text-sm text-gray-600">{selectedContent.description}</p>
              </div>
            )}

            <div>
              <h4 className="font-medium text-gray-900 mb-2">Content</h4>
              <div className="bg-gray-50 p-4 rounded-lg max-h-64 overflow-y-auto">
                <pre className="whitespace-pre-wrap text-sm">{selectedContent.contentData}</pre>
              </div>
            </div>

            {selectedContent.markingScheme && (
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Marking Scheme</h4>
                <div className="bg-gray-50 p-4 rounded-lg max-h-64 overflow-y-auto">
                  <pre className="whitespace-pre-wrap text-sm">{selectedContent.markingScheme}</pre>
                </div>
              </div>
            )}

            <div className="flex justify-end space-x-3 pt-4">
              <Button variant="outline" onClick={() => setShowContentModal(false)}>
                Close
              </Button>
              <Button>
                <Download className="h-4 w-4 mr-2" />
                Download
              </Button>
            </div>
          </div>
        )}
      </Modal>

      {/* Edit Content Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => setShowEditModal(false)}
        title="Edit Content"
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Title *
            </label>
            <Input
              value={editForm.title}
              onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
              placeholder="Enter content title"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Description
            </label>
            <Textarea
              value={editForm.description}
              onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
              placeholder="Enter content description"
              rows={3}
            />
          </div>

          <div>
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={editForm.published}
                onChange={(e) => setEditForm({ ...editForm, published: e.target.checked })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700">Published</span>
            </label>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowEditModal(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpdateContent}>
              Update Content
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Delete Content"
      >
        <div className="space-y-4">
          <div className="text-center">
            <Trash2 className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              Delete Content
            </h3>
            <p className="text-gray-600">
              Are you sure you want to delete "{selectedContent?.title}"? This action cannot be undone.
            </p>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowDeleteModal(false)}>
              Cancel
            </Button>
            <Button 
              onClick={handleConfirmDeleteContent}
              className="bg-red-600 hover:bg-red-700"
            >
              Delete Content
            </Button>
          </div>
        </div>
      </Modal>

      {/* Bulk Operations Modal */}
      <Modal
        isOpen={showBulkModal}
        onClose={() => setShowBulkModal(false)}
        title="Bulk Operations"
      >
        <div className="space-y-4">
          <div>
            <p className="text-sm text-gray-600 mb-4">
              {selectedItems.length} items selected for bulk operation
            </p>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Select Action
              </label>
              <Select
                value={bulkAction}
                onChange={(e) => setBulkAction(e.target.value)}
                options={[
                  { value: '', label: 'Choose an action...' },
                  { value: 'publish', label: 'Publish Selected' },
                  { value: 'unpublish', label: 'Unpublish Selected' },
                  { value: 'delete', label: 'Delete Selected' },
                  { value: 'export', label: 'Export Selected (PDF)' }
                ]}
              />
            </div>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowBulkModal(false)}>
              Cancel
            </Button>
            <Button 
              onClick={handleBulkAction}
              disabled={!bulkAction}
            >
              Execute Action
            </Button>
          </div>
        </div>
      </Modal>

      {/* WhatsApp Sharing Modal */}
      <Modal
        isOpen={showWhatsAppModal}
        onClose={() => setShowWhatsAppModal(false)}
        title="Share via WhatsApp"
      >
        <div className="space-y-4">
          <div className="text-center">
            <MessageCircle className="h-12 w-12 text-green-500 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              Share Content via WhatsApp
            </h3>
            <p className="text-gray-600 mb-4">
              Share "{selectedContent?.title}" to students in a specific class
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Form
              </label>
              <Select
                value=""
                onChange={() => {}}
                options={[
                  { value: '', label: 'Select Form' },
                  { value: 'Form 1', label: 'Form 1' },
                  { value: 'Form 2', label: 'Form 2' },
                  { value: 'Form 3', label: 'Form 3' },
                  { value: 'Form 4', label: 'Form 4' },
                  { value: 'Form 5', label: 'Form 5' },
                  { value: 'Form 6', label: 'Form 6' }
                ]}
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Section
              </label>
              <Select
                value=""
                onChange={() => {}}
                options={[
                  { value: '', label: 'Select Section' },
                  { value: 'A', label: 'A' },
                  { value: 'B', label: 'B' },
                  { value: 'C', label: 'C' },
                  { value: 'D', label: 'D' }
                ]}
              />
            </div>
          </div>

          <div className="bg-blue-50 border border-blue-200 rounded-md p-3">
            <p className="text-sm text-blue-800">
              <strong>Note:</strong> This will share the content to all students in the selected class who have WhatsApp numbers registered in the system.
            </p>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowWhatsAppModal(false)}>
              Cancel
            </Button>
            <Button 
              onClick={() => handleShareToClass('Form 1', 'A')}
              className="bg-green-600 hover:bg-green-700"
            >
              Share to Class
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default AiContentPage;
