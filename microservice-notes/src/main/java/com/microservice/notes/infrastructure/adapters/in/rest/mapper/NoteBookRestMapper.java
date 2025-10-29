package com.microservice.notes.infrastructure.adapters.in.rest.mapper;

import com.microservice.notes.domain.model.NoteBook;
import com.microservice.notes.infrastructure.adapters.in.rest.dto.NoteBookDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = NoteRestMapper.class)
public interface NoteBookRestMapper {

    NoteBookDto toDto(NoteBook domain);

    NoteBook toDomain(NoteBookDto dto);
}