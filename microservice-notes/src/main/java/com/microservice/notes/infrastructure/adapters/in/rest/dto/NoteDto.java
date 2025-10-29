package com.microservice.notes.infrastructure.adapters.in.rest.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private String userId;
    private Long notebookId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
