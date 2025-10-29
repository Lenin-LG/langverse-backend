package com.microservice.speaking.application.ports.in;

import com.microservice.speaking.domain.model.Phrase;

import java.util.List;

public interface PhraseServicePort {
    List<Phrase> getAllPhrasesByTopicId(Long topicId);

    Phrase getPhraseById(Long id);
    Phrase createPhrase(Phrase phrase);
    Phrase updatePhrase(Phrase phrase);
    void deletePhrase(Long id);

}
