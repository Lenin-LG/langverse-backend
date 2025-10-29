package com.microservice.movie.application.ports.out;

import com.microservice.movie.domain.model.Movie;

import java.util.List;
import java.util.Optional;

public interface MoviePersistencePort {
    Optional<Movie> findById(Long id);
    List<Movie> findAll();
    Movie save(Movie movie);
    void deleteById(Long id);
    Optional<Movie> findByCategory(Long idCategory);
    Optional<Movie> findByTitle(String title);
    List<Movie> searchByTitle(String title);
    List<Movie> findAllByTitle(String title);

}
