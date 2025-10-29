package com.microservice.speaking.application.ports.in;

import com.microservice.speaking.domain.model.Topic;

import java.util.List;

public interface TopicServicePort {
    List<Topic> getAllTopics();
    Topic createTopic(Topic topic);
    Topic updateTopic(Topic topic);
    void deleteTopic(Long id);
}
