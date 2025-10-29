package com.microservice.speaking.application.services;

import com.microservice.speaking.application.ports.in.PhraseServicePort;
import com.microservice.speaking.application.ports.out.PhrasePersistencePort;
import com.microservice.speaking.domain.exceptions.PhraseNotFoundException;
import com.microservice.speaking.domain.model.Phrase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhraseServiceImpl implements PhraseServicePort {
    @Autowired
    private PhrasePersistencePort phrasePersistencePort;

    @Override
    public List<Phrase> getAllPhrasesByTopicId(Long topicId) {
        return phrasePersistencePort.findAllByTopicId(topicId);
    }

    @Override
    public Phrase getPhraseById(Long id) {
        return phrasePersistencePort.findById(id)
                .orElseThrow(PhraseNotFoundException::new);
    }

    @Override
    public Phrase createPhrase(Phrase phrase) {
        return phrasePersistencePort.save(phrase);
    }

    @Override
    public Phrase updatePhrase(Phrase phrase) {
        return phrasePersistencePort.save(phrase);
    }

    @Override
    public void deletePhrase(Long id) {
        phrasePersistencePort.deleteById(id);
    }
}
