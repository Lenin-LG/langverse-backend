package com.microservice.music.application.ports.in;

import com.microservice.music.domain.model.Song;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadSongPort {
    Song processAndUploadSong(MultipartFile audioFile,
                              MultipartFile lyricsEs,
                              MultipartFile lyricsEn,
                              Song song) throws IOException;
}
