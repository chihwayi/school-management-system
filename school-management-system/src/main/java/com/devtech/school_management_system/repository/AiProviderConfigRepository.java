package com.devtech.school_management_system.repository;

import com.devtech.school_management_system.entity.AiProviderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiProviderConfigRepository extends JpaRepository<AiProviderConfig, Long> {

    Optional<AiProviderConfig> findByProviderName(String providerName);

    List<AiProviderConfig> findByIsEnabledTrue();

    @Query("SELECT apc FROM AiProviderConfig apc WHERE apc.isEnabled = true AND apc.apiKey IS NOT NULL AND apc.apiKey != ''")
    List<AiProviderConfig> findConfiguredProviders();

    @Query("SELECT apc FROM AiProviderConfig apc WHERE apc.providerName = :providerName AND apc.isEnabled = true AND apc.apiKey IS NOT NULL AND apc.apiKey != ''")
    Optional<AiProviderConfig> findConfiguredProvider(@Param("providerName") String providerName);

    boolean existsByProviderName(String providerName);
}
