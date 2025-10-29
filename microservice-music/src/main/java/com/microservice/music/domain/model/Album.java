package com.microservice.music.domain.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Album {
    private Long idAlbum;
    private String title;
    private String description;
    private String imageCover;
    private Boolean visible;
    private List<Song> songs;
    private String userId;
}