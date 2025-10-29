package com.microservice.music.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "song")
public class SongEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSong;

    private String title;

    @Column(name = "audio_url")
    private String audioUrl;

    @Column(name = "lyrics_es", columnDefinition = "TEXT")
    private String lyricsEs;

    @Column(name = "lyrics_en", columnDefinition = "TEXT")
    private String lyricsEn;

    @ManyToMany(mappedBy = "songs")
    private List<AlbumEntity> albums = new ArrayList<>();


    private Integer durationInSeconds;

    private Boolean visible;
}