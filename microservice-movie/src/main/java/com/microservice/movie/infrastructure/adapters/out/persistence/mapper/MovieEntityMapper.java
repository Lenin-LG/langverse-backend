package com.microservice.movie.infrastructure.adapters.out.persistence.mapper;

import com.microservice.movie.domain.model.Movie;
import com.microservice.movie.infrastructure.adapters.out.persistence.entity.CategoryEntity;
import com.microservice.movie.infrastructure.adapters.out.persistence.entity.MovieEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE )
public interface MovieEntityMapper {
    @Mapping(source = "category.idCategory", target = "idCategory")
    Movie toDomain(MovieEntity entity);

    @Mapping(target = "category", expression = "java(mapToCategory(movie.getIdCategory()))")
    MovieEntity toEntity(Movie movie);

    default CategoryEntity mapToCategory(Long idCategory) {
        if (idCategory == null) {
            return null;
        }
        CategoryEntity category = new CategoryEntity();
        category.setIdCategory(idCategory);
        return category;
    }
}
