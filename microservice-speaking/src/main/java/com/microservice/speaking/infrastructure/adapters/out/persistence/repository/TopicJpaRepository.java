package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicJpaRepository extends JpaRepository<TopicEntity,Long>{
}
