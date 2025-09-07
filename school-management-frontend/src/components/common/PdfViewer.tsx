import React, { useState, useRef, useEffect } from 'react';
import { Button } from '../ui';
import { Download, ZoomIn, ZoomOut, RotateCw, X, FileText } from 'lucide-react';

interface PdfViewerProps {
  pdfUrl: string;
  title?: string;
  onClose?: () => void;
  className?: string;
}

const PdfViewer: React.FC<PdfViewerProps> = ({ 
  pdfUrl, 
  title = "PDF Document", 
  onClose,
  className = ""
}) => {
  const [scale, setScale] = useState(1);
  const [rotation, setRotation] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const iframeRef = useRef<HTMLIFrameElement>(null);

  useEffect(() => {
    setLoading(true);
    setError(null);
  }, [pdfUrl]);

  const handleZoomIn = () => {
    setScale(prev => Math.min(prev + 0.25, 3));
  };

  const handleZoomOut = () => {
    setScale(prev => Math.max(prev - 0.25, 0.5));
  };

  const handleRotate = () => {
    setRotation(prev => (prev + 90) % 360);
  };

  const handleDownload = () => {
    const link = document.createElement('a');
    link.href = pdfUrl;
    link.download = `${title}.pdf`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleIframeLoad = () => {
    setLoading(false);
  };

  const handleIframeError = () => {
    setLoading(false);
    setError('Failed to load PDF. Please try again.');
  };

  return (
    <div className={`fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 ${className}`}>
      <div className="bg-white rounded-lg shadow-xl max-w-6xl max-h-[90vh] w-full mx-4 flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-4 border-b">
          <div className="flex items-center">
            <FileText className="h-5 w-5 text-blue-600 mr-2" />
            <h2 className="text-lg font-semibold text-gray-900">{title}</h2>
          </div>
          <div className="flex items-center space-x-2">
            {/* Zoom Controls */}
            <div className="flex items-center space-x-1">
              <Button
                variant="outline"
                size="sm"
                onClick={handleZoomOut}
                disabled={scale <= 0.5}
                className="p-2"
              >
                <ZoomOut className="h-4 w-4" />
              </Button>
              <span className="text-sm text-gray-600 min-w-[3rem] text-center">
                {Math.round(scale * 100)}%
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={handleZoomIn}
                disabled={scale >= 3}
                className="p-2"
              >
                <ZoomIn className="h-4 w-4" />
              </Button>
            </div>

            {/* Rotate Button */}
            <Button
              variant="outline"
              size="sm"
              onClick={handleRotate}
              className="p-2"
            >
              <RotateCw className="h-4 w-4" />
            </Button>

            {/* Download Button */}
            <Button
              variant="outline"
              size="sm"
              onClick={handleDownload}
              className="p-2"
            >
              <Download className="h-4 w-4" />
            </Button>

            {/* Close Button */}
            {onClose && (
              <Button
                variant="outline"
                size="sm"
                onClick={onClose}
                className="p-2"
              >
                <X className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>

        {/* PDF Content */}
        <div className="flex-1 overflow-hidden relative">
          {loading && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-50">
              <div className="text-center">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto mb-2"></div>
                <p className="text-gray-600">Loading PDF...</p>
              </div>
            </div>
          )}

          {error && (
            <div className="absolute inset-0 flex items-center justify-center bg-gray-50">
              <div className="text-center">
                <FileText className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                <p className="text-red-600 mb-4">{error}</p>
                <Button onClick={() => window.location.reload()}>
                  Retry
                </Button>
              </div>
            </div>
          )}

          <div 
            className="w-full h-full overflow-auto"
            style={{
              transform: `scale(${scale}) rotate(${rotation}deg)`,
              transformOrigin: 'top left',
              width: `${100 / scale}%`,
              height: `${100 / scale}%`
            }}
          >
            <iframe
              ref={iframeRef}
              src={`${pdfUrl}#toolbar=1&navpanes=1&scrollbar=1`}
              className="w-full h-full border-0"
              onLoad={handleIframeLoad}
              onError={handleIframeError}
              title={title}
            />
          </div>
        </div>

        {/* Footer */}
        <div className="p-3 border-t bg-gray-50 text-center">
          <p className="text-sm text-gray-600">
            Use the controls above to zoom, rotate, or download the PDF
          </p>
        </div>
      </div>
    </div>
  );
};

export default PdfViewer;
