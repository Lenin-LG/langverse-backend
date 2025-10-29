package com.microservice.speaking.application.ports.out;

import com.microservice.speaking.domain.model.Topic;

import java.util.List;

public interface TopicPersistencePort {
    List<Topic> findAll();
    Topic save(Topic topic);
    void deleteById(Long id);
}
