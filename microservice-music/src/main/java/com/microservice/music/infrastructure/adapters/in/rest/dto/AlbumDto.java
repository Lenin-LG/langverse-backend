package com.microservice.music.infrastructure.adapters.in.rest.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto {
    private Long idAlbum;
    private String title;
    private String description;
    private String imageCover;
    private List<SongDto> songs;
    private List<Long> songIds;
    private String userId;
}
