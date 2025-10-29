package com.microservice.speaking.application.ports.out;

import com.microservice.speaking.domain.model.SpeakingPractice;

import java.util.List;

public interface SpeakingPracticePersistencePort {
    SpeakingPractice save(SpeakingPractice practice);

    List<SpeakingPractice> findByUserId(String userId);
}
