package com.microservice.speaking.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Phrase {

    private Long phraseId;
    private String text;
    private Long topicId;
}
