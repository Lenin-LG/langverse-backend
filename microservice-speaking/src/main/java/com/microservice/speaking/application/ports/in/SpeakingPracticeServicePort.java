package com.microservice.speaking.application.ports.in;

import com.microservice.speaking.domain.model.SpeakingPractice;

import java.util.List;

public interface SpeakingPracticeServicePort {

    SpeakingPractice savePractice(SpeakingPractice practice);

    List<SpeakingPractice> getPracticesByUser(String userId);
}
