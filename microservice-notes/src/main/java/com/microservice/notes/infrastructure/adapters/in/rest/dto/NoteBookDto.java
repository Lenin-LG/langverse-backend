package com.microservice.notes.infrastructure.adapters.in.rest.dto;


import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteBookDto {

    private Long id;
    private String title;
    private String userId;
    private LocalDateTime createdAt;
}
