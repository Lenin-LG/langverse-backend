package com.microservice.notes.application.ports.in;

import com.microservice.notes.domain.model.NoteBook;

import java.util.List;

public interface NoteBookServicePort {

    NoteBook createNotebook(NoteBook notebook);
    void deleteNotebook(Long id, String userId);
    List<NoteBook> getMyNotebooks(String userId);
    NoteBook getNotebookById(Long id, String userId);
}
