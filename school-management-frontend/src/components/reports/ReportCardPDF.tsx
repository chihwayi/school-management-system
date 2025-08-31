import React from 'react';
import { Document, Page, Text, View, StyleSheet, Font } from '@react-pdf/renderer';
import type { Report, SubjectReport } from '../../types';

// Register fonts (you can add custom fonts here)
Font.register({
  family: 'Helvetica',
  fonts: [
    { src: 'https://fonts.gstatic.com/s/helveticaneue/v70/1Ptsg8zYS_SKggPNyC0IT4ttDfA.ttf', fontWeight: 'normal' },
    { src: 'https://fonts.gstatic.com/s/helveticaneue/v70/1Ptsg8zYS_SKggPNyC0IT4ttDfB.ttf', fontWeight: 'bold' }
  ]
});

// Create styles
const styles = StyleSheet.create({
  page: {
    flexDirection: 'column',
    backgroundColor: '#ffffff',
    padding: 30,
    fontFamily: 'Helvetica'
  },
  header: {
    marginBottom: 20,
    textAlign: 'center'
  },
  schoolName: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 5,
    color: '#1f2937'
  },
  reportTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#374151'
  },
  studentInfo: {
    marginBottom: 20,
    padding: 15,
    backgroundColor: '#f9fafb',
    borderRadius: 5
  },
  studentInfoRow: {
    flexDirection: 'row',
    marginBottom: 5
  },
  label: {
    fontWeight: 'bold',
    width: 120,
    color: '#374151'
  },
  value: {
    color: '#1f2937'
  },
  table: {
    marginTop: 20
  },
  tableHeader: {
    flexDirection: 'row',
    backgroundColor: '#e5e7eb',
    padding: 8,
    fontWeight: 'bold',
    fontSize: 12
  },
  tableRow: {
    flexDirection: 'row',
    padding: 8,
    borderBottomWidth: 1,
    borderBottomColor: '#d1d5db',
    fontSize: 10
  },
  subjectCol: {
    width: '30%'
  },
  gradeCol: {
    width: '15%',
    textAlign: 'center'
  },
  commentCol: {
    width: '55%'
  },
  footer: {
    marginTop: 30,
    paddingTop: 15,
    borderTopWidth: 1,
    borderTopColor: '#d1d5db'
  },
  signature: {
    marginTop: 20,
    flexDirection: 'row',
    justifyContent: 'space-between'
  },
  signatureBox: {
    width: '45%',
    textAlign: 'center'
  },
  signatureLine: {
    borderBottomWidth: 1,
    borderBottomColor: '#000',
    marginBottom: 5,
    height: 20
  }
});

interface ReportCardPDFProps {
  report: Report;
  schoolSettings: any;
}

const ReportCardPDF: React.FC<ReportCardPDFProps> = ({ report, schoolSettings }) => {
  const schoolName = schoolSettings?.schoolName || 'School Management System';
  const currentDate = new Date().toLocaleDateString();

  return (
    <Document>
      <Page size="A4" style={styles.page}>
        {/* Header */}
        <View style={styles.header}>
          <Text style={styles.schoolName}>{schoolName}</Text>
          <Text style={styles.reportTitle}>STUDENT REPORT CARD</Text>
        </View>

        {/* Student Information */}
        <View style={styles.studentInfo}>
          <View style={styles.studentInfoRow}>
            <Text style={styles.label}>Student Name:</Text>
            <Text style={styles.value}>{report.student?.firstName} {report.student?.lastName}</Text>
          </View>
          <View style={styles.studentInfoRow}>
            <Text style={styles.label}>Student ID:</Text>
            <Text style={styles.value}>{report.student?.studentId}</Text>
          </View>
          <View style={styles.studentInfoRow}>
            <Text style={styles.label}>Class:</Text>
            <Text style={styles.value}>{report.student?.form} {report.student?.section}</Text>
          </View>
          <View style={styles.studentInfoRow}>
            <Text style={styles.label}>Term:</Text>
            <Text style={styles.value}>{report.term}</Text>
          </View>
          <View style={styles.studentInfoRow}>
            <Text style={styles.label}>Academic Year:</Text>
            <Text style={styles.value}>{report.academicYear}</Text>
          </View>
        </View>

        {/* Subject Grades Table */}
        <View style={styles.table}>
          <View style={styles.tableHeader}>
            <Text style={styles.subjectCol}>Subject</Text>
            <Text style={styles.gradeCol}>Grade</Text>
            <Text style={styles.commentCol}>Comment</Text>
          </View>
          
          {report.subjectReports?.map((subjectReport: SubjectReport, index: number) => (
            <View key={index} style={styles.tableRow}>
              <Text style={styles.subjectCol}>{subjectReport.subject?.name}</Text>
              <Text style={styles.gradeCol}>{subjectReport.grade}</Text>
              <Text style={styles.commentCol}>{subjectReport.comment || 'No comment'}</Text>
            </View>
          ))}
        </View>

        {/* Overall Comment */}
        {report.overallComment && (
          <View style={{ marginTop: 20 }}>
            <Text style={{ fontWeight: 'bold', marginBottom: 5 }}>Overall Comment:</Text>
            <Text>{report.overallComment}</Text>
          </View>
        )}

        {/* Footer with Signatures */}
        <View style={styles.footer}>
          <Text style={{ marginBottom: 10 }}>Date: {currentDate}</Text>
          
          <View style={styles.signature}>
            <View style={styles.signatureBox}>
              <View style={styles.signatureLine} />
              <Text>Class Teacher</Text>
            </View>
            <View style={styles.signatureBox}>
              <View style={styles.signatureLine} />
              <Text>Principal</Text>
            </View>
          </View>
        </View>
      </Page>
    </Document>
  );
};

export default ReportCardPDF;