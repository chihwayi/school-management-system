package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {
    
    List<AiUsageLog> findByTeacherId(Long teacherId);
    
    List<AiUsageLog> findByTeacherIdAndOperation(Long teacherId, String operation);
    
    List<AiUsageLog> findByTeacherIdAndSuccess(Long teacherId, boolean success);
    
    List<AiUsageLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<AiUsageLog> findByTeacherIdAndCreatedAtBetween(Long teacherId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT aul.operation, COUNT(aul) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId GROUP BY aul.operation")
    List<Object[]> getOperationCountsByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT SUM(aul.tokensUsed) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId AND aul.createdAt >= :startDate")
    Long getTotalTokensUsedByTeacherSince(@Param("teacherId") Long teacherId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT SUM(aul.costInCents) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId AND aul.createdAt >= :startDate")
    Long getTotalCostByTeacherSince(@Param("teacherId") Long teacherId, @Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT AVG(aul.processingTimeMs) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId AND aul.success = true")
    Double getAverageProcessingTimeByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT aul.aiModelUsed, COUNT(aul) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId GROUP BY aul.aiModelUsed")
    List<Object[]> getModelUsageByTeacher(@Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(aul) FROM AiUsageLog aul WHERE aul.teacher.id = :teacherId AND aul.success = false")
    Long getFailedOperationsCountByTeacher(@Param("teacherId") Long teacherId);
}
