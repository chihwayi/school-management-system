import React, { useRef, useState, useEffect } from 'react';
import SignatureCanvas from 'react-signature-canvas';
import { Card, Button } from '../ui';
import { signatureService, type SignatureData } from '../../services/signatureService';
import { Check, RotateCcw, Download, Upload } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { getImageUrl } from '../../utils/imageUtils';

interface SignaturePadProps {
  onSignatureSaved?: (signature: SignatureData) => void;
}

const SignaturePad: React.FC<SignaturePadProps> = ({ onSignatureSaved }) => {
  const signaturePadRef = useRef<SignatureCanvas>(null);
  const [signature, setSignature] = useState<SignatureData | null>(null);
  const [hasTeacherRecord, setHasTeacherRecord] = useState<boolean>(true);
  const [saving, setSaving] = useState(false);
  const [isDrawing, setIsDrawing] = useState(false);

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

  const handleBegin = () => {
    setIsDrawing(true);
  };

  const handleEnd = () => {
    setIsDrawing(false);
  };

  const clearSignature = () => {
    if (signaturePadRef.current) {
      signaturePadRef.current.clear();
    }
  };

  const saveSignature = async () => {
    if (!signaturePadRef.current) {
      toast.error('Signature pad not initialized');
      return;
    }

    if (signaturePadRef.current.isEmpty()) {
      toast.error('Please draw your signature first');
      return;
    }

    try {
      setSaving(true);
      
      // Convert canvas to blob
      const canvas = signaturePadRef.current.getCanvas();
      canvas.toBlob(async (blob) => {
        if (!blob) {
          toast.error('Failed to generate signature image');
          return;
        }

        // Create a File object from the blob
        const file = new File([blob], 'signature.png', { type: 'image/png' });
        
        // Upload the signature
        const uploadedSignature = await signatureService.uploadSignature(file);
        setSignature(uploadedSignature);
        setHasTeacherRecord(true);
        toast.success('Signature saved successfully');
        
        if (onSignatureSaved) {
          onSignatureSaved(uploadedSignature);
        }
      }, 'image/png');
    } catch (error) {
      toast.error('Failed to save signature');
    } finally {
      setSaving(false);
    }
  };

  const downloadSignature = () => {
    if (!signaturePadRef.current) return;
    
    const canvas = signaturePadRef.current.getCanvas();
    const link = document.createElement('a');
    link.download = 'signature.png';
    link.href = canvas.toDataURL();
    link.click();
  };

  return (
    <Card className="p-6">
      <div className="flex items-center mb-4">
        <Check className="h-6 w-6 text-blue-600 mr-2" />
        <h3 className="text-lg font-semibold">Digital Signature</h3>
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
          
          <div className="border-2 border-dashed rounded-lg p-4">
            <div className="text-center mb-4">
              <h4 className="text-lg font-medium text-gray-900 mb-2">Draw Your Signature</h4>
              <p className="text-sm text-gray-600">
                Use your mouse or touch to draw your signature below
              </p>
            </div>
            
            <div className="border rounded-lg bg-white">
              <SignatureCanvas
                ref={signaturePadRef}
                canvasProps={{
                  className: 'w-full h-48 border-0',
                  style: { 
                    border: 'none',
                    backgroundColor: 'white'
                  }
                }}
                onBegin={handleBegin}
                onEnd={handleEnd}
                penColor="black"
                backgroundColor="white"
                minWidth={2}
                maxWidth={3}
              />
            </div>
            
            <div className="flex justify-between items-center mt-4">
              <div className="flex space-x-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={clearSignature}
                >
                  <RotateCcw className="h-4 w-4 mr-1" />
                  Clear
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={downloadSignature}
                >
                  <Download className="h-4 w-4 mr-1" />
                  Download
                </Button>
              </div>
              
              <Button
                onClick={saveSignature}
                disabled={saving}
                loading={saving}
              >
                Save Signature
              </Button>
            </div>
          </div>
        </div>
      )}
    </Card>
  );
};

export default SignaturePad;
