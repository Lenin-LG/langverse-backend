package com.microservice.speaking.application.services;

import com.microservice.speaking.application.ports.in.TopicServicePort;
import com.microservice.speaking.application.ports.out.TopicPersistencePort;
import com.microservice.speaking.domain.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TopicServiceImpl implements TopicServicePort {
    @Autowired
    private TopicPersistencePort topicPersistencePort;
    @Override
    public List<Topic> getAllTopics() {
        return topicPersistencePort.findAll();
    }

    @Override
    public Topic createTopic(Topic topic) {
        return topicPersistencePort.save(topic);
    }

    @Override
    public Topic updateTopic(Topic topic) {
        return topicPersistencePort.save(topic);
    }

    @Override
    public void deleteTopic(Long id) {
        topicPersistencePort.deleteById(id);
    }
}
