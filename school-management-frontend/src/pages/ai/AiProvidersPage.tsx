import React, { useState, useEffect } from 'react';
import { 
  Brain, 
  CheckCircle, 
  XCircle, 
  Settings, 
  DollarSign,
  Zap,
  Shield,
  Globe,
  Server,
  Cpu
} from 'lucide-react';
import Card from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Select from '../../components/ui/Select';
import { useAuth } from '../../hooks/useAuth';
import { aiService } from '../../services/aiService';

interface ProviderStatus {
  available: boolean;
  configured: boolean;
}

interface AiModel {
  id: string;
  name: string;
  provider: string;
  description: string;
  available: boolean;
  costPer1kTokens: number;
  maxTokens: number;
  useCases: string;
}

const AiProvidersPage: React.FC = () => {
  const { theme } = useAuth();
  const [providerStatus, setProviderStatus] = useState<Record<string, ProviderStatus>>({});
  const [availableModels, setAvailableModels] = useState<AiModel[]>([]);
  const [selectedProvider, setSelectedProvider] = useState<string>('openai');
  const [selectedModel, setSelectedModel] = useState<string>('gpt-4o-mini');
  const [currentProvider, setCurrentProvider] = useState<string>('');
  const [currentModel, setCurrentModel] = useState<string>('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProviderData = async () => {
      try {
        const [status, models, current] = await Promise.all([
          aiService.getProviderStatus(),
          aiService.getAvailableModels(),
          aiService.getCurrentProvider()
        ]);
        setProviderStatus(status);
        setAvailableModels(models);
        setCurrentProvider(current.provider);
        setCurrentModel(current.model);
        setSelectedProvider(current.provider);
        setSelectedModel(current.model);
      } catch (error) {
        console.error('Failed to fetch provider data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchProviderData();
  }, []);

  const handleProviderSelect = async () => {
    try {
      await aiService.selectProvider(selectedProvider, selectedModel);
      setCurrentProvider(selectedProvider);
      setCurrentModel(selectedModel);
      alert('AI provider selected successfully!');
    } catch (error) {
      console.error('Failed to select provider:', error);
      alert('Failed to select provider. Please try again.');
    }
  };

  const getProviderIcon = (provider: string) => {
    switch (provider) {
      case 'openai': return <Brain className="h-5 w-5" />;
      case 'anthropic': return <Shield className="h-5 w-5" />;
      case 'google': return <Globe className="h-5 w-5" />;
      case 'azure-openai': return <Server className="h-5 w-5" />;
      case 'huggingface': return <Zap className="h-5 w-5" />;
      case 'local': return <Cpu className="h-5 w-5" />;
      case 'mock': return <Settings className="h-5 w-5" />;
      default: return <Brain className="h-5 w-5" />;
    }
  };

  const getProviderColor = (provider: string) => {
    switch (provider) {
      case 'openai': return 'bg-green-100 text-green-800';
      case 'anthropic': return 'bg-blue-100 text-blue-800';
      case 'google': return 'bg-red-100 text-red-800';
      case 'azure-openai': return 'bg-blue-100 text-blue-800';
      case 'huggingface': return 'bg-yellow-100 text-yellow-800';
      case 'local': return 'bg-purple-100 text-purple-800';
      case 'mock': return 'bg-gray-100 text-gray-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const formatCost = (cost: number) => {
    if (cost === 0) return 'Free';
    return `$${cost.toFixed(4)}/1k tokens`;
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
          <h1 className="text-2xl font-bold text-gray-900">AI Providers</h1>
          <p className="text-gray-600">Manage and configure AI model providers for content generation</p>
        </div>
        <div className="flex items-center space-x-2">
          <Settings className="h-8 w-8 text-blue-500" />
        </div>
      </div>

      {/* Current Selection */}
      {currentProvider && currentModel && (
        <Card className="p-6 bg-blue-50 border-blue-200">
          <div className="flex items-center space-x-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              {getProviderIcon(currentProvider)}
            </div>
            <div>
              <h2 className="text-lg font-semibold text-blue-900">Currently Selected Model</h2>
              <p className="text-blue-700">
                <span className="font-medium">{currentProvider.replace('-', ' ').toUpperCase()}</span> - {currentModel}
              </p>
              <p className="text-sm text-blue-600">This model is currently being used for AI content generation</p>
            </div>
            <div className="ml-auto">
              <div className="flex items-center space-x-2">
                <CheckCircle className="h-6 w-6 text-green-500" />
                <span className="text-sm font-medium text-green-700">Active</span>
              </div>
            </div>
          </div>
        </Card>
      )}

      {/* Provider Status Overview */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {Object.entries(providerStatus).map(([provider, status]) => (
          <Card key={provider} className="p-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center space-x-3">
                {getProviderIcon(provider)}
                <div>
                  <h3 className="font-semibold text-gray-900 capitalize">
                    {provider.replace('-', ' ')}
                  </h3>
                  <p className="text-sm text-gray-600">
                    {status.configured ? 'Configured' : 'Not configured'}
                  </p>
                </div>
              </div>
              <div className="flex items-center space-x-2">
                {status.available ? (
                  <CheckCircle className="h-5 w-5 text-green-500" />
                ) : (
                  <XCircle className="h-5 w-5 text-red-500" />
                )}
              </div>
            </div>
          </Card>
        ))}
      </div>

      {/* Provider Selection */}
      <Card className="p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Select AI Provider</h2>
        {currentProvider && currentModel && (
          <div className="mb-4 p-3 bg-gray-50 rounded-lg">
            <p className="text-sm text-gray-600">
              <span className="font-medium">Current:</span> {currentProvider.replace('-', ' ').toUpperCase()} - {currentModel}
            </p>
          </div>
        )}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Provider
            </label>
            <Select
              value={selectedProvider}
              onChange={(e) => setSelectedProvider(e.target.value)}
              options={[
                { value: 'openai', label: 'OpenAI' },
                { value: 'anthropic', label: 'Anthropic Claude' },
                { value: 'google', label: 'Google Gemini' },
                { value: 'azure-openai', label: 'Azure OpenAI' },
                { value: 'huggingface', label: 'Hugging Face' },
                { value: 'local', label: 'Local AI' },
                { value: 'mock', label: 'Mock AI' }
              ]}
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Model
            </label>
            <Select
              value={selectedModel}
              onChange={(e) => setSelectedModel(e.target.value)}
              options={availableModels
                .filter(model => model.provider === selectedProvider)
                .map(model => ({
                  value: model.id,
                  label: model.name
                }))}
            />
          </div>
        </div>
        <div className="mt-4">
          <Button onClick={handleProviderSelect}>
            Select Provider
          </Button>
        </div>
      </Card>

      {/* Available Models */}
      <Card className="p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Available Models</h2>
        <div className="space-y-4">
          {availableModels.map((model) => (
            <div key={model.id} className="border rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  {getProviderIcon(model.provider)}
                  <div>
                    <h3 className="font-semibold text-gray-900">{model.name}</h3>
                    <p className="text-sm text-gray-600">{model.description}</p>
                    <p className="text-xs text-gray-500 mt-1">{model.useCases}</p>
                  </div>
                </div>
                <div className="text-right">
                  <div className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getProviderColor(model.provider)}`}>
                    {model.provider.replace('-', ' ')}
                  </div>
                  <div className="mt-2 flex items-center space-x-4 text-sm text-gray-600">
                    <div className="flex items-center space-x-1">
                      <DollarSign className="h-4 w-4" />
                      <span>{formatCost(model.costPer1kTokens)}</span>
                    </div>
                    <div className="flex items-center space-x-1">
                      <Zap className="h-4 w-4" />
                      <span>{model.maxTokens.toLocaleString()} tokens</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      </Card>

      {/* Setup Instructions */}
      <Card className="p-6">
        <h2 className="text-lg font-semibold text-gray-900 mb-4">Setup Instructions</h2>
        <div className="space-y-4">
          <div>
            <h3 className="font-medium text-gray-900">OpenAI</h3>
            <p className="text-sm text-gray-600">
              Add your OpenAI API key to the environment variables: <code>AI_OPENAI_API_KEY</code>
            </p>
          </div>
          <div>
            <h3 className="font-medium text-gray-900">Anthropic Claude</h3>
            <p className="text-sm text-gray-600">
              Add your Anthropic API key to the environment variables: <code>AI_ANTHROPIC_API_KEY</code>
            </p>
          </div>
          <div>
            <h3 className="font-medium text-gray-900">Google Gemini</h3>
            <p className="text-sm text-gray-600">
              Add your Google AI API key to the environment variables: <code>AI_GOOGLE_API_KEY</code>
            </p>
          </div>
          <div>
            <h3 className="font-medium text-gray-900">Local AI (Ollama)</h3>
            <p className="text-sm text-gray-600">
              Set up Ollama locally and configure: <code>AI_LOCAL_ENDPOINT=http://localhost:11434</code>
            </p>
          </div>
        </div>
      </Card>
    </div>
  );
};

export default AiProvidersPage;
