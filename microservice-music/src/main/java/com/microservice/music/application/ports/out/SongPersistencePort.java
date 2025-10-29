package com.microservice.music.application.ports.out;

import com.microservice.music.domain.model.Song;

import java.util.List;
import java.util.Optional;

public interface SongPersistencePort {
    Song save(Song song);
    Optional<Song> findById(Long id);
    List<Song> findAll();
    void deleteById(Long id);
    List<Song> findByAlbumId(Long albumId);
    List<Song> findByTitleContains(String title);
    Optional<Song> getSongById(Long id);

}
