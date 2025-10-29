package com.microservice.movie.infrastructure.adapters.out.persistence.repository;

import com.microservice.movie.infrastructure.adapters.out.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieJpaRepository extends JpaRepository<MovieEntity, Long> {
    Optional<MovieEntity> findByCategory_IdCategory(Long idCategory);
    Optional<MovieEntity> findByTitle(String title);
    List<MovieEntity> findByTitleContainingIgnoreCase(String title);
    List<MovieEntity> findAllByTitle(String title);

}
