package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.AiResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiResourceRepository extends JpaRepository<AiResource, Long> {
    
    List<AiResource> findByTeacherId(Long teacherId);
    
    List<AiResource> findBySubjectId(Long subjectId);
    
    List<AiResource> findByTeacherIdAndSubjectId(Long teacherId, Long subjectId);
    
    List<AiResource> findByTeacherIdAndType(Long teacherId, AiResource.ResourceType type);
    
    List<AiResource> findBySubjectIdAndType(Long subjectId, AiResource.ResourceType type);
    
    List<AiResource> findByTeacherIdAndAcademicYear(Long teacherId, String academicYear);
    
    List<AiResource> findByTeacherIdAndFormLevel(Long teacherId, String formLevel);
    
    List<AiResource> findByProcessingStatus(AiResource.ProcessingStatus status);
    
    @Query("SELECT ar FROM AiResource ar WHERE ar.teacher.id = :teacherId AND ar.processed = true")
    List<AiResource> findProcessedResourcesByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT ar FROM AiResource ar WHERE ar.subject.id = :subjectId AND ar.processed = true")
    List<AiResource> findProcessedResourcesBySubject(@Param("subjectId") Long subjectId);
    
    @Query("SELECT ar FROM AiResource ar WHERE ar.teacher.id = :teacherId AND ar.subject.id = :subjectId AND ar.processed = true")
    List<AiResource> findProcessedResourcesByTeacherAndSubject(@Param("teacherId") Long teacherId, @Param("subjectId") Long subjectId);
    
    @Query("SELECT COUNT(ar) FROM AiResource ar WHERE ar.teacher.id = :teacherId AND ar.type = :type")
    Long countByTeacherAndType(@Param("teacherId") Long teacherId, @Param("type") AiResource.ResourceType type);
    
    @Query("SELECT ar.type, COUNT(ar) FROM AiResource ar WHERE ar.teacher.id = :teacherId GROUP BY ar.type")
    List<Object[]> getResourceTypeCountsByTeacher(@Param("teacherId") Long teacherId);
}
