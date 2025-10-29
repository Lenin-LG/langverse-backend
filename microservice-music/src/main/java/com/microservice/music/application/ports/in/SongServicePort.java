package com.microservice.music.application.ports.in;

import com.microservice.music.domain.model.Song;

import java.util.List;

public interface SongServicePort {
    Song saveSong(Song song);
    Song getSongById(Long id);
    List<Song> getAllSongs();
    void deleteSong(Long id);
    List<Song> getSongsByAlbumId(Long albumId);
    List<Song> searchSongsByTitle(String title);
}
