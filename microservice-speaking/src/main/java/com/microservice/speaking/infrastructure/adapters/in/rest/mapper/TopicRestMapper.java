package com.microservice.speaking.infrastructure.adapters.in.rest.mapper;

import com.microservice.speaking.domain.model.Topic;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.TopicDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicRestMapper {
    Topic toDomain(TopicDto dto);
    TopicDto toDto(Topic domain);
}