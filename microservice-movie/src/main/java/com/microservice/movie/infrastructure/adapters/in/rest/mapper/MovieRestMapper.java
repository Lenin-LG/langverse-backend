package com.microservice.movie.infrastructure.adapters.in.rest.mapper;

import com.microservice.movie.domain.model.Movie;
import com.microservice.movie.infrastructure.adapters.in.rest.dto.MovieDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieRestMapper {
    Movie toDomain(MovieDto dto);
    MovieDto toDto(Movie movie);
}
