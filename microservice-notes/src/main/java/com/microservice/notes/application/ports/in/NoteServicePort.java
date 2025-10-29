package com.microservice.notes.application.ports.in;

import com.microservice.notes.domain.model.Note;

import java.util.List;

public interface NoteServicePort {

    Note createNote(Note note);
    void deleteNote(Long id, String userId);
    Note updateNote(Note note, String userId);
    Note getNoteById(Long id, String userId);
    List<Note> getNotesByNotebook(Long notebookId, String userId);
    List<Note> searchMyNotes(String keyword, String userId);
}
