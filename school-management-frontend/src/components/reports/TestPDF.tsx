import React, { useState } from 'react';
import { PDFDownloadLink } from '@react-pdf/renderer';
import { Document, Page, Text, View, StyleSheet } from '@react-pdf/renderer';
import Button from '../ui/Button';
import PDFErrorBoundary from './PDFErrorBoundary';

// Create styles
const styles = StyleSheet.create({
  page: {
    flexDirection: 'column',
    backgroundColor: '#ffffff',
    padding: 30,
  },
  title: {
    fontSize: 24,
    textAlign: 'center',
    marginBottom: 20,
  },
  text: {
    fontSize: 12,
    marginBottom: 10,
  },
  header: {
    fontSize: 16,
    fontWeight: 'bold',
    marginBottom: 15,
    color: '#1f2937',
  },
  section: {
    marginBottom: 20,
  },
});

// Create Document Component
const TestDocument = () => (
  <Document>
    <Page size="A4" style={styles.page}>
      <Text style={styles.title}>School Management System</Text>
      <Text style={styles.header}>PDF Generation Test Report</Text>
      
      <View style={styles.section}>
        <Text style={styles.text}>✅ PDF generation is working correctly!</Text>
        <Text style={styles.text}>✅ @react-pdf/renderer is properly configured</Text>
        <Text style={styles.text}>✅ Document styling is functional</Text>
        <Text style={styles.text}>✅ File download is operational</Text>
      </View>
      
      <View style={styles.section}>
        <Text style={styles.header}>System Information:</Text>
        <Text style={styles.text}>Generated on: {new Date().toLocaleDateString()}</Text>
        <Text style={styles.text}>Time: {new Date().toLocaleTimeString()}</Text>
        <Text style={styles.text}>Browser: {navigator.userAgent.split(' ')[0]}</Text>
      </View>
      
      <View style={styles.section}>
        <Text style={styles.header}>Features Available:</Text>
        <Text style={styles.text}>• Student Report Cards</Text>
        <Text style={styles.text}>• Academic Transcripts</Text>
        <Text style={styles.text}>• Attendance Reports</Text>
        <Text style={styles.text}>• Performance Analytics</Text>
      </View>
    </Page>
  </Document>
);

const TestPDF: React.FC = () => {
  const [error, setError] = useState<string | null>(null);

  const handlePDFGeneration = () => {
    try {
      setError(null);
      // The PDFDownloadLink will handle the generation
    } catch (err) {
      setError('PDF generation failed. Please try again.');
      console.error('PDF generation error:', err);
    }
  };

  return (
    <div className="p-4">
      <h2 className="text-lg font-semibold mb-4">PDF Generation Test</h2>
      
      {error && (
        <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
          {error}
        </div>
      )}
      
      <div className="mb-4">
        <p className="text-sm text-gray-600 mb-2">
          Click the button below to test PDF generation functionality:
        </p>
      </div>
      
      <PDFErrorBoundary>
        <PDFDownloadLink
          document={<TestDocument />}
          fileName={`test-document-${new Date().toISOString().split('T')[0]}.pdf`}
          onClick={handlePDFGeneration}
        >
          {({ loading, error: pdfError }) => (
            <Button
              variant="outline"
              disabled={loading}
              className="bg-blue-600 hover:bg-blue-700 text-white"
              onClick={() => {
                if (pdfError) {
                  setError('PDF generation failed: ' + pdfError.message);
                }
              }}
            >
              {loading ? 'Generating PDF...' : 'Download Test PDF'}
            </Button>
          )}
        </PDFDownloadLink>
      </PDFErrorBoundary>
      
      <div className="mt-4 text-xs text-gray-500">
        <p>Note: The warnings about MouseEvent.mozInputSource and source maps are development-only and don't affect functionality.</p>
      </div>
    </div>
  );
};

export default TestPDF;
