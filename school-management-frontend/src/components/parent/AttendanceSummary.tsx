import React, { useState, useEffect } from 'react';
import { Card, Button } from '../ui';
import { 
  Calendar, 
  TrendingUp, 
  TrendingDown, 
  CheckCircle, 
  XCircle, 
  Clock,
  BarChart3,
  ChevronDown,
  ChevronUp
} from 'lucide-react';
import { parentService, type ParentAttendanceSummary, type MonthlyAttendance, type RecentAttendance } from '../../services/parentService';

interface AttendanceSummaryProps {
  studentId: number;
  studentName: string;
  className?: string;
}

const AttendanceSummary: React.FC<AttendanceSummaryProps> = ({ 
  studentId, 
  studentName,
  className = ""
}) => {
  const [attendanceData, setAttendanceData] = useState<ParentAttendanceSummary | null>(null);
  const [monthlyData, setMonthlyData] = useState<MonthlyAttendance[]>([]);
  const [recentData, setRecentData] = useState<RecentAttendance[]>([]);
  const [loading, setLoading] = useState(true);
  const [expanded, setExpanded] = useState(false);
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear());

  useEffect(() => {
    loadAttendanceData();
  }, [studentId, selectedYear]);

  const loadAttendanceData = async () => {
    try {
      setLoading(true);
      const [summary, monthly, recent] = await Promise.all([
        parentService.getStudentAttendanceSummary(studentId),
        parentService.getMonthlyAttendance(studentId, selectedYear),
        parentService.getRecentAttendance(studentId, 30)
      ]);

      setAttendanceData(summary);
      setMonthlyData(monthly);
      setRecentData(recent);
    } catch (error) {
      console.error('Error loading attendance data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getAttendanceColor = (percentage: number) => {
    if (percentage >= 90) return 'text-green-600';
    if (percentage >= 80) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getAttendanceBgColor = (percentage: number) => {
    if (percentage >= 90) return 'bg-green-100';
    if (percentage >= 80) return 'bg-yellow-100';
    return 'bg-red-100';
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric'
    });
  };

  const getMonthName = (month: string) => {
    const monthNames = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return monthNames[parseInt(month) - 1] || month;
  };

  if (loading) {
    return (
      <Card className={`p-6 ${className}`}>
        <div className="animate-pulse">
          <div className="h-4 bg-gray-200 rounded w-1/4 mb-4"></div>
          <div className="h-8 bg-gray-200 rounded w-1/2 mb-4"></div>
          <div className="h-4 bg-gray-200 rounded w-3/4"></div>
        </div>
      </Card>
    );
  }

  if (!attendanceData) {
    return (
      <Card className={`p-6 ${className}`}>
        <div className="text-center text-gray-500">
          <Calendar className="h-12 w-12 mx-auto mb-4 text-gray-300" />
          <p>No attendance data available</p>
        </div>
      </Card>
    );
  }

  return (
    <Card className={`p-6 ${className}`}>
      <div className="flex items-center justify-between mb-6">
        <div className="flex items-center">
          <Calendar className="h-6 w-6 text-blue-600 mr-3" />
          <h3 className="text-lg font-semibold text-gray-900">Attendance Summary</h3>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => setExpanded(!expanded)}
        >
          {expanded ? (
            <>
              <ChevronUp className="h-4 w-4 mr-1" />
              Show Less
            </>
          ) : (
            <>
              <ChevronDown className="h-4 w-4 mr-1" />
              Show Details
            </>
          )}
        </Button>
      </div>

      {/* Overall Statistics */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
        <div className="text-center">
          <div className={`inline-flex items-center justify-center w-12 h-12 rounded-full ${getAttendanceBgColor(attendanceData.attendancePercentage)} mb-2`}>
            <BarChart3 className={`h-6 w-6 ${getAttendanceColor(attendanceData.attendancePercentage)}`} />
          </div>
          <p className="text-sm text-gray-600">Overall</p>
          <p className={`text-lg font-bold ${getAttendanceColor(attendanceData.attendancePercentage)}`}>
            {attendanceData.attendancePercentage.toFixed(1)}%
          </p>
        </div>

        <div className="text-center">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-blue-100 mb-2">
            <CheckCircle className="h-6 w-6 text-blue-600" />
          </div>
          <p className="text-sm text-gray-600">Present</p>
          <p className="text-lg font-bold text-gray-900">{attendanceData.presentDays}</p>
        </div>

        <div className="text-center">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-red-100 mb-2">
            <XCircle className="h-6 w-6 text-red-600" />
          </div>
          <p className="text-sm text-gray-600">Absent</p>
          <p className="text-lg font-bold text-gray-900">{attendanceData.absentDays}</p>
        </div>

        <div className="text-center">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-gray-100 mb-2">
            <Clock className="h-6 w-6 text-gray-600" />
          </div>
          <p className="text-sm text-gray-600">Total Days</p>
          <p className="text-lg font-bold text-gray-900">{attendanceData.totalDays}</p>
        </div>
      </div>

      {/* Recent Attendance */}
      <div className="mb-6">
        <h4 className="text-md font-medium text-gray-900 mb-3">Recent Attendance (Last 30 Days)</h4>
        <div className="grid grid-cols-7 gap-2">
          {recentData.slice(0, 14).map((record, index) => (
            <div
              key={index}
              className={`text-center p-2 rounded-lg text-xs ${
                record.present 
                  ? 'bg-green-100 text-green-800' 
                  : 'bg-red-100 text-red-800'
              }`}
              title={`${formatDate(record.date)} - ${record.present ? 'Present' : 'Absent'}`}
            >
              <div className="font-medium">
                {new Date(record.date).getDate()}
              </div>
              <div className="text-xs">
                {record.present ? '✓' : '✗'}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Expanded Details */}
      {expanded && (
        <div className="space-y-6">
          {/* Monthly Breakdown */}
          <div>
            <div className="flex items-center justify-between mb-4">
              <h4 className="text-md font-medium text-gray-900">Monthly Breakdown ({selectedYear})</h4>
              <select
                value={selectedYear}
                onChange={(e) => setSelectedYear(parseInt(e.target.value))}
                className="text-sm border border-gray-300 rounded-md px-3 py-1"
              >
                {[2023, 2024, 2025].map(year => (
                  <option key={year} value={year}>{year}</option>
                ))}
              </select>
            </div>
            
            <div className="space-y-3">
              {monthlyData.map((month, index) => (
                <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                  <div className="flex items-center">
                    <div className="w-20 text-sm font-medium text-gray-900">
                      {getMonthName(month.month)}
                    </div>
                    <div className="flex items-center space-x-4 text-sm text-gray-600">
                      <span>{month.presentDays} present</span>
                      <span>{month.absentDays} absent</span>
                      <span>{month.totalDays} total</span>
                    </div>
                  </div>
                  <div className="flex items-center">
                    <div className={`px-3 py-1 rounded-full text-sm font-medium ${getAttendanceBgColor(month.percentage)} ${getAttendanceColor(month.percentage)}`}>
                      {month.percentage.toFixed(1)}%
                    </div>
                    <div className="w-20 ml-3">
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div
                          className={`h-2 rounded-full ${
                            month.percentage >= 90 ? 'bg-green-500' :
                            month.percentage >= 80 ? 'bg-yellow-500' : 'bg-red-500'
                          }`}
                          style={{ width: `${Math.min(month.percentage, 100)}%` }}
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Attendance Trend */}
          <div>
            <h4 className="text-md font-medium text-gray-900 mb-3">Attendance Trend</h4>
            <div className="p-4 bg-gray-50 rounded-lg">
              <div className="flex items-center justify-between text-sm">
                <div className="flex items-center">
                  {attendanceData.attendancePercentage >= 90 ? (
                    <TrendingUp className="h-4 w-4 text-green-600 mr-1" />
                  ) : (
                    <TrendingDown className="h-4 w-4 text-red-600 mr-1" />
                  )}
                  <span className="text-gray-600">
                    {attendanceData.attendancePercentage >= 90 
                      ? 'Excellent attendance record' 
                      : attendanceData.attendancePercentage >= 80
                      ? 'Good attendance record'
                      : 'Attendance needs improvement'
                    }
                  </span>
                </div>
                <span className={`font-medium ${getAttendanceColor(attendanceData.attendancePercentage)}`}>
                  {attendanceData.attendancePercentage.toFixed(1)}%
                </span>
              </div>
            </div>
          </div>
        </div>
      )}
    </Card>
  );
};

export default AttendanceSummary;
