package com.microservice.music.infrastructure.adapters.out.persistence.repository;

import com.microservice.music.infrastructure.adapters.out.persistence.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SongJpaRepository extends JpaRepository<SongEntity, Long> {
    @Query("SELECT s FROM SongEntity s JOIN s.albums a WHERE a.idAlbum = :albumId")
    List<SongEntity> findByAlbumId(@Param("albumId") Long albumId);

    List<SongEntity> findByTitleContainingIgnoreCase(String title);
}
