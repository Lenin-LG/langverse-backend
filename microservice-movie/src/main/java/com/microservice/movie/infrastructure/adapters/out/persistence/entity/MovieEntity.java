package com.microservice.movie.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "movie")
public class MovieEntity {
    @Id
    @Column(name = "id_movie")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovie;
    private String title;
    private String description;

    @JoinColumn(name = "id_category")
    @ManyToOne(fetch = FetchType.LAZY)
    private CategoryEntity category;

    @Column(name = "video_url_480p")
    private String videoUrl480p;
    @Column(name = "video_url_720p")
    private String videoUrl720p;
    @Column(name = "video_url_1080p")
    private String videoUrl1080p;

    @Column(name = "audio_url_en")
    private String audioUrlEn;
    @Column(name = "audio_url_es")
    private String audioUrlEs;

    @Column(name = "sub_titles_english")
    private String subTitlesEnglish;
    @Column(name = "sub_titles_spanish")
    private String subTitlesSpanish;

    @Column(name = "duration_in_minutes")
    private int durationInMinutes;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @Column(name = "season_number")
    private Integer seasonNumber;
    @Column(name = "episode_number")
    private Integer episodeNumber;
    @Column(name = "image_banner")
    private String imageBanner;

    private Boolean estate;
}
