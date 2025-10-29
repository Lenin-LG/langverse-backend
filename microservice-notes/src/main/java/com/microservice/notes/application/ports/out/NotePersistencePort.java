package com.microservice.notes.application.ports.out;

import com.microservice.notes.domain.model.Note;

import java.util.List;
import java.util.Optional;

public interface NotePersistencePort {

    Note save(Note note);
    void deleteById(Long id);
    Optional<Note> findById(Long id);
    List<Note> findByNotebookId(Long notebookId);
    List<Note> findByUserId(String userId);
    List<Note> searchNotes(String keyword, String userId);
}
