package com.microservice.notes.application.services;

import com.microservice.notes.application.ports.in.NoteBookServicePort;
import com.microservice.notes.application.ports.out.NoteBookPersistencePort;
import com.microservice.notes.domain.exceptions.AccessDeniedToNotebookException;
import com.microservice.notes.domain.model.NoteBook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class NoteBookServiceImpl implements NoteBookServicePort {
    @Autowired
    private NoteBookPersistencePort noteBookPersistencePort;
    @Override
    public NoteBook createNotebook(NoteBook notebook) {
        notebook.setCreatedAt(LocalDateTime.now());
        return noteBookPersistencePort.save(notebook);
    }

    @Override
    public void deleteNotebook(Long id, String userId) {

        NoteBook existing = getNotebookById(id, userId);
        if (!existing.getUserId().equals(userId)) {
          new AccessDeniedToNotebookException("You do not have permission to delete this notebook");
        }
        noteBookPersistencePort.deleteById(id);
    }

    @Override
    public List<NoteBook> getMyNotebooks(String userId) {
        return noteBookPersistencePort.findByUserId(userId);
    }

    @Override
    public NoteBook getNotebookById(Long id, String userId) {
        return noteBookPersistencePort.findById(id)
                .filter(nb -> nb.getUserId().equals(userId))
                .orElseThrow(() -> new AccessDeniedToNotebookException("You do not have access to this notebook"));

    }
}
