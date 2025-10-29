package com.microservice.music.infrastructure.adapters.out.persistence.repository;

import com.microservice.music.application.ports.out.SongPersistencePort;
import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.SongEntity;
import com.microservice.music.infrastructure.adapters.out.persistence.mapper.SongEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class SongPersistenceAdapter implements SongPersistencePort {

    private final SongJpaRepository songJpaRepository;
    private final SongEntityMapper songEntityMapper;
    @Override
    public Song save(Song song) {
        SongEntity entity = songEntityMapper.toEntity(song);
        SongEntity saved = songJpaRepository.save(entity);
        return songEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Song> findById(Long id) {
        return songJpaRepository.findById(id)
                .map(songEntityMapper::toDomain);
    }

    @Override
    public List<Song> findAll() {
        return songJpaRepository.findAll()
                .stream()
                .map(songEntityMapper::toDomain)
                .toList();

    }

    @Override
    public void deleteById(Long id) {
        songJpaRepository.deleteById(id);
    }

    @Override
    public List<Song> findByAlbumId(Long albumId) {
        return songJpaRepository.findByAlbumId(albumId)
                .stream()
                .map(songEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Song> findByTitleContains(String title) {
        return songJpaRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(songEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Song> getSongById(Long id) {
        return songJpaRepository.findById(id)
                .map(songEntityMapper::toDomain);
    }
}
