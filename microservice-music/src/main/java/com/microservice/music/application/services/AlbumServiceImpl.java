package com.microservice.music.application.services;

import com.microservice.music.application.ports.in.AlbumServicePort;
import com.microservice.music.application.ports.out.AlbumPersistencePort;
import com.microservice.music.application.ports.out.SongPersistencePort;
import com.microservice.music.domain.exceptions.AccessDeniedToAlbumException;
import com.microservice.music.domain.exceptions.AlbumNotFoundException;
import com.microservice.music.domain.model.Album;
import com.microservice.music.domain.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AlbumServiceImpl implements AlbumServicePort {

    @Autowired
    private AlbumPersistencePort albumPersistencePort;
    @Autowired
    private SongPersistencePort songPersistencePort;
    @Override
    public Album saveAlbum(Album album) {
        return albumPersistencePort.save(album);
    }

    @Override
    public Album getAlbumById(Long id) {
        return albumPersistencePort.findById(id)
                .orElseThrow(AlbumNotFoundException :: new);
    }

    @Override
    public List<Album> getAllAlbums() {
        return albumPersistencePort.findAll();
    }

    @Override
    public void deleteAlbum(Long id) {
        if(albumPersistencePort.findById(id).isEmpty()){
            throw new AlbumNotFoundException();
        }
        albumPersistencePort.deleteById(id);
    }

    @Override
    public List<Album> searchAlbumsByTitle(String title) {
        return albumPersistencePort.findByTitleContains(title);
    }

    @Override
    public List<Album> findByUserId(String userId) {
        return albumPersistencePort.findByUserId(userId);
    }

    @Override
    public Album saveAlbumWithSongs(Album album, List<Long> songIds) {
        List<Song> songs = songIds.stream()
                .map(id -> songPersistencePort.getSongById(id)
                        .orElseThrow(() -> new IllegalArgumentException("There is no song with ID: " + id)))
                .toList();
        album.setSongs(songs);
        return albumPersistencePort.save(album);
    }

    @Override
    public void addSongToAlbum(Long albumId, Long songId) {
        Album album = albumPersistencePort.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        Song song = songPersistencePort.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        if (album.getSongs().stream().noneMatch(s -> s.getIdSong().equals(songId))) {
            album.getSongs().add(song);
            albumPersistencePort.save(album);
        }
    }

    @Override
    public void removeSongFromAlbum(Long albumId, Long songId) {
        Album album = albumPersistencePort.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        album.getSongs().removeIf(s -> s.getIdSong().equals(songId));
        albumPersistencePort.save(album);
    }

    @Override
    public List<Song> getSongsByAlbumIdForUser(Long albumId, String userId) {
        Album album = albumPersistencePort.findById(albumId)
                .orElseThrow(AlbumNotFoundException ::  new);

        if (!album.getUserId().equals(userId)) {
            throw new AccessDeniedToAlbumException("You do not have permission to view the songs on this album.");
        }


        return album.getSongs();
    }
}
