package com.microservice.notes.domain.model;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteBook {
    private Long id;
    private String title;
    private String userId;
    private LocalDateTime createdAt;
}