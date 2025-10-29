package com.microservice.speaking.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "speaking_practices")
public class SpeakingPracticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @JoinColumn(name = "phrase_id")
    @ManyToOne
    private PhraseEntity phraseId;
    private String spokenText;
    private Double accuracy;
    private LocalDateTime practiceAt;
}
