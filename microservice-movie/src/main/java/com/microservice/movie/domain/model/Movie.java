package com.microservice.movie.domain.model;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {

    private Long idMovie;
    private String title;
    private String description;
    private Long idCategory;

    private String videoUrl480p;
    private String videoUrl720p;
    private String videoUrl1080p;

    private String audioUrlEn;
    private String audioUrlEs;

    private String subTitlesEnglish;
    private String subTitlesSpanish;

    private int durationInMinutes;
    private LocalDate releaseDate;

    private Integer seasonNumber;
    private Integer episodeNumber;

    private String imageBanner;

    private Boolean estate;

}
