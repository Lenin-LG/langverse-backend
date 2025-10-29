package com.microservice.music.infrastructure.adapters.out.persistence.mapper;


import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.AlbumEntity;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.SongEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SongEntityMapper {

    @Mapping(target = "albumIds", expression = "java(mapAlbumIds(entity.getAlbums()))")
    Song toDomain(SongEntity entity);

    @Mapping(target = "albums", expression = "java(mapToAlbums(song.getAlbumIds()))")
    SongEntity toEntity(Song song);

    default List<Long> mapAlbumIds(List<AlbumEntity> albums) {
        if (albums == null) return List.of();
        return albums.stream()
                .map(AlbumEntity::getIdAlbum)
                .toList();
    }

    default List<AlbumEntity> mapToAlbums(List<Long> ids) {
        if (ids == null) return List.of();
        return ids.stream().map(id -> {
            AlbumEntity album = new AlbumEntity();
            album.setIdAlbum(id);
            return album;
        }).toList();
    }
}