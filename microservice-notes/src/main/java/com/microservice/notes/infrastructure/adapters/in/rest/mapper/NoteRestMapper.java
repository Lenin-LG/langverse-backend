package com.microservice.notes.infrastructure.adapters.in.rest.mapper;

import com.microservice.notes.domain.model.Note;
import com.microservice.notes.infrastructure.adapters.in.rest.dto.NoteDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoteRestMapper {

    NoteDto toDto(Note domain);

    Note toDomain(NoteDto dto);
}