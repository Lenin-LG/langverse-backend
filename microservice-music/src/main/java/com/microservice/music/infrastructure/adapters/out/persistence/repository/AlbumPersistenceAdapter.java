package com.microservice.music.infrastructure.adapters.out.persistence.repository;

import com.microservice.music.application.ports.out.AlbumPersistencePort;
import com.microservice.music.domain.model.Album;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.AlbumEntity;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.SongEntity;
import com.microservice.music.infrastructure.adapters.out.persistence.mapper.AlbumEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AlbumPersistenceAdapter implements AlbumPersistencePort{

    private final AlbumJpaRepository albumJpaRepository;
    private final AlbumEntityMapper albumEntityMapper;
    @Override
    public Album save(Album album) {
        AlbumEntity entity = albumEntityMapper.toEntity(album);

        //IMPORTANT: Make sure the songs are also linked to this album
        if (entity.getSongs() != null) {
            for (SongEntity songEntity : entity.getSongs()) {
                // add this album to the song's album list if it isn't already
                if (songEntity.getAlbums() == null) {
                    songEntity.setAlbums(new ArrayList<>());
                }
                if (songEntity.getAlbums().stream().noneMatch(a -> a.getIdAlbum().equals(entity.getIdAlbum()))) {
                    songEntity.getAlbums().add(entity);
                }
            }
        }

        AlbumEntity saved = albumJpaRepository.save(entity);
        return albumEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Album> findById(Long id) {
        return albumJpaRepository.findById(id)
                .map(albumEntityMapper::toDomain);
    }

    @Override
    public List<Album> findAll() {
        return albumJpaRepository.findAll()
                .stream()
                .map(albumEntityMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        albumJpaRepository.deleteById(id);
    }

    @Override
    public List<Album> findByTitleContains(String title) {
        return albumJpaRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(albumEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Album> findByUserId(String userId) {
        return albumJpaRepository.findByUserId(userId)
                .stream()
                .map(albumEntityMapper::toDomain)
                .toList();
    }
}
