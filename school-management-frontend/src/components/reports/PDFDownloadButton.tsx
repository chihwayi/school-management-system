import React from 'react';
import { PDFDownloadLink } from '@react-pdf/renderer';
import Button from '../ui/Button';
import { Download } from 'lucide-react';
import ReportCardPDF from './ReportCardPDF';
import PDFErrorBoundary from './PDFErrorBoundary';
import type { Report } from '../../types';

interface PDFDownloadButtonProps {
  report: Report;
  schoolSettings: any;
  disabled?: boolean;
}

const PDFDownloadButton: React.FC<PDFDownloadButtonProps> = ({ 
  report, 
  schoolSettings, 
  disabled = false 
}) => {
  return (
    <PDFErrorBoundary>
      <PDFDownloadLink
        document={<ReportCardPDF report={report} schoolSettings={schoolSettings} />}
        fileName={`Report_${report.studentName}_${report.term}_${report.academicYear}.pdf`}
      >
        {({ loading }) => (
          <Button
            size="sm"
            variant="outline"
            disabled={disabled || loading}
            className="text-blue-600 hover:text-blue-700"
          >
            <Download className="h-3 w-3 mr-1" />
            {loading ? 'Generating...' : 'Download PDF'}
          </Button>
        )}
      </PDFDownloadLink>
    </PDFErrorBoundary>
  );
};

export default PDFDownloadButton;
