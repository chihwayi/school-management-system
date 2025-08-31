import React, { useState, useEffect } from 'react';
import { 
  FileText, 
  Eye, 
  Download, 
  Search,
  Filter,
  Calendar,
  BookOpen,
  Target,
  Sparkles,
  Star
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Select from '../../components/ui/Select';
import Modal from '../../components/ui/Modal';
import Badge from '../../components/ui/Badge';
import { useAuth } from '../../hooks/useAuth';
import { aiService } from '../../services/aiService';
import { subjectService } from '../../services/subjectService';

// Temporary type definitions
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

const DIFFICULTY_COLORS = {
  [DifficultyLevel.EASY]: 'green',
  [DifficultyLevel.MEDIUM]: 'yellow',
  [DifficultyLevel.HARD]: 'orange',
  [DifficultyLevel.EXPERT]: 'red'
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

const AiStudentContentPage: React.FC = () => {
  const { theme, user } = useAuth();
  const [content, setContent] = useState<AiGeneratedContent[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [showContentModal, setShowContentModal] = useState(false);
  const [selectedContent, setSelectedContent] = useState<AiGeneratedContent | null>(null);
  const [filters, setFilters] = useState({
    search: '',
    subjectId: '',
    contentType: '',
    difficultyLevel: '',
    formLevel: ''
  });
  const [favorites, setFavorites] = useState<number[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        // Get all published content for students
        const [contentData, subjectsData] = await Promise.all([
          aiService.getPublishedContentForStudents(),
          subjectService.getAllSubjects()
        ]);
        setContent(contentData);
        setSubjects(subjectsData);
        
        // Load favorites from localStorage
        const savedFavorites = localStorage.getItem('aiContentFavorites');
        if (savedFavorites) {
          setFavorites(JSON.parse(savedFavorites));
        }
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleViewContent = (content: AiGeneratedContent) => {
    setSelectedContent(content);
    setShowContentModal(true);
    
    // Increment usage count
    aiService.useContent(content.id);
  };

  const handleDownloadContent = async (content: AiGeneratedContent) => {
    try {
      await aiService.exportContent(content.id, 'pdf');
    } catch (error) {
      console.error('Failed to download content:', error);
      alert('Failed to download content. Please try again.');
    }
  };

  const handleToggleFavorite = (contentId: number) => {
    const newFavorites = favorites.includes(contentId)
      ? favorites.filter(id => id !== contentId)
      : [...favorites, contentId];
    
    setFavorites(newFavorites);
    localStorage.setItem('aiContentFavorites', JSON.stringify(newFavorites));
  };

  const filteredContent = content.filter(item => {
    const matchesSearch = item.title.toLowerCase().includes(filters.search.toLowerCase()) ||
                         item.description?.toLowerCase().includes(filters.search.toLowerCase()) ||
                         item.topicFocus?.toLowerCase().includes(filters.search.toLowerCase());
    const matchesSubject = !filters.subjectId || item.subjectId.toString() === filters.subjectId;
    const matchesType = !filters.contentType || item.type === filters.contentType;
    const matchesDifficulty = !filters.difficultyLevel || item.difficultyLevel === filters.difficultyLevel;
    const matchesFormLevel = !filters.formLevel || item.formLevel === filters.formLevel;

    return matchesSearch && matchesSubject && matchesType && matchesDifficulty && matchesFormLevel;
  });

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString();
  };

  const getDifficultyColor = (difficulty: DifficultyLevel) => {
    return DIFFICULTY_COLORS[difficulty] || 'gray';
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
          <h1 className="text-2xl font-bold text-gray-900">AI Learning Resources</h1>
          <p className="text-gray-600">Access AI-generated educational content from your teachers</p>
        </div>
        <div className="flex items-center space-x-2">
          <Button variant="outline">
            <Star className="h-4 w-4 mr-2" />
            Favorites ({favorites.length})
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
              <p className="text-sm text-gray-600">Available Content</p>
              <p className="text-xl font-bold text-gray-900">{content.length}</p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <BookOpen className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Subjects</p>
              <p className="text-xl font-bold text-gray-900">{subjects.length}</p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Target className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Favorites</p>
              <p className="text-xl font-bold text-gray-900">{favorites.length}</p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <Sparkles className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">AI Generated</p>
              <p className="text-xl font-bold text-gray-900">100%</p>
            </div>
          </div>
        </Card>
      </div>

      {/* Filters */}
      <Card>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <Input
            placeholder="Search content..."
            value={filters.search}
            onChange={(e) => setFilters({ ...filters, search: e.target.value })}
            icon={<Search className="h-4 w-4" />}
          />
          <Select
            placeholder="Subject"
            value={filters.subjectId}
            onChange={(e) => setFilters({ ...filters, subjectId: e.target.value })}
            options={[
              { value: '', label: 'All Subjects' },
              ...subjects.map(subject => ({ value: subject.id.toString(), label: subject.name }))
            ]}
          />
          <Select
            placeholder="Content Type"
            value={filters.contentType}
            onChange={(e) => setFilters({ ...filters, contentType: e.target.value })}
            options={[
              { value: '', label: 'All Types' },
              ...Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({ value, label }))
            ]}
          />
          <Select
            placeholder="Difficulty"
            value={filters.difficultyLevel}
            onChange={(e) => setFilters({ ...filters, difficultyLevel: e.target.value })}
            options={[
              { value: '', label: 'All Levels' },
              ...Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({ value, label }))
            ]}
          />
          <Select
            placeholder="Form Level"
            value={filters.formLevel}
            onChange={(e) => setFilters({ ...filters, formLevel: e.target.value })}
            options={[
              { value: '', label: 'All Forms' },
              { value: 'Form 1', label: 'Form 1' },
              { value: 'Form 2', label: 'Form 2' },
              { value: 'Form 3', label: 'Form 3' },
              { value: 'Form 4', label: 'Form 4' },
              { value: 'Form 5', label: 'Form 5' },
              { value: 'Form 6', label: 'Form 6' }
            ]}
          />
        </div>
      </Card>

      {/* Content Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredContent.map((item) => (
          <Card key={item.id} className="p-6 hover:shadow-lg transition-shadow">
            <div className="flex justify-between items-start mb-4">
              <div className="p-2 bg-purple-100 rounded-lg">
                <Sparkles className="h-5 w-5 text-purple-600" />
              </div>
              <button
                onClick={() => handleToggleFavorite(item.id)}
                className={`p-1 rounded-full transition-colors ${
                  favorites.includes(item.id) 
                    ? 'text-yellow-500 hover:text-yellow-600' 
                    : 'text-gray-400 hover:text-yellow-500'
                }`}
              >
                <Star className={`h-5 w-5 ${favorites.includes(item.id) ? 'fill-current' : ''}`} />
              </button>
            </div>

            <div className="space-y-3">
              <div>
                <h3 className="font-semibold text-gray-900 text-lg mb-1">{item.title}</h3>
                <p className="text-sm text-gray-600">{item.description}</p>
              </div>

              <div className="flex flex-wrap gap-2">
                <Badge color="blue">{CONTENT_TYPE_LABELS[item.type]}</Badge>
                <Badge color={getDifficultyColor(item.difficultyLevel) as any}>
                  {DIFFICULTY_LEVEL_LABELS[item.difficultyLevel]}
                </Badge>
                <Badge variant="outline">{item.subjectName}</Badge>
                {item.formLevel && <Badge variant="outline">{item.formLevel}</Badge>}
              </div>

              <div className="flex justify-between items-center text-sm text-gray-500">
                <span>By {item.teacherName}</span>
                <span>{formatDate(item.createdAt)}</span>
              </div>

              {item.estimatedDuration && (
                <div className="text-sm text-gray-600">
                  <Calendar className="h-4 w-4 inline mr-1" />
                  {item.estimatedDuration} minutes
                </div>
              )}

              <div className="flex space-x-2 pt-2">
                <Button 
                  size="sm" 
                  onClick={() => handleViewContent(item)}
                  className="flex-1"
                >
                  <Eye className="h-4 w-4 mr-2" />
                  View
                </Button>
                <Button 
                  size="sm" 
                  variant="outline"
                  onClick={() => handleDownloadContent(item)}
                >
                  <Download className="h-4 w-4" />
                </Button>
              </div>
            </div>
          </Card>
        ))}
      </div>

      {filteredContent.length === 0 && (
        <Card className="p-12 text-center">
          <FileText className="h-16 w-16 text-gray-300 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No content found</h3>
          <p className="text-gray-500">
            {filters.search || filters.subjectId || filters.contentType || filters.difficultyLevel || filters.formLevel
              ? 'Try adjusting your filters to see more content'
              : 'No AI-generated content is available yet'
            }
          </p>
        </Card>
      )}

      {/* Content View Modal */}
      <Modal
        isOpen={showContentModal}
        onClose={() => setShowContentModal(false)}
        title={selectedContent?.title || 'AI Generated Content'}
        size="lg"
      >
        {selectedContent && (
          <div className="space-y-6">
            <div className="flex flex-wrap gap-2">
              <Badge color="blue">{CONTENT_TYPE_LABELS[selectedContent.type]}</Badge>
              <Badge color={getDifficultyColor(selectedContent.difficultyLevel) as any}>
                {DIFFICULTY_LEVEL_LABELS[selectedContent.difficultyLevel]}
              </Badge>
              <Badge variant="outline">{selectedContent.subjectName}</Badge>
              {selectedContent.formLevel && <Badge variant="outline">{selectedContent.formLevel}</Badge>}
            </div>

            {selectedContent.topicFocus && (
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Topic Focus</h4>
                <p className="text-gray-600">{selectedContent.topicFocus}</p>
              </div>
            )}

            <div>
              <h4 className="font-medium text-gray-900 mb-2">Content</h4>
              <div className="bg-gray-50 p-4 rounded-lg max-h-96 overflow-y-auto">
                <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans">
                  {selectedContent.contentData}
                </pre>
              </div>
            </div>

            {selectedContent.markingScheme && (
              <div>
                <h4 className="font-medium text-gray-900 mb-2">Marking Scheme</h4>
                <div className="bg-blue-50 p-4 rounded-lg">
                  <pre className="whitespace-pre-wrap text-sm text-gray-700 font-sans">
                    {selectedContent.markingScheme}
                  </pre>
                </div>
              </div>
            )}

            <div className="flex justify-between items-center text-sm text-gray-500">
              <div>
                <p>Generated by {selectedContent.teacherName}</p>
                <p>Created on {formatDate(selectedContent.createdAt)}</p>
              </div>
              <div className="text-right">
                <p>Used {selectedContent.usageCount} times</p>
                {selectedContent.totalMarks && <p>Total Marks: {selectedContent.totalMarks}</p>}
              </div>
            </div>

            <div className="flex justify-end space-x-2">
              <Button variant="outline" onClick={() => setShowContentModal(false)}>
                Close
              </Button>
              <Button onClick={() => handleDownloadContent(selectedContent)}>
                <Download className="h-4 w-4 mr-2" />
                Download PDF
              </Button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default AiStudentContentPage;
