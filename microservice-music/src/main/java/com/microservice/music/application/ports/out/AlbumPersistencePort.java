package com.microservice.music.application.ports.out;

import com.microservice.music.domain.model.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumPersistencePort {
    Album save(Album album);
    Optional<Album> findById(Long id);
    List<Album> findAll();
    void deleteById(Long id);
    List<Album> findByTitleContains(String title);
    List<Album> findByUserId(String userId);
}
