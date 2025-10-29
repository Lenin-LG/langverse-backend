package com.microservice.speaking.infrastructure.adapters.in.rest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicDto {

    private Long topicId;
    private String name;
}