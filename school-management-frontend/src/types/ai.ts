// AI Types - Updated
export interface AiResource {
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

export interface AiGeneratedContent {
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

export interface AiContentGenerationRequest {
  subjectId: number;
  title: string;
  description?: string;
  contentType: ContentType;
  topicFocus?: string;
  difficultyLevel: DifficultyLevel;
  academicYear: string;
  formLevel?: string;
  estimatedDuration?: number;
  totalMarks?: number;
  additionalInstructions?: string;
  syllabusScope?: string;
  targetAudience?: string;
  includeMarkingScheme?: boolean;
  includeExplanations?: boolean;
}

export interface AiUsageStats {
  teacherId: number;
  usageStats: Array<[string, number]>;
  monthlyTokens: number;
  monthlyCost: number;
}

export interface AiMonthlyUsage {
  teacherId: number;
  period: string;
  totalTokens: number;
  totalCost: number;
}

export enum ResourceType {
  SYLLABUS = 'SYLLABUS',
  TEXTBOOK = 'TEXTBOOK',
  PAST_PAPER = 'PAST_PAPER',
  MARKING_SCHEME = 'MARKING_SCHEME',
  STUDY_GUIDE = 'STUDY_GUIDE',
  REFERENCE_MATERIAL = 'REFERENCE_MATERIAL'
}

export enum ContentType {
  STUDY_NOTES = 'STUDY_NOTES',
  PRACTICE_QUESTIONS = 'PRACTICE_QUESTIONS',
  QUIZ = 'QUIZ',
  MIDTERM_EXAM = 'MIDTERM_EXAM',
  FINAL_EXAM = 'FINAL_EXAM',
  ASSIGNMENT = 'ASSIGNMENT',
  REVISION_MATERIAL = 'REVISION_MATERIAL'
}

export enum DifficultyLevel {
  EASY = 'EASY',
  MEDIUM = 'MEDIUM',
  HARD = 'HARD',
  EXPERT = 'EXPERT'
}

export enum ProcessingStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED'
}

// Resource Type Labels
export const RESOURCE_TYPE_LABELS = {
  [ResourceType.SYLLABUS]: 'Syllabus',
  [ResourceType.TEXTBOOK]: 'Textbook',
  [ResourceType.PAST_PAPER]: 'Past Paper',
  [ResourceType.MARKING_SCHEME]: 'Marking Scheme',
  [ResourceType.STUDY_GUIDE]: 'Study Guide',
  [ResourceType.REFERENCE_MATERIAL]: 'Reference Material'
};

// Content Type Labels
export const CONTENT_TYPE_LABELS = {
  [ContentType.STUDY_NOTES]: 'Study Notes',
  [ContentType.PRACTICE_QUESTIONS]: 'Practice Questions',
  [ContentType.QUIZ]: 'Quiz',
  [ContentType.MIDTERM_EXAM]: 'Midterm Exam',
  [ContentType.FINAL_EXAM]: 'Final Exam',
  [ContentType.ASSIGNMENT]: 'Assignment',
  [ContentType.REVISION_MATERIAL]: 'Revision Material'
};

// Difficulty Level Labels
export const DIFFICULTY_LEVEL_LABELS = {
  [DifficultyLevel.EASY]: 'Easy',
  [DifficultyLevel.MEDIUM]: 'Medium',
  [DifficultyLevel.HARD]: 'Hard',
  [DifficultyLevel.EXPERT]: 'Expert'
};

// Processing Status Labels
export const PROCESSING_STATUS_LABELS = {
  [ProcessingStatus.PENDING]: 'Pending',
  [ProcessingStatus.PROCESSING]: 'Processing',
  [ProcessingStatus.COMPLETED]: 'Completed',
  [ProcessingStatus.FAILED]: 'Failed'
};

// Processing Status Colors
export const PROCESSING_STATUS_COLORS = {
  [ProcessingStatus.PENDING]: 'yellow',
  [ProcessingStatus.PROCESSING]: 'blue',
  [ProcessingStatus.COMPLETED]: 'green',
  [ProcessingStatus.FAILED]: 'red'
};

// AI Model Configuration
export interface AiModelConfig {
  modelId: string;
  modelName: string;
  provider: string;
  description: string;
  available: boolean;
  costPerToken: number;
  maxTokens: number;
  capabilities: string;
}

// AI Template
export interface AiTemplate {
  id: number;
  teacherId: number;
  teacherName: string;
  name: string;
  description: string;
  contentType: ContentType;
  difficultyLevel: DifficultyLevel;
  formLevel: string;
  estimatedDuration: number;
  totalMarks: number;
  additionalInstructions: string;
  isPublic: boolean;
  usageCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CreateTemplateRequest {
  name: string;
  description: string;
  contentType: ContentType;
  difficultyLevel: DifficultyLevel;
  formLevel: string;
  estimatedDuration: number;
  totalMarks: number;
  additionalInstructions: string;
  isPublic: boolean;
}
