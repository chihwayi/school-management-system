import React, { useState, useEffect } from 'react';
import { Card, Button, Input, Select, Textarea, Modal, Badge } from '../../components/ui';
import { aiService } from '../../services/aiService';
import { subjectService } from '../../services/subjectService';
import { FORMS } from '../../types';

// Temporary type definitions to bypass import issues
interface AiTemplate {
  id: number;
  teacherId: number;
  teacherName: string;
  name: string;
  description: string;
  contentType: string;
  difficultyLevel: string;
  formLevel: string;
  estimatedDuration: number;
  totalMarks: number;
  additionalInstructions: string;
  isPublic: boolean;
  usageCount: number;
  createdAt: string;
  updatedAt: string;
}

interface Subject {
  id: number;
  name: string;
  code: string;
}

const CONTENT_TYPE_LABELS = {
  STUDY_NOTES: 'Study Notes',
  PRACTICE_QUESTIONS: 'Practice Questions',
  QUIZ: 'Quiz',
  MIDTERM_EXAM: 'Midterm Exam',
  FINAL_EXAM: 'Final Exam',
  ASSIGNMENT: 'Assignment',
  REVISION_MATERIAL: 'Revision Material'
};

const DIFFICULTY_LEVEL_LABELS = {
  EASY: 'Easy',
  MEDIUM: 'Medium',
  HARD: 'Hard',
  EXPERT: 'Expert'
};

