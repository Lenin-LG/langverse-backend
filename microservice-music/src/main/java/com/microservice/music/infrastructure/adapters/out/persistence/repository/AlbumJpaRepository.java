package com.microservice.music.infrastructure.adapters.out.persistence.repository;

import com.microservice.music.infrastructure.adapters.out.persistence.entity.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumJpaRepository extends JpaRepository<AlbumEntity, Long> {
    List<AlbumEntity> findByTitleContainingIgnoreCase(String title);
    List<AlbumEntity> findByUserId(String userId);

}
