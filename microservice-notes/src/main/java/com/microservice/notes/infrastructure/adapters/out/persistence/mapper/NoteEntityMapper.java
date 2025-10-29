package com.microservice.notes.infrastructure.adapters.out.persistence.mapper;

import com.microservice.notes.domain.model.Note;
import com.microservice.notes.infrastructure.adapters.out.persistence.entity.NoteBookEntity;
import com.microservice.notes.infrastructure.adapters.out.persistence.entity.NoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoteEntityMapper {

    @Mapping(source = "notebook.id", target = "notebookId")
    Note toDomain(NoteEntity entity);

    @Mapping(target = "notebook", expression = "java(toNotebookEntity(domain.getNotebookId()))")
    NoteEntity toEntity(Note domain);

    default NoteBookEntity toNotebookEntity(Long notebookId) {
        if (notebookId == null) return null;
        NoteBookEntity notebook = new NoteBookEntity();
        notebook.setId(notebookId);
        return notebook;
    }
}
