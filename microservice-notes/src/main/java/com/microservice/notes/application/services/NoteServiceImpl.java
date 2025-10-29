package com.microservice.notes.application.services;

import com.microservice.notes.application.ports.in.NoteServicePort;
import com.microservice.notes.application.ports.out.NotePersistencePort;
import com.microservice.notes.domain.model.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class NoteServiceImpl implements NoteServicePort {
    @Autowired
    private NotePersistencePort notePersistencePort;
    @Override
    public Note createNote(Note note) {
        note.setCreatedAt(LocalDateTime.now());
        note.setUpdatedAt(LocalDateTime.now());
        return notePersistencePort.save(note);
    }

    @Override
    public void deleteNote(Long id, String userId) {
        Note note = getNoteById(id, userId);
        notePersistencePort.deleteById(id);
    }

    @Override
    public Note updateNote(Note note, String userId) {
        Note existing = getNoteById(note.getId(), userId);

        existing.setTitle(note.getTitle());
        existing.setContent(note.getContent());

        if (note.getNotebookId() != null) {
            existing.setNotebookId(note.getNotebookId());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        return notePersistencePort.save(existing);
    }

    @Override
    public Note getNoteById(Long id, String userId) {

        return notePersistencePort.findById(id)
                .filter(n -> n.getUserId().equals(userId)).orElseThrow();
    }

    @Override
    public List<Note> getNotesByNotebook(Long notebookId, String userId) {
        return notePersistencePort.findByNotebookId(notebookId).stream()
                .filter(n -> n.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Note> searchMyNotes(String keyword, String userId) {
        return notePersistencePort.searchNotes(keyword, userId);
    }
}
