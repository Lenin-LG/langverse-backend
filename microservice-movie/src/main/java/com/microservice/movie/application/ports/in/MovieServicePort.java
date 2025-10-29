package com.microservice.movie.application.ports.in;

import com.microservice.movie.domain.model.Movie;

import java.util.List;

public interface MovieServicePort {

    Movie findById(Long id);
    Movie save(Movie movie);
    List<Movie> findAll();
    Movie findByCategory(Long idCategory);
    void deleteById(Long id);
    Movie updateById(Long id,Movie updateMovie);
    Movie findByTitle(String title);
    List<Movie> searchByTitle(String title);
    List<Movie> getEpisodesBySeriesTitle(String title);
    void setActiveStatus(Long id, boolean active);
}
