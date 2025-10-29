package com.microservice.music.application.services;

import com.microservice.music.application.ports.in.UploadSongPort;
import com.microservice.music.application.ports.out.FileStoragePort;
import com.microservice.music.domain.model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class UploadSongServiceImpl implements UploadSongPort {
    @Autowired
    private FileStoragePort fileStoragePort;
    @Override
    public Song processAndUploadSong(MultipartFile audioFile,
                                     MultipartFile lyricsEs,
                                     MultipartFile lyricsEn,
                                     Song song) throws IOException {
        String basePath = song.getTitle().replace(" ", "_") + "_" + UUID.randomUUID();

        // Upload .mp3 audio
        String audioKey = basePath + "/audio.mp3";
        Path audioPath = saveTempFile(audioFile);
        fileStoragePort.uploadFile( audioKey, audioPath);
        song.setAudioUrl(audioKey);

        // Upload Spanish lyrics
        if (lyricsEs != null && !lyricsEs.isEmpty()) {
            String lyricsEsKey = basePath + "/lyrics_es.txt";
            Path lyricsEsPath = saveTempFile(lyricsEs);
            fileStoragePort.uploadFile( lyricsEsKey, lyricsEsPath);
            song.setLyricsEs(lyricsEsKey);
        }

        // Upload English lyrics
        if (lyricsEn != null && !lyricsEn.isEmpty()) {
            String lyricsEnKey = basePath + "/lyrics_en.txt";
            Path lyricsEnPath = saveTempFile(lyricsEn);
            fileStoragePort.uploadFile( lyricsEnKey, lyricsEnPath);
            song.setLyricsEn(lyricsEnKey);
        }

        return song;
    }
    private Path saveTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", "-" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
        }
        return tempFile.toPath();
    }
}
