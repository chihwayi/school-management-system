package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.AiGeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiGeneratedContentRepository extends JpaRepository<AiGeneratedContent, Long> {
    
    List<AiGeneratedContent> findByTeacherId(Long teacherId);
    
    List<AiGeneratedContent> findBySubjectId(Long subjectId);
    
    List<AiGeneratedContent> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    
    List<AiGeneratedContent> findByTeacherIdAndType(Long teacherId, AiGeneratedContent.ContentType type);
    
    List<AiGeneratedContent> findBySubjectIdAndType(Long subjectId, AiGeneratedContent.ContentType type);
    
    List<AiGeneratedContent> findByTeacherIdAndAcademicYear(Long teacherId, String academicYear);
    
    List<AiGeneratedContent> findByTeacherIdAndFormLevel(Long teacherId, String formLevel);
    
    List<AiGeneratedContent> findByPublished(boolean published);
    
    List<AiGeneratedContent> findByTeacherIdAndPublished(Long teacherId, boolean published);
    
    List<AiGeneratedContent> findBySubjectIdAndPublished(Long subjectId, boolean published);
    
    @Query("SELECT agc FROM AiGeneratedContent agc WHERE agc.teacher.id = :teacherId AND agc.topicFocus LIKE %:topic%")
    List<AiGeneratedContent> findByTeacherIdAndTopicContaining(@Param("teacherId") Long teacherId, @Param("topic") String topic);
    
    @Query("SELECT agc FROM AiGeneratedContent agc WHERE agc.subject.id = :subjectId AND agc.difficultyLevel = :difficulty")
    List<AiGeneratedContent> findBySubjectIdAndDifficultyLevel(@Param("subjectId") Long subjectId, @Param("difficulty") AiGeneratedContent.DifficultyLevel difficulty);
    
    @Query("SELECT agc FROM AiGeneratedContent agc WHERE agc.teacher.id = :teacherId ORDER BY agc.usageCount DESC")
    List<AiGeneratedContent> findTopUsedByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT agc.type, COUNT(agc) FROM AiGeneratedContent agc WHERE agc.teacher.id = :teacherId GROUP BY agc.type")
    List<Object[]> getContentTypeCountsByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT agc FROM AiGeneratedContent agc WHERE agc.teacher.id = :teacherId AND agc.createdAt >= :startDate")
    List<AiGeneratedContent> findByTeacherIdAndCreatedAfter(@Param("teacherId") Long teacherId, @Param("startDate") java.time.LocalDateTime startDate);
}
