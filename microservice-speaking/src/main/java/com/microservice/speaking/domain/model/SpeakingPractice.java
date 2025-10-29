package com.microservice.speaking.domain.model;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingPractice {
    private Long id;
    private String userId;
    private Long phraseId;
    private String spokenText;
    private Double accuracy;
    private LocalDateTime practiceAt;
}
