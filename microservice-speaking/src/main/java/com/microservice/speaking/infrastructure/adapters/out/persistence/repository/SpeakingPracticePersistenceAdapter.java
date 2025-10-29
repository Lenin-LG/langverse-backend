package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.application.ports.out.SpeakingPracticePersistencePort;
import com.microservice.speaking.domain.model.SpeakingPractice;
import com.microservice.speaking.infrastructure.adapters.out.persistence.mapper.SpeakingPracticeEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
@RequiredArgsConstructor
public class SpeakingPracticePersistenceAdapter implements SpeakingPracticePersistencePort {
    private final SpeakingPracticeJpaRepository speakingPracticeJpaRepository;
    private final SpeakingPracticeEntityMapper speakingPracticeEntityMapper;
    @Override
    public SpeakingPractice save(SpeakingPractice practice) {
        var entity = speakingPracticeEntityMapper.toEntity(practice);
        var saved = speakingPracticeJpaRepository.save(entity);
        return speakingPracticeEntityMapper.toDomain(saved);
    }

    @Override
    public List<SpeakingPractice> findByUserId(String userId) {
        return speakingPracticeJpaRepository.findByUserId(userId).stream()
                .map(speakingPracticeEntityMapper::toDomain)
                .toList();
    }
}
