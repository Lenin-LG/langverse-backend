package com.microservice.movie.infrastructure.adapters.in.rest.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieAssetsDto {
    private String audioEsUrl;
    private String audioEnUrl;
    private String subtitlesEsUrl;
    private String subtitlesEnUrl;
    private String imageBannerUrl;
}