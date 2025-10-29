package com.microservice.music.infrastructure.adapters.in.rest.controller;

import com.microservice.music.application.ports.in.AlbumServicePort;
import com.microservice.music.application.ports.in.SongServicePort;
import com.microservice.music.domain.model.Album;
import com.microservice.music.domain.model.Song;
import com.microservice.music.infrastructure.adapters.in.rest.dto.AlbumDto;
import com.microservice.music.infrastructure.adapters.in.rest.dto.SongDto;
import com.microservice.music.infrastructure.adapters.in.rest.mapper.AlbumRestMapper;
import com.microservice.music.infrastructure.adapters.in.rest.mapper.SongRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/album")
@RestController
public class AlbumController {

    @Autowired
    private AlbumServicePort albumServicePort;
    @Autowired
    private AlbumRestMapper albumRestMapper;
    @Autowired
    private SongServicePort songServicePort;
    @Autowired
    private SongRestMapper songRestMapper;

    //Create a new album
    @Operation(
            summary = "Create a new album",
            description = "Allows an authenticated user to create a new album and associate songs with it.",
            tags = {"Album-User"},
            responses = { // mejor usar 'responses' que '@ApiResponses(value = {...})'
                    @ApiResponse(
                            responseCode = "201",
                            description = "Album created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlbumDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto dto) {
        String userId = getAuthenticatedUserId();
        dto.setUserId(userId);

        Album album = albumRestMapper.toDomain(dto);
        List<Long> songIds = dto.getSongIds();

        Album saved = albumServicePort.saveAlbumWithSongs(album, songIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(albumRestMapper.toDto(saved));
    }

    //Get all albums of the authenticated user
    @Operation(
            summary = "Get all albums of the authenticated user",
            description = "Returns the list of albums created by the currently authenticated user.",
            tags = {"Album-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Album list successfully obtained",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AlbumDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No albums found for the user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping("/mine")
    public ResponseEntity<List<AlbumDto>> getMyAlbums() {
        String userId = getAuthenticatedUserId();
        List<Album> albums = albumServicePort.findByUserId(userId);
        return ResponseEntity.ok(albums.stream().map(albumRestMapper::toDto).toList());
    }
    //Delete an album from the authenticated user
    @Operation(
            summary = "Delete an album from the authenticated user",
            description = "Allows the authenticated user to delete an album that belongs to them.",
            tags = {"Album-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Album successfully deleted"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The user does not have permission to delete this album",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Album not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable("id") Long id) {
        Album album = albumServicePort.getAlbumById(id);
        String userId = getAuthenticatedUserId();
        if (!album.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        albumServicePort.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
    //Add a song to an album
    @Operation(
            summary = "Add a song to an album",
            description = "Allows you to add an existing song to an album of the authenticated user.",
            tags = {"Album-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Song added to album successfully"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You do not have permission to edit this album.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Album or song not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping("/albums/{albumId}/add-song/{songId}")
    public ResponseEntity<Void> addSongToAlbum(@PathVariable("albumId") Long albumId, @PathVariable("songId") Long songId) {
        albumServicePort.addSongToAlbum(albumId, songId);
        return ResponseEntity.ok().build();
    }
    //Delete a song from an album
    @Operation(
            summary = "Eliminar una canción de un álbum",
            description = "Allows you to delete an existing song from an authenticated user's album.",
            tags = {"Album-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Song successfully removed from album"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You do not have permission to edit this album.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Album or song not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @DeleteMapping("/albums/{albumId}/remove-song/{songId}")
    public ResponseEntity<Void> removeSongFromAlbum(@PathVariable("albumId") Long albumId, @PathVariable("songId") Long songId) {
        albumServicePort.removeSongFromAlbum(albumId, songId);
        return ResponseEntity.noContent().build();
    }
    //Get songs from an album
    @Operation(
            summary = "Get songs from an album",
            description = "Returns the list of songs for a specific album of the authenticated user.",
            tags = {"Album-User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Song list successfully obtained",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SongDto.class)))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "You do not have permission to view the songs on this album.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Album not found or no songs",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping("/{albumId}/songs")
    public ResponseEntity<List<SongDto>> getSongsByAlbum(@PathVariable("albumId")  Long albumId) {
        String userId = getAuthenticatedUserId(); //sub token

        List<Song> songs = albumServicePort.getSongsByAlbumIdForUser(albumId, userId);
        return ResponseEntity.ok(songs.stream().map(songRestMapper::toDto).toList());
    }

    public String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