const AiTemplatesPage: React.FC = () => {
  const [templates, setTemplates] = useState<AiTemplate[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedTemplate, setSelectedTemplate] = useState<AiTemplate | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');
  const [filterDifficulty, setFilterDifficulty] = useState('');
  const [filterPublic, setFilterPublic] = useState('');

  // Form state
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    contentType: '',
    difficultyLevel: '',
    formLevel: '',
    estimatedDuration: 60,
    totalMarks: 100,
    additionalInstructions: '',
    isPublic: false
  });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [templatesData, subjectsData] = await Promise.all([
        aiService.getTemplates(),
        subjectService.getAllSubjects()
      ]);
      setTemplates(templatesData);
      setSubjects(subjectsData);
    } catch (error) {
      console.error('Failed to fetch data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateTemplate = async () => {
    try {
      await aiService.createTemplate(formData);
      setShowCreateModal(false);
      resetForm();
      fetchData();
    } catch (error) {
      console.error('Failed to create template:', error);
    }
  };

  const handleEditTemplate = async () => {
    if (!selectedTemplate) return;
    
    try {
      await aiService.updateTemplate(selectedTemplate.id, formData);
      setShowEditModal(false);
      setSelectedTemplate(null);
      resetForm();
      fetchData();
    } catch (error) {
      console.error('Failed to update template:', error);
    }
  };

  const handleDeleteTemplate = async () => {
    if (!selectedTemplate) return;
    
    try {
      await aiService.deleteTemplate(selectedTemplate.id);
      setShowDeleteModal(false);
      setSelectedTemplate(null);
      fetchData();
    } catch (error) {
      console.error('Failed to delete template:', error);
    }
  };

  const handleUseTemplate = async (template: AiTemplate) => {
    try {
      const result = await aiService.useTemplate(template.id);
      // Navigate to generate page with template data
      console.log('Template used:', result);
    } catch (error) {
      console.error('Failed to use template:', error);
    }
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      contentType: '',
      difficultyLevel: '',
      formLevel: '',
      estimatedDuration: 60,
      totalMarks: 100,
      additionalInstructions: '',
      isPublic: false
    });
  };

  const openEditModal = (template: AiTemplate) => {
    setSelectedTemplate(template);
    setFormData({
      name: template.name,
      description: template.description,
      contentType: template.contentType,
      difficultyLevel: template.difficultyLevel,
      formLevel: template.formLevel,
      estimatedDuration: template.estimatedDuration,
      totalMarks: template.totalMarks,
      additionalInstructions: template.additionalInstructions,
      isPublic: template.isPublic
    });
    setShowEditModal(true);
  };

  const openDeleteModal = (template: AiTemplate) => {
    setSelectedTemplate(template);
    setShowDeleteModal(true);
  };

  // Filter templates
  const filteredTemplates = templates.filter(template => {
    const matchesSearch = template.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         template.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesType = !filterType || template.contentType === filterType;
    const matchesDifficulty = !filterDifficulty || template.difficultyLevel === filterDifficulty;
    const matchesPublic = filterPublic === '' || 
                         (filterPublic === 'true' && template.isPublic) ||
                         (filterPublic === 'false' && !template.isPublic);
    
    return matchesSearch && matchesType && matchesDifficulty && matchesPublic;
  });

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-lg">Loading templates...</div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">AI Templates</h1>
          <p className="text-gray-600">Create and manage content generation templates</p>
        </div>
        <Button onClick={() => setShowCreateModal(true)}>
          Create Template
        </Button>
      </div>

      {/* Filters */}
      <Card>
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <Input
            placeholder="Search templates..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <Select
            placeholder="Content Type"
            value={filterType}
            onChange={(e) => setFilterType(e.target.value)}
            options={Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            placeholder="Difficulty"
            value={filterDifficulty}
            onChange={(e) => setFilterDifficulty(e.target.value)}
            options={Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            placeholder="Visibility"
            value={filterPublic}
            onChange={(e) => setFilterPublic(e.target.value)}
            options={[
              { value: '', label: 'All' },
              { value: 'true', label: 'Public' },
              { value: 'false', label: 'Private' }
            ]}
          />
        </div>
      </Card>

      {/* Templates List */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filteredTemplates.map((template) => (
          <Card key={template.id} className="p-6">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h3 className="text-lg font-semibold text-gray-900">{template.name}</h3>
                <p className="text-sm text-gray-600">{template.description}</p>
              </div>
              <div className="flex space-x-2">
                {template.isPublic && <Badge color="green">Public</Badge>}
                <Badge color="blue">{CONTENT_TYPE_LABELS[template.contentType as keyof typeof CONTENT_TYPE_LABELS]}</Badge>
              </div>
            </div>

            <div className="space-y-2 mb-4">
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Difficulty:</span>
                <span>{DIFFICULTY_LEVEL_LABELS[template.difficultyLevel as keyof typeof DIFFICULTY_LEVEL_LABELS]}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Form Level:</span>
                <span>{template.formLevel}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Duration:</span>
                <span>{template.estimatedDuration} min</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Marks:</span>
                <span>{template.totalMarks}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-600">Usage:</span>
                <span>{template.usageCount} times</span>
              </div>
            </div>

            <div className="flex space-x-2">
              <Button size="sm" onClick={() => handleUseTemplate(template)}>
                Use Template
              </Button>
              <Button size="sm" variant="outline" onClick={() => openEditModal(template)}>
                Edit
              </Button>
              <Button size="sm" variant="outline" color="red" onClick={() => openDeleteModal(template)}>
                Delete
              </Button>
            </div>
          </Card>
        ))}
      </div>

      {filteredTemplates.length === 0 && (
        <Card className="p-12 text-center">
          <div className="text-gray-500">
            <p className="text-lg mb-2">No templates found</p>
            <p>Create your first template to get started</p>
          </div>
        </Card>
      )}

      {/* Create Template Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="Create Template"
      >
        <div className="space-y-4">
          <Input
            label="Template Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="Enter template name"
          />
          <Textarea
            label="Description"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Enter template description"
          />
          <Select
            label="Content Type"
            value={formData.contentType}
            onChange={(e) => setFormData({ ...formData, contentType: e.target.value })}
            options={Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            label="Difficulty Level"
            value={formData.difficultyLevel}
            onChange={(e) => setFormData({ ...formData, difficultyLevel: e.target.value })}
            options={Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            label="Form Level"
            value={formData.formLevel}
            onChange={(e) => setFormData({ ...formData, formLevel: e.target.value })}
            options={Object.values(FORMS).flat().map(form => ({ value: form, label: form }))}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Estimated Duration (minutes)"
              type="number"
              value={formData.estimatedDuration}
              onChange={(e) => setFormData({ ...formData, estimatedDuration: parseInt(e.target.value) })}
            />
            <Input
              label="Total Marks"
              type="number"
              value={formData.totalMarks}
              onChange={(e) => setFormData({ ...formData, totalMarks: parseInt(e.target.value) })}
            />
          </div>
          <Textarea
            label="Additional Instructions"
            value={formData.additionalInstructions}
            onChange={(e) => setFormData({ ...formData, additionalInstructions: e.target.value })}
            placeholder="Enter additional instructions for content generation"
          />
          <div className="flex items-center space-x-2">
            <input
              type="checkbox"
              id="isPublic"
              checked={formData.isPublic}
              onChange={(e) => setFormData({ ...formData, isPublic: e.target.checked })}
              className="rounded"
            />
            <label htmlFor="isPublic" className="text-sm text-gray-700">
              Make this template public (share with other teachers)
            </label>
          </div>
        </div>
        <div className="flex justify-end space-x-2 mt-6">
          <Button variant="outline" onClick={() => setShowCreateModal(false)}>
            Cancel
          </Button>
          <Button onClick={handleCreateTemplate}>
            Create Template
          </Button>
        </div>
      </Modal>

      {/* Edit Template Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => setShowEditModal(false)}
        title="Edit Template"
      >
        <div className="space-y-4">
          <Input
            label="Template Name"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            placeholder="Enter template name"
          />
          <Textarea
            label="Description"
            value={formData.description}
            onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            placeholder="Enter template description"
          />
          <Select
            label="Content Type"
            value={formData.contentType}
            onChange={(e) => setFormData({ ...formData, contentType: e.target.value })}
            options={Object.entries(CONTENT_TYPE_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            label="Difficulty Level"
            value={formData.difficultyLevel}
            onChange={(e) => setFormData({ ...formData, difficultyLevel: e.target.value })}
            options={Object.entries(DIFFICULTY_LEVEL_LABELS).map(([value, label]) => ({ value, label }))}
          />
          <Select
            label="Form Level"
            value={formData.formLevel}
            onChange={(e) => setFormData({ ...formData, formLevel: e.target.value })}
            options={Object.values(FORMS).flat().map(form => ({ value: form, label: form }))}
          />
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Estimated Duration (minutes)"
              type="number"
              value={formData.estimatedDuration}
              onChange={(e) => setFormData({ ...formData, estimatedDuration: parseInt(e.target.value) })}
            />
            <Input
              label="Total Marks"
              type="number"
              value={formData.totalMarks}
              onChange={(e) => setFormData({ ...formData, totalMarks: parseInt(e.target.value) })}
            />
          </div>
          <Textarea
            label="Additional Instructions"
            value={formData.additionalInstructions}
            onChange={(e) => setFormData({ ...formData, additionalInstructions: e.target.value })}
            placeholder="Enter additional instructions for content generation"
          />
          <div className="flex items-center space-x-2">
            <input
              type="checkbox"
              id="editIsPublic"
              checked={formData.isPublic}
              onChange={(e) => setFormData({ ...formData, isPublic: e.target.checked })}
              className="rounded"
            />
            <label htmlFor="editIsPublic" className="text-sm text-gray-700">
              Make this template public (share with other teachers)
            </label>
          </div>
        </div>
        <div className="flex justify-end space-x-2 mt-6">
          <Button variant="outline" onClick={() => setShowEditModal(false)}>
            Cancel
          </Button>
          <Button onClick={handleEditTemplate}>
            Update Template
          </Button>
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Delete Template"
      >
        <div className="p-4">
          <p className="text-gray-700 mb-4">
            Are you sure you want to delete the template "{selectedTemplate?.name}"? 
            This action cannot be undone.
          </p>
        </div>
        <div className="flex justify-end space-x-2">
          <Button variant="outline" onClick={() => setShowDeleteModal(false)}>
            Cancel
          </Button>
          <Button color="red" onClick={handleDeleteTemplate}>
            Delete Template
          </Button>
        </div>
      </Modal>
    </div>
  );
};

export default AiTemplatesPage;
