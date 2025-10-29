package com.microservice.speaking.infrastructure.adapters.in.rest.mapper;

import com.microservice.speaking.domain.model.Phrase;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.PhraseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PhraseRestMapper {
    Phrase toDomain(PhraseDto dto);
    PhraseDto toDto(Phrase domain);
}
