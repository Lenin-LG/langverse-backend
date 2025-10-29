package com.microservice.speaking.infrastructure.adapters.out.persistence.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phrases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhraseEntity {

    @Id
    @Column(name = "phrase_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long phraseId;

    private String text;

    @JoinColumn(name = "topic_id")
    @ManyToOne
    private TopicEntity topic;
}