package com.microservice.music.domain.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Song {
    private Long idSong;
    private String title;
    private String audioUrl;
    private String lyricsEs;
    private String lyricsEn;
    private List<Long> albumIds;
    private Integer durationInSeconds;
    private Boolean visible;
}
