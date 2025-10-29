package com.microservice.notes.infrastructure.adapters.out.persistence.repository;

import com.microservice.notes.infrastructure.adapters.out.persistence.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteJpaRepository extends JpaRepository<NoteEntity,Long> {
    List<NoteEntity> findByNotebookId(Long notebookId);
    List<NoteEntity> findByUserId(String userId);
    List<NoteEntity> findByTitleContainingIgnoreCaseAndUserId(String keyword, String userId);

}
