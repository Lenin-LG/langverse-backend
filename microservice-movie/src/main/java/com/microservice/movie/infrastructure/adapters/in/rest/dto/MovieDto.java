package com.microservice.movie.infrastructure.adapters.in.rest.dto;

import lombok.*;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDto {

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
