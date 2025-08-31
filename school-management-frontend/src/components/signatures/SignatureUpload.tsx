import React, { useState, useEffect } from 'react';
import { Card, Button } from '../ui';
import { signatureService, type SignatureData } from '../../services/signatureService';
import { Upload, Check, PenTool } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { getImageUrl } from '../../utils/imageUtils';
import SignaturePad from './SignaturePad';

const SignatureUpload: React.FC = () => {
  const [signature, setSignature] = useState<SignatureData | null>(null);
  const [hasTeacherRecord, setHasTeacherRecord] = useState<boolean>(true);
  const [uploading, setUploading] = useState(false);
  const [useDigitalPad, setUseDigitalPad] = useState(true);

  useEffect(() => {
    loadMySignature();
  }, []);

  const loadMySignature = async () => {
    try {
      const signatureData = await signatureService.getMySignature();
      setSignature(signatureData);
      setHasTeacherRecord(true);
    } catch (error) {
      console.error('Error loading signature:', error);
      // Check if it's a 500 error (no teacher record)
      if (error.response?.status === 500) {
        setHasTeacherRecord(false);
      }
      setSignature(null);
    }
  };

  const handleFileSelect = async (file: File) => {
    if (!file.type.startsWith('image/')) {
      toast.error('Please select an image file');
      return;
    }

    try {
      setUploading(true);
      const uploadedSignature = await signatureService.uploadSignature(file);
      setSignature(uploadedSignature);
      setHasTeacherRecord(true);
      toast.success('Signature uploaded successfully');
    } catch (error) {
      toast.error('Failed to upload signature');
    } finally {
      setUploading(false);
    }
  };

  const handleFileInput = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (files && files.length > 0) {
      handleFileSelect(files[0]);
    }
  };

  const handleSignatureSaved = (savedSignature: SignatureData) => {
    setSignature(savedSignature);
  };

  return (
    <Card className="p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold">Signature Management</h3>
        <div className="flex space-x-2">
          <Button
            variant={useDigitalPad ? "default" : "outline"}
            size="sm"
            onClick={() => setUseDigitalPad(true)}
          >
            <PenTool className="h-4 w-4 mr-1" />
            Digital Pad
          </Button>
          <Button
            variant={!useDigitalPad ? "default" : "outline"}
            size="sm"
            onClick={() => setUseDigitalPad(false)}
          >
            <Upload className="h-4 w-4 mr-1" />
            Upload File
          </Button>
        </div>
      </div>
      
      {signature ? (
        <div className="space-y-4">
          <div className="flex items-center space-x-2 text-green-600">
            <Check className="h-5 w-5" />
            <span>Signature saved</span>
          </div>
          
          <div className="border rounded-lg p-4 bg-gray-50">
            <img 
              src={getImageUrl(signature.signatureUrl) || ''}
              alt="My Signature"
              className="max-h-20 max-w-full object-contain"
            />
          </div>
          
          <div className="flex space-x-2">
            <Button
              variant="outline"
              onClick={() => setSignature(null)}
            >
              <Upload className="h-4 w-4 mr-2" />
              Create New Signature
            </Button>
          </div>
        </div>
      ) : (
        <div className="space-y-4">
          {!hasTeacherRecord && (
            <div className="p-3 bg-blue-50 border border-blue-200 rounded-lg">
              <p className="text-sm text-blue-800">
                System will automatically create your teacher profile when you save your signature.
              </p>
            </div>
          )}
          
          {useDigitalPad ? (
            <SignaturePad onSignatureSaved={handleSignatureSaved} />
          ) : (
            <div className="border-2 border-dashed rounded-lg p-8 text-center">
              <Upload className="h-12 w-12 text-gray-400 mx-auto mb-4" />
              <h4 className="text-lg font-medium text-gray-900 mb-2">Upload Your Signature</h4>
              <Button
                onClick={() => document.getElementById('signature-file')?.click()}
                disabled={uploading}
                loading={uploading}
              >
                Select Image
              </Button>
            </div>
          )}
        </div>
      )}
      
      <input
        id="signature-file"
        type="file"
        accept="image/*"
        onChange={handleFileInput}
        className="hidden"
      />
    </Card>
  );
};

export default SignatureUpload;