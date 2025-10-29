package com.microservice.music.infrastructure.adapters.in.rest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDto {
    private Long idSong;
    private String title;
    private String audioUrl;
    private String lyricsEs;
    private String lyricsEn;
    private Long idAlbum;
    private Integer durationInSeconds;
    private Boolean visible;
}
