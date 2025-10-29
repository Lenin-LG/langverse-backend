package com.microservice.speaking.infrastructure.adapters.out.persistence.mapper;

import com.microservice.speaking.domain.model.Topic;
import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.TopicEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TopicEntityMapper {
    Topic toDomain(TopicEntity entity);
    TopicEntity toEntity(Topic domain);
}