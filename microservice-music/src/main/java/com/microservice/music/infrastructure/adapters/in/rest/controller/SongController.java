package com.microservice.music.infrastructure.adapters.in.rest.controller;

import com.microservice.music.application.ports.in.SongServicePort;
import com.microservice.music.application.ports.in.UploadSongPort;
import com.microservice.music.application.ports.out.FileStoragePort;
import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.in.rest.dto.SongDto;
import com.microservice.music.infrastructure.adapters.in.rest.mapper.SongRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@RequestMapping("/song")
@RestController
public class SongController {
    @Autowired
    private SongServicePort songServicePort;
    @Autowired
    private UploadSongPort uploadSongPort;
    @Autowired
    private SongRestMapper songRestMapper;
    @Autowired
    private FileStoragePort fileStoragePort;

    //Upload new song (.mp3 file + lyrics)
    @Operation(
            summary = "Upload new song (.mp3 file + lyrics)",
            description = "Allows an admin to upload a new song with audio (.mp3) and lyrics in Spanish and English.",
            tags = {"Song-Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Song uploaded successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SongDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request (missing file or incorrect data)",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal error processing song",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping(value = "/songs/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<SongDto> uploadSong(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("lyricsEs") MultipartFile lyricsEs,
            @RequestParam("lyricsEn") MultipartFile lyricsEn,
            @RequestParam("title") String title,
            @RequestParam("duration") Integer durationInSeconds
    ) throws IOException {

        Song song = Song.builder()
                .title(title)
                .durationInSeconds(durationInSeconds)
                .visible(true)
                .build();

        Song processedSong = uploadSongPort.processAndUploadSong(audioFile, lyricsEs, lyricsEn, song);
        Song saved = songServicePort.saveSong(processedSong);

        return ResponseEntity.status(HttpStatus.CREATED).body(songRestMapper.toDto(saved));
    }
    //Get all songs
    @Operation(
            summary = "Get all songs",
            description = "Returns a list of all songs available on the system.",
            tags = {"Song-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Song list successfully obtained",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SongDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs() {
        List<Song> songs = songServicePort.getAllSongs();
        return ResponseEntity.ok(songs.stream().map(songRestMapper::toDto).toList());
    }
    //Get song by ID
    @Operation(
            summary = "Get song by ID",
            description = "Returns information for a specific song, including pre-signed links for audio and lyrics.",
            tags = {"Song-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Song found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SongDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Song not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping("/songs/{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable("id") Long id) {
        Song song = songServicePort.getSongById(id);

        SongDto dto = songRestMapper.toDto(song);
        dto.setAudioUrl(fileStoragePort.generatePresignedDownloadUrl( song.getAudioUrl()));

        if (song.getLyricsEs() != null) {
            dto.setLyricsEs(fileStoragePort.generatePresignedDownloadUrl( song.getLyricsEs()));
        }

        if (song.getLyricsEn() != null) {
            dto.setLyricsEn(fileStoragePort.generatePresignedDownloadUrl( song.getLyricsEn()));
        }

        return ResponseEntity.ok(dto);
    }
    //Delete a song
    @Operation(
            summary = "Delete a song",
            description = "Allows you to delete a specific song by its ID.",
            tags = {"Song-Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Song successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Song not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @DeleteMapping("/songs/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<Void> deleteSong(@PathVariable("id") Long id) {
        songServicePort.deleteSong(id);
        return ResponseEntity.noContent().build();
    }
    private String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
