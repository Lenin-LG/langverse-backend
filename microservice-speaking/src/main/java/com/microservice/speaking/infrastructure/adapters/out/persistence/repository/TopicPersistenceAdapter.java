package com.microservice.speaking.infrastructure.adapters.out.persistence.repository;

import com.microservice.speaking.application.ports.out.TopicPersistencePort;
import com.microservice.speaking.domain.model.Topic;
import com.microservice.speaking.infrastructure.adapters.out.persistence.mapper.TopicEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TopicPersistenceAdapter implements TopicPersistencePort {
    private final TopicJpaRepository topicJpaRepository;
    private final TopicEntityMapper topicEntityMapper;
    @Override
    public List<Topic> findAll() {
        return topicJpaRepository.findAll().stream()
                .map(topicEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Topic save(Topic topic) {
        var entity = topicEntityMapper.toEntity(topic);
        var saved = topicJpaRepository.save(entity);
        return topicEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        topicJpaRepository.deleteById(id);
    }
}
