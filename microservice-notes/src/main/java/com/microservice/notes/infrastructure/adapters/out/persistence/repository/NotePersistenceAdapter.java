package com.microservice.notes.infrastructure.adapters.out.persistence.repository;

import com.microservice.notes.application.ports.out.NotePersistencePort;
import com.microservice.notes.domain.model.Note;
import com.microservice.notes.infrastructure.adapters.out.persistence.mapper.NoteEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotePersistenceAdapter implements NotePersistencePort {
    private final NoteJpaRepository noteJpaRepository;
    private final NoteEntityMapper noteEntityMapper;

    @Override
    public Note save(Note note) {
        return noteEntityMapper.toDomain(
                noteJpaRepository.save(noteEntityMapper.toEntity(note))
        );
    }

    @Override
    public void deleteById(Long id) {
        noteJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Note> findById(Long id) {

        return noteJpaRepository.findById(id)
                .map(noteEntityMapper::toDomain);
    }

    @Override
    public List<Note> findByNotebookId(Long notebookId) {
        return noteJpaRepository.findByNotebookId(notebookId).stream()
                .map(noteEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Note> findByUserId(String userId) {
        return noteJpaRepository.findByUserId(userId).stream()
                .map(noteEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Note> searchNotes(String keyword, String userId) {
        return noteJpaRepository.findByTitleContainingIgnoreCaseAndUserId(keyword, userId).stream()
                .map(noteEntityMapper::toDomain)
                .toList();
    }
}
