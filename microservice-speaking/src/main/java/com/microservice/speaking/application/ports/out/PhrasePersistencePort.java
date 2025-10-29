package com.microservice.speaking.application.ports.out;

import com.microservice.speaking.domain.model.Phrase;

import java.util.List;
import java.util.Optional;

public interface PhrasePersistencePort {
    Optional<Phrase> findById(Long id);

    List<Phrase> findAllByTopicId(Long topicId);
    Phrase save(Phrase phrase);
    void deleteById(Long id);

}
