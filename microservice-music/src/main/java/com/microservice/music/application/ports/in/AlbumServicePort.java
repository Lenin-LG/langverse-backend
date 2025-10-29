package com.microservice.music.application.ports.in;

import com.microservice.music.domain.model.Album;
import com.microservice.music.domain.model.Song;

import java.util.List;

public interface AlbumServicePort {
    Album saveAlbum(Album album);
    Album getAlbumById(Long id);
    List<Album> getAllAlbums();
    void deleteAlbum(Long id);
    List<Album> searchAlbumsByTitle(String title);
    List<Album> findByUserId(String userId);
    Album saveAlbumWithSongs(Album album, List<Long> songIds);
    void addSongToAlbum(Long albumId, Long songId);
    void removeSongFromAlbum(Long albumId, Long songId);
    List<Song> getSongsByAlbumIdForUser(Long albumId, String userId);
}
