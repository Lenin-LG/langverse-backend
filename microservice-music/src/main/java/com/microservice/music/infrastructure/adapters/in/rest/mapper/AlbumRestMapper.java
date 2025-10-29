package com.microservice.music.infrastructure.adapters.in.rest.mapper;

import com.microservice.music.domain.model.Album;
import com.microservice.music.infrastructure.adapters.in.rest.dto.AlbumDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumRestMapper {
    @Mapping(target = "songs", ignore = true)// songIds will be used separately
    Album toDomain(AlbumDto dto);
    @Mapping(target = "songIds", expression = "java(album.getSongs() != null ? album.getSongs().stream().map(Song::getIdSong).toList() : null)")
    AlbumDto toDto(Album album);
}
