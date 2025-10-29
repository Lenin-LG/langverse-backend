package com.microservice.music.infrastructure.adapters.in.rest.mapper;

import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.in.rest.dto.SongDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongRestMapper {
    Song toDomain(SongDto dto);
    SongDto toDto(Song song);
}
