import React, { useState, useEffect } from 'react';
import Card, { CardContent, CardHeader, CardTitle } from '../../components/ui/Card';
import Button from '../../components/ui/Button';
import Input from '../../components/ui/Input';
import Label from '../../components/ui/Label';
import Badge from '../../components/ui/Badge';
import Switch from '../../components/ui/Switch';
import Textarea from '../../components/ui/Textarea';
import { 
  Dialog, 
  DialogContent, 
  DialogHeader, 
  DialogTitle, 
  DialogTrigger,
  DialogFooter 
} from '../../components/ui/Dialog';
// Select components not used in this page
import { 
  Settings, 
  Plus, 
  Edit, 
  Trash2, 
  Key, 
  Globe, 
  Cpu, 
  DollarSign,
  CheckCircle,
  XCircle,
  AlertCircle,
  RefreshCw
} from 'lucide-react';
import { toast } from 'react-hot-toast';
import { aiProviderConfigService } from '../../services/aiProviderConfigService';
import type { AiProviderConfig } from '../../services/aiProviderConfigService';

const AiProviderConfigPage: React.FC = () => {
  const [providers, setProviders] = useState<AiProviderConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [editingProvider, setEditingProvider] = useState<AiProviderConfig | null>(null);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [formData, setFormData] = useState<Partial<AiProviderConfig>>({});

  useEffect(() => {
    fetchProviders();
  }, []);

  const fetchProviders = async () => {
    try {
      setLoading(true);
      const data = await aiProviderConfigService.getAllProviderConfigs();
      setProviders(data);
    } catch (error) {
      console.error('Failed to fetch providers:', error);
      toast.error('Failed to fetch AI provider configurations');
    } finally {
      setLoading(false);
    }
  };

  const initializeDefaultProviders = async () => {
    try {
      await aiProviderConfigService.initializeDefaultProviders();
      toast.success('Default providers initialized successfully');
      fetchProviders();
    } catch (error) {
      console.error('Failed to initialize providers:', error);
      toast.error('Failed to initialize default providers');
    }
  };

  const handleCreateProvider = async () => {
    try {
      await aiProviderConfigService.createProviderConfig(formData as any);
      toast.success('Provider configuration created successfully');
      setIsCreateDialogOpen(false);
      setFormData({});
      fetchProviders();
    } catch (error) {
      console.error('Failed to create provider:', error);
      toast.error('Failed to create provider configuration');
    }
  };

  const handleUpdateProvider = async () => {
    if (!editingProvider?.id) return;
    
    try {
      await aiProviderConfigService.updateProviderConfig(editingProvider.id, formData as any);
      toast.success('Provider configuration updated successfully');
      setIsEditDialogOpen(false);
      setEditingProvider(null);
      setFormData({});
      fetchProviders();
    } catch (error) {
      console.error('Failed to update provider:', error);
      toast.error('Failed to update provider configuration');
    }
  };

  const handleDeleteProvider = async (id: number) => {
    if (!confirm('Are you sure you want to delete this provider configuration?')) return;
    
    try {
      await aiProviderConfigService.deleteProviderConfig(id);
      toast.success('Provider configuration deleted successfully');
      fetchProviders();
    } catch (error) {
      console.error('Failed to delete provider:', error);
      toast.error('Failed to delete provider configuration');
    }
  };

  const handleToggleProvider = async (id: number) => {
    try {
      await aiProviderConfigService.toggleProviderStatus(id);
      toast.success('Provider status updated successfully');
      fetchProviders();
    } catch (error) {
      console.error('Failed to toggle provider:', error);
      toast.error('Failed to update provider status');
    }
  };

  const openEditDialog = (provider: AiProviderConfig) => {
    setEditingProvider(provider);
    setFormData(provider);
    setIsEditDialogOpen(true);
  };

  const getStatusBadge = (provider: AiProviderConfig) => {
    if (!provider.isEnabled) {
      return <Badge variant="secondary">Disabled</Badge>;
    }
    if (provider.isConfigured) {
      return <Badge variant="default" className="bg-green-500">Configured</Badge>;
    }
    return <Badge variant="destructive">Not Configured</Badge>;
  };

  const getProviderIcon = (providerName: string) => {
    switch (providerName.toLowerCase()) {
      case 'openai':
        return <Cpu className="h-5 w-5 text-green-600" />;
      case 'anthropic':
        return <Cpu className="h-5 w-5 text-orange-600" />;
      case 'google':
        return <Cpu className="h-5 w-5 text-blue-600" />;
      case 'local':
        return <Cpu className="h-5 w-5 text-purple-600" />;
      default:
        return <Cpu className="h-5 w-5 text-gray-600" />;
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <RefreshCw className="h-8 w-8 animate-spin" />
        <span className="ml-2">Loading AI provider configurations...</span>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">AI Provider Configuration</h1>
          <p className="text-gray-600 mt-2">
            Manage AI provider API keys and configurations for your school
          </p>
        </div>
        <div className="flex space-x-3">
          <Button
            onClick={initializeDefaultProviders}
            variant="outline"
            className="flex items-center space-x-2"
          >
            <RefreshCw className="h-4 w-4" />
            <span>Initialize Defaults</span>
          </Button>
          <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
            <DialogTrigger asChild>
              <Button className="flex items-center space-x-2">
                <Plus className="h-4 w-4" />
                <span>Add Provider</span>
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl">
              <DialogHeader>
                <DialogTitle>Add New AI Provider</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="providerName">Provider Name</Label>
                    <Input
                      id="providerName"
                      value={formData.providerName || ''}
                      onChange={(e) => setFormData({ ...formData, providerName: e.target.value })}
                      placeholder="e.g., openai, anthropic"
                    />
                  </div>
                  <div>
                    <Label htmlFor="displayName">Display Name</Label>
                    <Input
                      id="displayName"
                      value={formData.displayName || ''}
                      onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                      placeholder="e.g., OpenAI, Anthropic"
                    />
                  </div>
                </div>
                <div>
                  <Label htmlFor="apiKey">API Key</Label>
                  <Input
                    id="apiKey"
                    type="password"
                    value={formData.apiKey || ''}
                    onChange={(e) => setFormData({ ...formData, apiKey: e.target.value })}
                    placeholder="Enter API key"
                  />
                </div>
                <div>
                  <Label htmlFor="apiUrl">API URL</Label>
                  <Input
                    id="apiUrl"
                    value={formData.apiUrl || ''}
                    onChange={(e) => setFormData({ ...formData, apiUrl: e.target.value })}
                    placeholder="e.g., https://api.openai.com/v1/chat/completions"
                  />
                </div>
                <div>
                  <Label htmlFor="defaultModel">Default Model</Label>
                  <Input
                    id="defaultModel"
                    value={formData.defaultModel || ''}
                    onChange={(e) => setFormData({ ...formData, defaultModel: e.target.value })}
                    placeholder="e.g., gpt-4o-mini"
                  />
                </div>
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <Label htmlFor="maxTokens">Max Tokens</Label>
                    <Input
                      id="maxTokens"
                      type="number"
                      value={formData.maxTokens || ''}
                      onChange={(e) => setFormData({ ...formData, maxTokens: parseInt(e.target.value) })}
                      placeholder="4000"
                    />
                  </div>
                  <div>
                    <Label htmlFor="temperature">Temperature</Label>
                    <Input
                      id="temperature"
                      type="number"
                      step="0.1"
                      value={formData.temperature || ''}
                      onChange={(e) => setFormData({ ...formData, temperature: parseFloat(e.target.value) })}
                      placeholder="0.7"
                    />
                  </div>
                  <div>
                    <Label htmlFor="costPer1kTokens">Cost per 1K Tokens</Label>
                    <Input
                      id="costPer1kTokens"
                      type="number"
                      step="0.0001"
                      value={formData.costPer1kTokens || ''}
                      onChange={(e) => setFormData({ ...formData, costPer1kTokens: parseFloat(e.target.value) })}
                      placeholder="0.00015"
                    />
                  </div>
                </div>
                <div>
                  <Label htmlFor="description">Description</Label>
                  <Textarea
                    id="description"
                    value={formData.description || ''}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    placeholder="Provider description"
                    rows={3}
                  />
                </div>
                <div>
                  <Label htmlFor="useCases">Use Cases</Label>
                  <Textarea
                    id="useCases"
                    value={formData.useCases || ''}
                    onChange={(e) => setFormData({ ...formData, useCases: e.target.value })}
                    placeholder="Content generation, analysis, problem-solving"
                    rows={2}
                  />
                </div>
                <div className="flex items-center space-x-2">
                  <Switch
                    id="isEnabled"
                    checked={formData.isEnabled || false}
                    onCheckedChange={(checked) => setFormData({ ...formData, isEnabled: checked })}
                  />
                  <Label htmlFor="isEnabled">Enable this provider</Label>
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                  Cancel
                </Button>
                <Button onClick={handleCreateProvider}>
                  Create Provider
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Providers Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {providers.map((provider) => (
          <Card key={provider.id} className="relative">
            <CardHeader className="pb-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center space-x-3">
                  {getProviderIcon(provider.providerName)}
                  <div>
                    <CardTitle className="text-lg">{provider.displayName}</CardTitle>
                    <p className="text-sm text-gray-500">{provider.providerName}</p>
                  </div>
                </div>
                {getStatusBadge(provider)}
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="space-y-2">
                <div className="flex items-center space-x-2 text-sm">
                  <Key className="h-4 w-4 text-gray-400" />
                  <span className={provider.hasApiKey ? 'text-green-600' : 'text-red-600'}>
                    {provider.hasApiKey ? 'API Key Configured' : 'No API Key'}
                  </span>
                </div>
                <div className="flex items-center space-x-2 text-sm">
                  <Globe className="h-4 w-4 text-gray-400" />
                  <span className="truncate">{provider.apiUrl || 'No URL'}</span>
                </div>
                <div className="flex items-center space-x-2 text-sm">
                  <Cpu className="h-4 w-4 text-gray-400" />
                  <span>{provider.defaultModel || 'No Model'}</span>
                </div>
                <div className="flex items-center space-x-2 text-sm">
                  <DollarSign className="h-4 w-4 text-gray-400" />
                  <span>${provider.costPer1kTokens || 0}/1K tokens</span>
                </div>
              </div>
              
              {provider.description && (
                <p className="text-sm text-gray-600 line-clamp-2">
                  {provider.description}
                </p>
              )}

              <div className="flex items-center justify-between pt-2">
                <div className="flex items-center space-x-2">
                  <Switch
                    checked={provider.isEnabled || false}
                    onCheckedChange={() => handleToggleProvider(provider.id!)}
                  />
                  <span className="text-sm">Enabled</span>
                </div>
                <div className="flex space-x-1">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => openEditDialog(provider)}
                  >
                    <Edit className="h-3 w-3" />
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => handleDeleteProvider(provider.id!)}
                    className="text-red-600 hover:text-red-700"
                  >
                    <Trash2 className="h-3 w-3" />
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Edit Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>Edit AI Provider</DialogTitle>
          </DialogHeader>
          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="edit-providerName">Provider Name</Label>
                <Input
                  id="edit-providerName"
                  value={formData.providerName || ''}
                  onChange={(e) => setFormData({ ...formData, providerName: e.target.value })}
                  placeholder="e.g., openai, anthropic"
                />
              </div>
              <div>
                <Label htmlFor="edit-displayName">Display Name</Label>
                <Input
                  id="edit-displayName"
                  value={formData.displayName || ''}
                  onChange={(e) => setFormData({ ...formData, displayName: e.target.value })}
                  placeholder="e.g., OpenAI, Anthropic"
                />
              </div>
            </div>
            <div>
              <Label htmlFor="edit-apiKey">API Key</Label>
              <Input
                id="edit-apiKey"
                type="password"
                value={formData.apiKey || ''}
                onChange={(e) => setFormData({ ...formData, apiKey: e.target.value })}
                placeholder="Enter API key"
              />
            </div>
            <div>
              <Label htmlFor="edit-apiUrl">API URL</Label>
              <Input
                id="edit-apiUrl"
                value={formData.apiUrl || ''}
                onChange={(e) => setFormData({ ...formData, apiUrl: e.target.value })}
                placeholder="e.g., https://api.openai.com/v1/chat/completions"
              />
            </div>
            <div>
              <Label htmlFor="edit-defaultModel">Default Model</Label>
              <Input
                id="edit-defaultModel"
                value={formData.defaultModel || ''}
                onChange={(e) => setFormData({ ...formData, defaultModel: e.target.value })}
                placeholder="e.g., gpt-4o-mini"
              />
            </div>
            <div className="grid grid-cols-3 gap-4">
              <div>
                <Label htmlFor="edit-maxTokens">Max Tokens</Label>
                <Input
                  id="edit-maxTokens"
                  type="number"
                  value={formData.maxTokens || ''}
                  onChange={(e) => setFormData({ ...formData, maxTokens: parseInt(e.target.value) })}
                  placeholder="4000"
                />
              </div>
              <div>
                <Label htmlFor="edit-temperature">Temperature</Label>
                <Input
                  id="edit-temperature"
                  type="number"
                  step="0.1"
                  value={formData.temperature || ''}
                  onChange={(e) => setFormData({ ...formData, temperature: parseFloat(e.target.value) })}
                  placeholder="0.7"
                />
              </div>
              <div>
                <Label htmlFor="edit-costPer1kTokens">Cost per 1K Tokens</Label>
                <Input
                  id="edit-costPer1kTokens"
                  type="number"
                  step="0.0001"
                  value={formData.costPer1kTokens || ''}
                  onChange={(e) => setFormData({ ...formData, costPer1kTokens: parseFloat(e.target.value) })}
                  placeholder="0.00015"
                />
              </div>
            </div>
            <div>
              <Label htmlFor="edit-description">Description</Label>
              <Textarea
                id="edit-description"
                value={formData.description || ''}
                onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                placeholder="Provider description"
                rows={3}
              />
            </div>
            <div>
              <Label htmlFor="edit-useCases">Use Cases</Label>
              <Textarea
                id="edit-useCases"
                value={formData.useCases || ''}
                onChange={(e) => setFormData({ ...formData, useCases: e.target.value })}
                placeholder="Content generation, analysis, problem-solving"
                rows={2}
              />
            </div>
            <div className="flex items-center space-x-2">
              <Switch
                id="edit-isEnabled"
                checked={formData.isEnabled || false}
                onCheckedChange={(checked) => setFormData({ ...formData, isEnabled: checked })}
              />
              <Label htmlFor="edit-isEnabled">Enable this provider</Label>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditDialogOpen(false)}>
              Cancel
            </Button>
            <Button onClick={handleUpdateProvider}>
              Update Provider
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default AiProviderConfigPage;
