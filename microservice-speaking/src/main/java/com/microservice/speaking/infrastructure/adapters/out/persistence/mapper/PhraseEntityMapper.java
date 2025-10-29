package com.microservice.speaking.infrastructure.adapters.out.persistence.mapper;

import com.microservice.speaking.domain.model.Phrase;
import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.PhraseEntity;
import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.TopicEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PhraseEntityMapper {
    @Mapping(source = "topic.topicId", target = "topicId")
    Phrase toDomain(PhraseEntity entity);
    @Mapping(source = "topicId", target = "topic")
    PhraseEntity toEntity(Phrase domain);
    // Métodos auxiliares para mapear TopicEntity <-> Long
    default Long map(TopicEntity entity) {
        return entity != null ? entity.getTopicId() : null;
    }

    default TopicEntity map(Long id) {
        if (id == null) return null;
        TopicEntity topic = new TopicEntity();
        topic.setTopicId(id);
        return topic;
    }
}