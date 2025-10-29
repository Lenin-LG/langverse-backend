package com.microservice.speaking.infrastructure.adapters.in.rest.mapper;

import com.microservice.speaking.domain.model.SpeakingPractice;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.SpeakingPracticeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpeakingPracticeRestMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "practiceAt", expression = "java(java.time.LocalDateTime.now())")
    SpeakingPractice toDomain(SpeakingPracticeDto dto);

    SpeakingPracticeDto toDto(SpeakingPractice domain);
}