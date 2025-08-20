import React from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { Card, Button, Badge } from '../../components/ui';
import { reportService } from '../../services/reportService';
import { schoolSettingsService } from '../../services/schoolSettingsService';
import { ArrowLeft, Download } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { LoadingSpinner } from '../../components/common';
import { useState, useEffect } from 'react';

const ReportDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [report, setReport] = useState<any>(null);
  const [schoolSettings, setSchoolSettings] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  
  // Get the referrer page from location state, default to /app/reports
  const fromPage = location.state?.from || '/app/reports';
  
  // Helper function to get back button text
  const getBackButtonText = () => {
    if (fromPage.includes('/app/classes/')) return 'Back to Class';
    if (fromPage.includes('/app/students/')) return 'Back to Student';
    return 'Back to Reports';
  };
  
  useEffect(() => {
    const loadData = async () => {
      if (!id) return;
      
      try {
        setLoading(true);
        
        const reportData = await reportService.getReportById(parseInt(id));
        setReport(reportData);
        
        const settings = await schoolSettingsService.getSchoolSettings();
        setSchoolSettings(settings);
      } catch (error) {
        console.error('Error loading report:', error);
        toast.error('Failed to load report');
      } finally {
        setLoading(false);
      }
    };
    
    loadData();
  }, [id]);
  
  if (loading) {
    return <LoadingSpinner />;
  }
  
  if (!report) {
    return (
      <div className="p-6">
        <h1 className="text-2xl font-bold mb-4">Report Not Found</h1>
        <Button onClick={() => navigate(fromPage)}>
          <ArrowLeft className="h-4 w-4 mr-2" />
          {getBackButtonText()}
        </Button>
      </div>
    );
  }
  
  return (
    <div className="space-y-6">
      <div className="flex items-center gap-4 mb-6">
        <Button
          variant="outline"
          onClick={() => navigate(fromPage)}
        >
          <ArrowLeft className="w-4 h-4 mr-2" />
          {getBackButtonText()}
        </Button>
        <h1 className="text-2xl font-bold">Report Details</h1>
      </div>
      
      <Card className="p-6">
        <div className="flex justify-between items-start mb-4">
          <div>
            <h2 className="text-xl font-semibold mb-2">
              {report.studentName}
            </h2>
            <p className="text-gray-600">
              {report.term} - {report.academicYear}
            </p>
            <p className="text-gray-600">
              Class: {report.form} {report.section}
            </p>
          </div>
          <div>
            <Badge
              variant={report.finalized ? 'success' : 'warning'}
            >
              {report.finalized ? 'Finalized' : 'In Progress'}
            </Badge>
          </div>
        </div>
        
        <div className="mt-6">
          <Button
            onClick={() => toast.success('PDF generation will be available when @react-pdf/renderer is installed')}
          >
            <Download className="h-4 w-4 mr-2" />
            Download Report
          </Button>
        </div>
      </Card>
      
      {/* Subject Reports Table */}
      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Subject Reports</h2>
        <div className="overflow-x-auto">
          <div className="min-w-[1000px]">
            <table className="min-w-full divide-y divide-gray-200">
                        <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Subject</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Coursework</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Exam</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Total</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Coursework Grade</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Exam Grade</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Teacher Comment</th>
                </tr>
              </thead>
                      <tbody className="bg-white divide-y divide-gray-200">
              {report.subjectReports?.map((subjectReport) => (
                <tr key={subjectReport.id}>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.subjectName}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.courseworkMark || 'N/A'}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.examMark || 'N/A'}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.finalMark || 'N/A'}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.courseworkGrade || 'N/A'}</td>
                  <td className="px-6 py-4 whitespace-nowrap">{subjectReport.examGrade || 'N/A'}</td>
                  <td className="px-6 py-4">{subjectReport.comment || 'No comment'}</td>
                </tr>
                            ))}
            </tbody>
          </table>
          </div>
        </div>
      </Card>
      
      {/* Class Teacher Comment */}
      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-2">Class Teacher's Comment</h2>
        <p className="text-gray-700">{report.overallComment || 'No comment provided'}</p>
      </Card>
      
      {/* Principal Comment */}
      {report.principalComment && (
        <Card className="p-6">
          <h2 className="text-lg font-semibold mb-2">Principal's Comment</h2>
          <p className="text-gray-700">{report.principalComment}</p>
        </Card>
      )}
    </div>
  );
};

export default ReportDetailPage;