package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.SpeakingPracticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpeakingPracticeJpaRepository extends JpaRepository<SpeakingPracticeEntity,Long> {
    List<SpeakingPracticeEntity> findByUserId(String userId);

}
