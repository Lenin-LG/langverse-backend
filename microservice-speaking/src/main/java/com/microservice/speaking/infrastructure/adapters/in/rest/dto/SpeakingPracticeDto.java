package com.microservice.speaking.infrastructure.adapters.in.rest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpeakingPracticeDto {

    private String userId;
    private Long phraseId;
    private String spokenText;
    private Double accuracy;
}