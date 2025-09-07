import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Button, Select } from '../../components/ui';
import { 
  ArrowLeft, 
  Calendar, 
  TrendingUp, 
  TrendingDown,
  CheckCircle,
  XCircle,
  Clock,
  BarChart3
} from 'lucide-react';
import { parentService } from '../../services/parentService';
import { toast } from 'react-hot-toast';

interface AttendanceRecord {
  id: number;
  date: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
  subject?: string;
  remarks?: string;
}

interface AttendanceSummary {
  studentId: number;
  studentName: string;
  totalDays: number;
  presentDays: number;
  absentDays: number;
  attendancePercentage: number;
  monthlyBreakdown: any[];
  recentAttendance: any[];
}

const ParentAttendancePage: React.FC = () => {
  const { childId } = useParams<{ childId: string }>();
  const navigate = useNavigate();
  const [attendanceRecords, setAttendanceRecords] = useState<AttendanceRecord[]>([]);
  const [attendanceSummary, setAttendanceSummary] = useState<AttendanceSummary | null>(null);
  const [loading, setLoading] = useState(true);
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  useEffect(() => {
    if (childId) {
      loadAttendanceData();
    }
  }, [childId, selectedMonth, selectedYear]);

  const loadAttendanceData = async () => {
    try {
      setLoading(true);
      const [records, summary] = await Promise.all([
        parentService.getStudentAttendance(parseInt(childId!)),
        parentService.getStudentAttendanceSummary(parseInt(childId!))
      ]);
      
      // Filter records by selected month and year
      const filteredRecords = records.filter(record => {
        const recordDate = new Date(record.date);
        return recordDate.getMonth() + 1 === selectedMonth && 
               recordDate.getFullYear() === selectedYear;
      });
      
      setAttendanceRecords(filteredRecords);
      setAttendanceSummary(summary);
    } catch (error) {
      console.error('Error loading attendance data:', error);
      toast.error('Failed to load attendance data');
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PRESENT':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      case 'ABSENT':
        return <XCircle className="h-4 w-4 text-red-600" />;
      case 'LATE':
        return <Clock className="h-4 w-4 text-yellow-600" />;
      case 'EXCUSED':
        return <CheckCircle className="h-4 w-4 text-blue-600" />;
      default:
        return <Clock className="h-4 w-4 text-gray-600" />;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PRESENT':
        return 'bg-green-100 text-green-800';
      case 'ABSENT':
        return 'bg-red-100 text-red-800';
      case 'LATE':
        return 'bg-yellow-100 text-yellow-800';
      case 'EXCUSED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const months = [
    { value: 1, label: 'January' },
    { value: 2, label: 'February' },
    { value: 3, label: 'March' },
    { value: 4, label: 'April' },
    { value: 5, label: 'May' },
    { value: 6, label: 'June' },
    { value: 7, label: 'July' },
    { value: 8, label: 'August' },
    { value: 9, label: 'September' },
    { value: 10, label: 'October' },
    { value: 11, label: 'November' },
    { value: 12, label: 'December' }
  ];

  const years = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading attendance data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header */}
        <div className="mb-8">
          <Button
            variant="ghost"
            onClick={() => navigate('/parent/dashboard')}
            className="mb-4"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Back to Dashboard
          </Button>
          <h1 className="text-3xl font-bold text-gray-900">Attendance Details</h1>
          <p className="text-gray-600 mt-2">View your child's attendance records and statistics</p>
        </div>

        {/* Filters */}
        <Card className="p-6 mb-6">
          <div className="flex items-center space-x-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">Month</label>
              <Select
                value={selectedMonth}
                onChange={(e) => setSelectedMonth(parseInt(e.target.value))}
                options={months}
              />
            </div>
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-2">Year</label>
              <Select
                value={selectedYear}
                onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                options={years.map(year => ({ value: year, label: year.toString() }))}
              />
            </div>
          </div>
        </Card>

        {/* Attendance Summary */}
        {attendanceSummary && (
          <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
            <Card className="p-6">
              <div className="flex items-center">
                <div className="p-2 bg-green-100 rounded-lg">
                  <CheckCircle className="h-6 w-6 text-green-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Present Days</p>
                  <p className="text-2xl font-bold text-green-600">{attendanceSummary.presentDays}</p>
                </div>
              </div>
            </Card>

            <Card className="p-6">
              <div className="flex items-center">
                <div className="p-2 bg-red-100 rounded-lg">
                  <XCircle className="h-6 w-6 text-red-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Absent Days</p>
                  <p className="text-2xl font-bold text-red-600">{attendanceSummary.absentDays}</p>
                </div>
              </div>
            </Card>

            <Card className="p-6">
              <div className="flex items-center">
                <div className="p-2 bg-yellow-100 rounded-lg">
                  <Clock className="h-6 w-6 text-yellow-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Late Days</p>
                  <p className="text-2xl font-bold text-yellow-600">0</p>
                </div>
              </div>
            </Card>

            <Card className="p-6">
              <div className="flex items-center">
                <div className="p-2 bg-blue-100 rounded-lg">
                  <BarChart3 className="h-6 w-6 text-blue-600" />
                </div>
                <div className="ml-4">
                  <p className="text-sm font-medium text-gray-600">Attendance Rate</p>
                  <p className="text-2xl font-bold text-blue-600">{attendanceSummary.attendancePercentage?.toFixed(1) || '0.0'}%</p>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Attendance Records */}
        <Card className="p-6">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold text-gray-900">Attendance Records</h2>
            <div className="text-sm text-gray-600">
              {attendanceRecords.length} records for {months[selectedMonth - 1]?.label} {selectedYear}
            </div>
          </div>

          {attendanceRecords.length === 0 ? (
            <div className="text-center py-8">
              <Calendar className="h-12 w-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No attendance records found for the selected period</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Date
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Status
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Subject
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Remarks
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {attendanceRecords.map((record) => (
                    <tr key={record.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {new Date(record.date).toLocaleDateString()}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center">
                          {getStatusIcon(record.status)}
                          <span className={`ml-2 px-2 py-1 text-xs font-medium rounded-full ${getStatusColor(record.status)}`}>
                            {record.status}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {record.subject || 'N/A'}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-900">
                        {record.remarks || 'No remarks'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default ParentAttendancePage;
