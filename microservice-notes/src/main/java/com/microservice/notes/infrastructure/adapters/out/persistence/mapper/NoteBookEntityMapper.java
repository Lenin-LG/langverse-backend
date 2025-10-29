package com.microservice.notes.infrastructure.adapters.out.persistence.mapper;

import com.microservice.notes.domain.model.NoteBook;
import com.microservice.notes.infrastructure.adapters.out.persistence.entity.NoteBookEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NoteEntityMapper.class)
public interface NoteBookEntityMapper {

    NoteBook toDomain(NoteBookEntity entity);

    NoteBookEntity toEntity(NoteBook domain);
}
