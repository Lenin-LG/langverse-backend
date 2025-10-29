package com.microservice.notes.infrastructure.adapters.out.persistence.repository;

import com.microservice.notes.infrastructure.adapters.out.persistence.entity.NoteBookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteBookJpaRepository extends JpaRepository<NoteBookEntity,Long> {
    List<NoteBookEntity> findByUserId(String userId);
}
