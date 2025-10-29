package com.microservice.speaking.infrastructure.adapters.out.persistence.mapper;

import com.microservice.speaking.domain.model.SpeakingPractice;
import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.PhraseEntity;
import com.microservice.speaking.infrastructure.adapters.out.persistence.entity.SpeakingPracticeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SpeakingPracticeEntityMapper {
    SpeakingPractice toDomain(SpeakingPracticeEntity entity);
    SpeakingPracticeEntity toEntity(SpeakingPractice domain);

    // Métodos auxiliares para mapear PhraseEntity <-> Long
    default Long map(PhraseEntity entity) {
        return entity != null ? entity.getPhraseId() : null;
    }

    default PhraseEntity map(Long id) {
        if (id == null) return null;
        PhraseEntity phrase = new PhraseEntity();
        phrase.setPhraseId(id);
        return phrase;
    }
}