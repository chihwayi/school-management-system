import React, { useState, useEffect } from 'react';
import { 
  Upload, 
  FileText, 
  BookOpen, 
  Clock, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  Download,
  Trash2,
  Eye,
  Edit
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
enum ResourceType {
  SYLLABUS = 'SYLLABUS',
  TEXTBOOK = 'TEXTBOOK',
  PAST_PAPER = 'PAST_PAPER',
  MARKING_SCHEME = 'MARKING_SCHEME',
  STUDY_GUIDE = 'STUDY_GUIDE',
  REFERENCE_MATERIAL = 'REFERENCE_MATERIAL'
}

enum ProcessingStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

const RESOURCE_TYPE_LABELS = {
  [ResourceType.SYLLABUS]: 'Syllabus',
  [ResourceType.TEXTBOOK]: 'Textbook',
  [ResourceType.PAST_PAPER]: 'Past Paper',
  [ResourceType.MARKING_SCHEME]: 'Marking Scheme',
  [ResourceType.STUDY_GUIDE]: 'Study Guide',
  [ResourceType.REFERENCE_MATERIAL]: 'Reference Material'
};

const PROCESSING_STATUS_LABELS = {
  [ProcessingStatus.PENDING]: 'Pending',
  [ProcessingStatus.PROCESSING]: 'Processing',
  [ProcessingStatus.COMPLETED]: 'Completed',
  [ProcessingStatus.FAILED]: 'Failed'
};

const PROCESSING_STATUS_COLORS = {
  [ProcessingStatus.PENDING]: 'yellow',
  [ProcessingStatus.PROCESSING]: 'blue',
  [ProcessingStatus.COMPLETED]: 'green',
  [ProcessingStatus.FAILED]: 'red'
};

interface AiResource {
  id: number;
  teacherId: number;
  teacherName: string;
  subjectId: number;
  subjectName: string;
  title: string;
  description?: string;
  type: ResourceType;
  fileName: string;
  fileSize: number;
  fileType: string;
  academicYear: string;
  formLevel?: string;
  processed: boolean;
  processingStatus: ProcessingStatus;
  processingNotes?: string;
  createdAt: string;
  updatedAt: string;
}

interface Subject {
  id: number;
  name: string;
  code: string;
  description?: string;
}

const AiResourcesPage: React.FC = () => {
  const { theme } = useAuth();
  const [resources, setResources] = useState<AiResource[]>([]);
  const [subjects, setSubjects] = useState<Subject[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [selectedResource, setSelectedResource] = useState<AiResource | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [uploadForm, setUploadForm] = useState({
    type: ResourceType.SYLLABUS,
    subjectId: '',
    title: '',
    description: '',
    academicYear: new Date().getFullYear().toString(),
    formLevel: ''
  });

  const [editForm, setEditForm] = useState({
    title: '',
    description: '',
    academicYear: '',
    formLevel: ''
  });

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [resourcesData, subjectsData] = await Promise.all([
          aiService.getTeacherResources(),
          subjectService.getAllSubjects()
        ]);
        setResources(resourcesData);
        setSubjects(subjectsData);
      } catch (error) {
        console.error('Failed to fetch data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile || !uploadForm.subjectId || !uploadForm.title) {
      alert('Please fill in all required fields and select a file');
      return;
    }

    setUploading(true);
    try {
      const newResource = await aiService.uploadResource(
        selectedFile,
        uploadForm.type,
        parseInt(uploadForm.subjectId),
        uploadForm.title,
        uploadForm.description,
        uploadForm.academicYear,
        uploadForm.formLevel
      );

      setResources([newResource, ...resources]);
      setShowUploadModal(false);
      setSelectedFile(null);
      setUploadForm({
        type: ResourceType.SYLLABUS,
        subjectId: '',
        title: '',
        description: '',
        academicYear: new Date().getFullYear().toString(),
        formLevel: ''
      });
    } catch (error) {
      console.error('Failed to upload resource:', error);
      alert('Failed to upload resource. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const handleEdit = (resource: AiResource) => {
    setSelectedResource(resource);
    setEditForm({
      title: resource.title,
      description: resource.description || '',
      academicYear: resource.academicYear,
      formLevel: resource.formLevel || ''
    });
    setShowEditModal(true);
  };

  const handleUpdate = async () => {
    if (!selectedResource || !editForm.title) {
      alert('Please fill in all required fields');
      return;
    }

    try {
      const updatedResource = await aiService.updateResource(
        selectedResource.id,
        editForm.title,
        editForm.description,
        editForm.academicYear,
        editForm.formLevel
      );

      setResources(resources.map(r => r.id === selectedResource.id ? updatedResource : r));
      setShowEditModal(false);
      setSelectedResource(null);
    } catch (error) {
      console.error('Failed to update resource:', error);
      alert('Failed to update resource. Please try again.');
    }
  };

  const handleDelete = (resource: AiResource) => {
    setSelectedResource(resource);
    setShowDeleteModal(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedResource) return;

    try {
      await aiService.deleteResource(selectedResource.id);
      setResources(resources.filter(r => r.id !== selectedResource.id));
      setShowDeleteModal(false);
      setSelectedResource(null);
    } catch (error) {
      console.error('Failed to delete resource:', error);
      alert('Failed to delete resource. Please try again.');
    }
  };

  const handleDownload = async (resource: AiResource) => {
    try {
      await aiService.downloadResource(resource.id);
    } catch (error) {
      console.error('Failed to download resource:', error);
      alert('Failed to download resource. Please try again.');
    }
  };

  // Filter and search functionality
  const filteredResources = resources.filter(resource => {
    const matchesSearch = resource.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         resource.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         resource.subjectName.toLowerCase().includes(searchTerm.toLowerCase());
    
    const matchesType = !filterType || resource.type === filterType;
    const matchesStatus = !filterStatus || resource.processingStatus === filterStatus;
    
    return matchesSearch && matchesType && matchesStatus;
  });

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

  const getStatusIcon = (status: ProcessingStatus) => {
    switch (status) {
      case ProcessingStatus.COMPLETED:
        return <CheckCircle className="h-4 w-4 text-green-500" />;
      case ProcessingStatus.FAILED:
        return <XCircle className="h-4 w-4 text-red-500" />;
      case ProcessingStatus.PROCESSING:
        return <Clock className="h-4 w-4 text-blue-500" />;
      default:
        return <AlertCircle className="h-4 w-4 text-yellow-500" />;
    }
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
          <h1 className="text-2xl font-bold text-gray-900">AI Resources</h1>
          <p className="text-gray-600">Upload and manage educational resources for AI training</p>
        </div>
        <Button onClick={() => setShowUploadModal(true)}>
          <Upload className="h-4 w-4 mr-2" />
          Upload Resource
        </Button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <FileText className="h-5 w-5 text-blue-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Total Resources</p>
              <p className="text-xl font-bold text-gray-900">{resources.length}</p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-green-100 rounded-lg">
              <CheckCircle className="h-5 w-5 text-green-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Processed</p>
              <p className="text-xl font-bold text-gray-900">
                {resources.filter(r => r.processed).length}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-yellow-100 rounded-lg">
              <Clock className="h-5 w-5 text-yellow-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Pending</p>
              <p className="text-xl font-bold text-gray-900">
                {resources.filter(r => !r.processed).length}
              </p>
            </div>
          </div>
        </Card>

        <Card className="p-4">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-purple-100 rounded-lg">
              <BookOpen className="h-5 w-5 text-purple-600" />
            </div>
            <div>
              <p className="text-sm text-gray-600">Subjects</p>
              <p className="text-xl font-bold text-gray-900">
                {new Set(resources.map(r => r.subjectId)).size}
              </p>
            </div>
          </div>
        </Card>
      </div>

      {/* Search and Filters */}
      <Card className="p-6">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Search</label>
            <Input
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              placeholder="Search resources..."
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Type</label>
            <Select
              value={filterType}
              onChange={(e) => setFilterType(e.target.value)}
              options={[
                { value: '', label: 'All Types' },
                ...Object.entries(RESOURCE_TYPE_LABELS).map(([value, label]) => ({
                  value,
                  label
                }))
              ]}
              placeholder="All Types"
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Status</label>
            <Select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              options={[
                { value: '', label: 'All Status' },
                ...Object.entries(PROCESSING_STATUS_LABELS).map(([value, label]) => ({
                  value,
                  label
                }))
              ]}
              placeholder="All Status"
            />
          </div>
          <div className="flex items-end">
            <Button 
              variant="outline" 
              onClick={() => {
                setSearchTerm('');
                setFilterType('');
                setFilterStatus('');
              }}
              className="w-full"
            >
              Clear Filters
            </Button>
          </div>
        </div>
      </Card>

      {/* Resources List */}
      <Card className="p-6">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-gray-900">Uploaded Resources</h3>
          <p className="text-sm text-gray-600">{filteredResources.length} of {resources.length} resources</p>
        </div>
        
        {filteredResources.length > 0 ? (
          <div className="space-y-4">
            {filteredResources.map((resource) => (
              <div key={resource.id} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                <div className="flex items-center space-x-4">
                  <div className="p-2 bg-blue-100 rounded-lg">
                    <FileText className="h-5 w-5 text-blue-600" />
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-900">{resource.title}</h4>
                    <p className="text-sm text-gray-600">{resource.subjectName}</p>
                    <div className="flex items-center space-x-2 mt-1">
                      <Badge variant="outline">{RESOURCE_TYPE_LABELS[resource.type]}</Badge>
                      <Badge 
                        variant="outline" 
                        className={`text-${PROCESSING_STATUS_COLORS[resource.processingStatus]}-600 border-${PROCESSING_STATUS_COLORS[resource.processingStatus]}-200`}
                      >
                        {PROCESSING_STATUS_LABELS[resource.processingStatus]}
                      </Badge>
                    </div>
                  </div>
                </div>
                
                <div className="flex items-center space-x-4">
                  <div className="text-right">
                    <p className="text-sm text-gray-600">{formatFileSize(resource.fileSize)}</p>
                    <p className="text-xs text-gray-500">{formatDate(resource.createdAt)}</p>
                  </div>
                  
                  <div className="flex items-center space-x-2">
                    {getStatusIcon(resource.processingStatus)}
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleDownload(resource)}
                      title="Download"
                    >
                      <Download className="h-4 w-4" />
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleEdit(resource)}
                      title="Edit"
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button 
                      variant="outline" 
                      size="sm"
                      onClick={() => handleDelete(resource)}
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
            <Upload className="h-16 w-16 text-gray-300 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">No resources uploaded yet</h3>
            <p className="text-gray-500 mb-4">Start by uploading syllabus, textbooks, and other educational materials</p>
            <Button onClick={() => setShowUploadModal(true)}>
              <Upload className="h-4 w-4 mr-2" />
              Upload Your First Resource
            </Button>
          </div>
        )}
      </Card>

      {/* Upload Modal */}
      <Modal
        isOpen={showUploadModal}
        onClose={() => setShowUploadModal(false)}
        title="Upload Resource"
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              File *
            </label>
            <input
              type="file"
              onChange={handleFileSelect}
              className="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              accept=".pdf,.doc,.docx,.txt,.jpg,.jpeg,.png"
            />
            {selectedFile && (
              <p className="mt-2 text-sm text-gray-600">
                Selected: {selectedFile.name} ({formatFileSize(selectedFile.size)})
              </p>
            )}
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Resource Type *
            </label>
            <Select
              value={uploadForm.type}
              onChange={(e) => setUploadForm({ ...uploadForm, type: e.target.value as ResourceType })}
              options={Object.entries(RESOURCE_TYPE_LABELS).map(([value, label]) => ({
                value,
                label
              }))}
              placeholder="Select resource type"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Subject *
            </label>
            <Select
              value={uploadForm.subjectId}
              onChange={(e) => setUploadForm({ ...uploadForm, subjectId: e.target.value })}
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
              Title *
            </label>
            <Input
              value={uploadForm.title}
              onChange={(e) => setUploadForm({ ...uploadForm, title: e.target.value })}
              placeholder="Enter resource title"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Description
            </label>
            <Textarea
              value={uploadForm.description}
              onChange={(e) => setUploadForm({ ...uploadForm, description: e.target.value })}
              placeholder="Enter resource description"
              rows={3}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Academic Year
              </label>
              <Input
                value={uploadForm.academicYear}
                onChange={(e) => setUploadForm({ ...uploadForm, academicYear: e.target.value })}
                placeholder="e.g., 2024"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Form Level
              </label>
              <Select
                value={uploadForm.formLevel}
                onChange={(e) => setUploadForm({ ...uploadForm, formLevel: e.target.value })}
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

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowUploadModal(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpload} disabled={uploading}>
              {uploading ? 'Uploading...' : 'Upload Resource'}
            </Button>
          </div>
        </div>
      </Modal>

      {/* Edit Resource Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => setShowEditModal(false)}
        title="Edit Resource"
      >
        <div className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Title *
            </label>
            <Input
              value={editForm.title}
              onChange={(e) => setEditForm({ ...editForm, title: e.target.value })}
              placeholder="Enter resource title"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Description
            </label>
            <Textarea
              value={editForm.description}
              onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
              placeholder="Enter resource description"
              rows={3}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Academic Year
              </label>
              <Input
                value={editForm.academicYear}
                onChange={(e) => setEditForm({ ...editForm, academicYear: e.target.value })}
                placeholder="e.g., 2024"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Form Level
              </label>
              <Select
                value={editForm.formLevel}
                onChange={(e) => setEditForm({ ...editForm, formLevel: e.target.value })}
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

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowEditModal(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpdate}>
              Update Resource
            </Button>
          </div>
        </div>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="Delete Resource"
      >
        <div className="space-y-4">
          <div className="text-center">
            <Trash2 className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h3 className="text-lg font-medium text-gray-900 mb-2">
              Delete Resource
            </h3>
            <p className="text-gray-600">
              Are you sure you want to delete "{selectedResource?.title}"? This action cannot be undone.
            </p>
          </div>

          <div className="flex justify-end space-x-3 pt-4">
            <Button variant="outline" onClick={() => setShowDeleteModal(false)}>
              Cancel
            </Button>
            <Button 
              onClick={handleConfirmDelete}
              className="bg-red-600 hover:bg-red-700"
            >
              Delete Resource
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default AiResourcesPage;

