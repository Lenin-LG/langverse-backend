package com.microservice.notes.infrastructure.adapters.out.persistence.repository;

import com.microservice.notes.application.ports.out.NoteBookPersistencePort;
import com.microservice.notes.domain.model.NoteBook;
import com.microservice.notes.infrastructure.adapters.out.persistence.mapper.NoteBookEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoteBookPersistenceAdapter implements NoteBookPersistencePort {
    private final NoteBookJpaRepository noteBookJpaRepository;
    private final NoteBookEntityMapper noteBookEntityMapper;

    @Override
    public NoteBook save(NoteBook notebook) {
        return noteBookEntityMapper.toDomain(
                noteBookJpaRepository.save(noteBookEntityMapper.toEntity(notebook))
        );
    }

    @Override
    public void deleteById(Long id) {
        noteBookJpaRepository.deleteById(id);
    }

    @Override
    public Optional<NoteBook> findById(Long id) {

        return noteBookJpaRepository.findById(id)
                .map(noteBookEntityMapper::toDomain);
    }

    @Override
    public List<NoteBook> findByUserId(String userId) {
        return noteBookJpaRepository.findByUserId(userId).stream()
                .map(noteBookEntityMapper::toDomain)
                .toList();
    }
}
