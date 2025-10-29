package com.microservice.speaking.domain.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Topic {

    private Long topicId;
    private String name;
}
