package com.microservice.notes.application.ports.out;
import com.microservice.notes.domain.model.NoteBook;

import java.util.List;
import java.util.Optional;
public interface NoteBookPersistencePort {

    NoteBook save(NoteBook notebook);
    void deleteById(Long id);
    Optional<NoteBook> findById(Long id);
    List<NoteBook> findByUserId(String userId);
}
