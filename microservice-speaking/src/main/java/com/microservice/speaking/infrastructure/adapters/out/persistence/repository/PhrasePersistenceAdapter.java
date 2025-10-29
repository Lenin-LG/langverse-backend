package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.application.ports.out.PhrasePersistencePort;
import com.microservice.speaking.domain.model.Phrase;
import com.microservice.speaking.infrastructure.adapters.out.persistence.mapper.PhraseEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class PhrasePersistenceAdapter implements PhrasePersistencePort {
    private final PhraseJpaRepository phraseJpaRepository;
    private final PhraseEntityMapper phraseEntityMapper;

    @Override
    public Optional<Phrase> findById(Long id) {
        return phraseJpaRepository.findById(id)
                .map(phraseEntityMapper::toDomain);
    }

    @Override
    public List<Phrase> findAllByTopicId(Long topicId) {
        return phraseJpaRepository.findByTopic_TopicId(topicId).stream()
                .map(phraseEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Phrase save(Phrase phrase) {
        var entity = phraseEntityMapper.toEntity(phrase);
        var saved = phraseJpaRepository.save(entity);
        return phraseEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        phraseJpaRepository.deleteById(id);
    }
}
