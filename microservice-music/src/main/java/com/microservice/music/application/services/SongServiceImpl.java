package com.microservice.music.application.services;

import com.microservice.music.application.ports.in.SongServicePort;
import com.microservice.music.application.ports.out.SongPersistencePort;
import com.microservice.music.domain.exceptions.SongNotFoundException;
import com.microservice.music.domain.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongServiceImpl implements SongServicePort {
    @Autowired
    private SongPersistencePort songPersistencePort;
    @Override
    public Song saveSong(Song song) {
        return songPersistencePort.save(song);
    }

    @Override
    public Song getSongById(Long id) {
        return songPersistencePort.findById(id)
                .orElseThrow(SongNotFoundException:: new);
    }

    @Override
    public List<Song> getAllSongs() {
        return songPersistencePort.findAll();
    }

    @Override
    public void deleteSong(Long id) {
        if(songPersistencePort.findById(id).isEmpty()){
            throw new SongNotFoundException();
        }
        songPersistencePort.deleteById(id);
    }

    @Override
    public List<Song> getSongsByAlbumId(Long albumId) {
        return songPersistencePort.findByAlbumId(albumId);
    }

    @Override
    public List<Song> searchSongsByTitle(String title) {
        return songPersistencePort.findByTitleContains(title);
    }
}
