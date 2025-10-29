package com.microservice.movie.infrastructure.adapters.out.persistence.repository;

import com.microservice.movie.application.ports.out.MoviePersistencePort;
import com.microservice.movie.domain.model.Movie;
import com.microservice.movie.infrastructure.adapters.out.persistence.entity.MovieEntity;
import com.microservice.movie.infrastructure.adapters.out.persistence.mapper.MovieEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MoviePersistenceAdapter implements MoviePersistencePort {
    private final MovieJpaRepository movieJpaRepository;
    private final MovieEntityMapper movieEntityMapper;
    @Override
    public Optional<Movie> findById(Long id) {
        return movieJpaRepository.findById(id)
                .map(movieEntityMapper::toDomain);
    }

    @Override
    public List<Movie> findAll() {
        return movieJpaRepository.findAll()
                .stream()
                .map(movieEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Movie save(Movie movie) {
        MovieEntity entity = movieEntityMapper.toEntity(movie);
        MovieEntity saved = movieJpaRepository.save(entity);
        return movieEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        movieJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Movie> findByCategory(Long idCategory) {
         return movieJpaRepository.findByCategory_IdCategory(idCategory)
                .map(movieEntityMapper::toDomain);
    }

    @Override
    public Optional<Movie> findByTitle(String title) {
        return movieJpaRepository.findByTitle(title)
                .map(movieEntityMapper::toDomain);
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        return movieJpaRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(movieEntityMapper::toDomain)
                .filter(movie -> Boolean.TRUE.equals(movie.getEstate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findAllByTitle(String title) {
        return movieJpaRepository.findAllByTitle(title)
                .stream()
                .map(movieEntityMapper::toDomain)
                .toList();
    }

}
