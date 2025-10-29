package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.PhraseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhraseJpaRepository extends JpaRepository<PhraseEntity,Long> {
    List<PhraseEntity> findByTopic_TopicId(Long id);

}
