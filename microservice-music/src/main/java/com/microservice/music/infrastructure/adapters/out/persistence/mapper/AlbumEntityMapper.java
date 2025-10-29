package com.microservice.music.infrastructure.adapters.out.persistence.mapper;

import com.microservice.music.domain.model.Album;
import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.AlbumEntity;
import com.microservice.music.infrastructure.adapters.out.persistence.entity.SongEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumEntityMapper {

    @Mapping(target = "songs", source = "songs")
    Album toDomain(AlbumEntity entity);

    @Mapping(target = "songs", source = "songs")
    AlbumEntity toEntity(Album domain);

    List<Song> map(List<SongEntity> entities);
    List<SongEntity> mapToEntity(List<Song> domains);
}