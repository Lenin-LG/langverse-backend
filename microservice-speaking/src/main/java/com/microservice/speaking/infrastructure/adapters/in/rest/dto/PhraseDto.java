package com.microservice.speaking.infrastructure.adapters.in.rest.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhraseDto {

    private Long phraseId;
    private String text;
    private Long topicId;
}