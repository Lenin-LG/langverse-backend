package com.microservice.speaking.application.services;

import com.microservice.speaking.application.ports.in.SpeakingPracticeServicePort;
import com.microservice.speaking.application.ports.out.PhrasePersistencePort;
import com.microservice.speaking.application.ports.out.SpeakingPracticePersistencePort;
import com.microservice.speaking.domain.exceptions.PhraseNotFoundException;
import com.microservice.speaking.domain.model.SpeakingPractice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpeakingPracticeServiceImpl implements SpeakingPracticeServicePort {

    @Autowired
    private SpeakingPracticePersistencePort speakingPracticePersistencePort;
    @Autowired
    private PhrasePersistencePort phrasePersistencePort;
    @Override
    public SpeakingPractice savePractice(SpeakingPractice practice) {
        phrasePersistencePort.findById(practice.getPhraseId())
                .orElseThrow(PhraseNotFoundException::new);

        return speakingPracticePersistencePort.save(practice);
    }

    @Override
    public List<SpeakingPractice> getPracticesByUser(String userId) {
        return speakingPracticePersistencePort.findByUserId(userId);
    }

}
