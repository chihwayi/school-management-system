import React, { useState, useEffect } from 'react';
import { 
  Sparkles, 
  FileText, 
  BookOpen, 
  Clock, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  Eye,
  Download,
  Share,
  Settings,
  Target,
  Users,
  Calendar
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
import { FORMS } from '../../types';
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

const AiGeneratePage: React.FC = () => {
  const { theme } = useAuth();
  const [generatedContent, setGeneratedContent] = useState<AiGeneratedContent[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [showGenerateModal, setShowGenerateModal] = useState(false);
  const [showContentModal, setShowContentModal] = useState(false);
  const [selectedContent, setSelectedContent] = useState<AiGeneratedContent | null>(null);
  const [generateForm, setGenerateForm] = useState({
    subjectId: '',
    title: '',
    description: '',
    contentType: ContentType.STUDY_NOTES,
    topicFocus: '',
    difficultyLevel: DifficultyLevel.MEDIUM,
    academicYear: new Date().getFullYear().toString(),
    formLevel: '',
    estimatedDuration: '',
    totalMarks: '',
    additionalInstructions: '',
    syllabusScope: '',
    targetAudience: '',
    includeMarkingScheme: false,
    includeExplanations: false
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [contentData, subjectsData] = await Promise.all([
          aiService.getTeacherGeneratedContent(),
          subjectService.getAllSubjects()
        ]);
        setGeneratedContent(contentData);
        setSubjects(subjectsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleGenerate = async () => {
    if (!generateForm.subjectId || !generateForm.title) {
      alert('Please fill in all required fields');
      return;
    }

    setGenerating(true);
    try {
      const newContent = await aiService.generateContent({
        subjectId: parseInt(generateForm.subjectId),
        title: generateForm.title,
        description: generateForm.description,
        contentType: generateForm.contentType,
        topicFocus: generateForm.topicFocus,
        difficultyLevel: generateForm.difficultyLevel,
        academicYear: generateForm.academicYear,
        formLevel: generateForm.formLevel,
        estimatedDuration: generateForm.estimatedDuration ? parseInt(generateForm.estimatedDuration) : undefined,
        totalMarks: generateForm.totalMarks ? parseInt(generateForm.totalMarks) : undefined,
        additionalInstructions: generateForm.additionalInstructions,
        syllabusScope: generateForm.syllabusScope,
        targetAudience: generateForm.targetAudience,
        includeMarkingScheme: generateForm.includeMarkingScheme,
        includeExplanations: generateForm.includeExplanations
      });

      setGeneratedContent([newContent, ...generatedContent]);
      setShowGenerateModal(false);
      setGenerateForm({
        subjectId: '',
        title: '',
        description: '',
        contentType: ContentType.STUDY_NOTES,
        topicFocus: '',
        difficultyLevel: DifficultyLevel.MEDIUM,
        academicYear: new Date().getFullYear().toString(),
        formLevel: '',
        estimatedDuration: '',
        totalMarks: '',
        additionalInstructions: '',
        syllabusScope: '',
        targetAudience: '',
        includeMarkingScheme: false,
        includeExplanations: false
      });
    } catch (error) {
      console.error('Failed to generate content:', error);
      alert('Failed to generate content. Please try again.');
    } finally {
      setGenerating(false);
    }
  };

  const handlePublish = async (contentId: number) => {
    try {
      const updatedContent = await aiService.publishContent(contentId);
      setGeneratedContent(prev => 
        prev.map(content => 
          content.id === contentId ? updatedContent : content
        )
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
          <h1 className="text-2xl font-bold text-gray-900">AI Content Generation</h1>
          <p className="text-gray-600">Generate notes, exams, and practice questions using AI</p>
        </div>
        <Button onClick={() => setShowGenerateModal(true)}>
          <Sparkles className="h-4 w-4 mr-2" />
          Generate Content
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <FileText className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Generated</p>
              <p className="text-xl font-bold text-gray-900">{generatedContent.length}</p>
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
                {generatedContent.filter(c => c.published).length}
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
                {generatedContent.reduce((sum, c) => sum + c.usageCount, 0)}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-orange-100 rounded-lg">
              <BookOpen className="h-5 w-5 text-orange-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Subjects</p>
              <p className="text-xl font-bold text-gray-900">
                {new Set(generatedContent.map(c => c.subjectId)).size}
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Generated Content List */}
      <Card className="p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Generated Content</h3>
        
        {generatedContent.length > 0 ? (
          <div className="space-y-4">
            {generatedContent.map((content) => (
              <div key={content.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-4">
                  <div className="p-2 bg-purple-100 rounded-lg">
                    <Sparkles className="h-5 w-5 text-purple-600" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900">{content.title}</h4>
                    <p className="text-sm text-gray-600">{content.subjectName}</p>
                    <div className="flex items-center space-x-2 mt-1">
                      <Badge variant="outline">{CONTENT_TYPE_LABELS[content.type]}</Badge>
                      <Badge variant="outline">{DIFFICULTY_LEVEL_LABELS[content.difficultyLevel]}</Badge>
                      {content.published && (
                        <Badge variant="outline" className="text-green-600 border-green-200">
                          Published
                        </Badge>
                      )}
                    </div>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="text-sm text-gray-600">{formatDate(content.createdAt)}</p>
                    <p className="text-xs text-gray-500">Used {content.usageCount} times</p>
                  </div>
                  
                  <div className="flex items-center space-x-2">
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleViewContent(content)}
                    >
                      <Eye className="h-4 w-4" />
                    </Button>
                    <Button variant="outline" size="sm">
                      <Download className="h-4 w-4" />
                    </Button>
                    {!content.published && (
                      <Button 
                        variant="outline" 
                        size="sm"
                        onClick={() => handlePublish(content.id)}
                      >
                        <Share className="h-4 w-4" />
                      </Button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="text-center py-12">
            <Sparkles className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No content generated yet</h3>
            <p className="text-gray-500 mb-4">Start by generating notes, exams, and practice questions</p>
            <Button onClick={() => setShowGenerateModal(true)}>
              <Sparkles className="h-4 w-4 mr-2" />
              Generate Your First Content
            </Button>
          </div>
        )}
      </Card>

      {/* Generate Modal */}
      <Modal
        isOpen={showGenerateModal}
        onClose={() => setShowGenerateModal(false)}
        title="Generate AI Content"
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Subject *
            </label>
            <Select
              value={generateForm.subjectId}
              onChange={(e) => setGenerateForm({ ...generateForm, subjectId: e.target.value })}
              options={[
                { value: '', label: 'Select a subject' },
                ...(subjects || []).map((subject) => ({
                  value: subject.id.toString(),
                  label: subject.name
                }))
              ]}
              placeholder="Select a subject"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Content Type *
            </label>
            <Select
              value={generateForm.contentType}
              onChange={(e) => setGenerateForm({ ...generateForm, contentType: e.target.value as ContentType })}
              options={Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({
                value,
                label
              }))}
              placeholder="Select content type"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Title *
            </label>
            <Input
              value={generateForm.title}
              onChange={(e) => setGenerateForm({ ...generateForm, title: e.target.value })}
              placeholder="Enter content title"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Description
            </label>
            <Textarea
              value={generateForm.description}
              onChange={(e) => setGenerateForm({ ...generateForm, description: e.target.value })}
              placeholder="Enter content description"
              rows={3}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Topic Focus
              </label>
              <Input
                value={generateForm.topicFocus}
                onChange={(e) => setGenerateForm({ ...generateForm, topicFocus: e.target.value })}
                placeholder="e.g., Algebra, Photosynthesis"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Difficulty Level
              </label>
              <Select
                value={generateForm.difficultyLevel}
                onChange={(e) => setGenerateForm({ ...generateForm, difficultyLevel: e.target.value as DifficultyLevel })}
                options={Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({
                  value,
                  label
                }))}
                placeholder="Select difficulty level"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Academic Year
              </label>
              <Input
                value={generateForm.academicYear}
                onChange={(e) => setGenerateForm({ ...generateForm, academicYear: e.target.value })}
                placeholder="e.g., 2024"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Form Level
              </label>
              <Select
                value={generateForm.formLevel}
                onChange={(e) => setGenerateForm({ ...generateForm, formLevel: e.target.value })}
                options={[
                  { value: '', label: 'Select Form Level' },
                  ...Object.values(FORMS).flat().map(form => ({
                    value: form,
                    label: form
                  }))
                ]}
                placeholder="Select Form Level"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Estimated Duration (minutes)
              </label>
              <Input
                value={generateForm.estimatedDuration}
                onChange={(e) => setGenerateForm({ ...generateForm, estimatedDuration: e.target.value })}
                placeholder="e.g., 60"
                type="number"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Total Marks
              </label>
              <Input
                value={generateForm.totalMarks}
                onChange={(e) => setGenerateForm({ ...generateForm, totalMarks: e.target.value })}
                placeholder="e.g., 100"
                type="number"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Additional Instructions
            </label>
            <Textarea
              value={generateForm.additionalInstructions}
              onChange={(e) => setGenerateForm({ ...generateForm, additionalInstructions: e.target.value })}
              placeholder="Any specific requirements or instructions for the AI"
              rows={3}
            />
          </div>

          <div className="flex items-center space-x-4">
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={generateForm.includeMarkingScheme}
                onChange={(e) => setGenerateForm({ ...generateForm, includeMarkingScheme: e.target.checked })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700">Include Marking Scheme</span>
            </label>
            <label className="flex items-center">
              <input
                type="checkbox"
                checked={generateForm.includeExplanations}
                onChange={(e) => setGenerateForm({ ...generateForm, includeExplanations: e.target.checked })}
                className="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <span className="ml-2 text-sm text-gray-700">Include Explanations</span>
            </label>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowGenerateModal(false)}>
              Cancel
            </Button>
            <Button onClick={handleGenerate} disabled={generating}>
              {generating ? 'Generating...' : 'Generate Content'}
            </Button>
          </div>
        </div>
      </Modal>

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
            </div>

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
    </div>
  );
};

export default AiGeneratePage;
