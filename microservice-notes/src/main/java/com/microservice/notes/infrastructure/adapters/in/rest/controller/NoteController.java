package com.microservice.notes.infrastructure.adapters.in.rest.controller;

import com.microservice.notes.application.ports.in.NoteServicePort;
import com.microservice.notes.domain.model.Note;
import com.microservice.notes.infrastructure.adapters.in.rest.dto.NoteDto;
import com.microservice.notes.infrastructure.adapters.in.rest.mapper.NoteRestMapper;
import io.swagger.v3.oas.annotations.Operation;
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

@RestController
@RequestMapping("/note")
public class NoteController {
    @Autowired
    private NoteServicePort noteServicePort;
    @Autowired
    private NoteRestMapper noteRestMapper;
    //Get notes by notebook
    @Operation(
            summary = "Get notes by notebook",
            description = "Allows the authenticated user to obtain all notes associated with a specific notebook. "
                    + "Only notes belonging to the authenticated user are returned.",
            tags = {"Notes"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of notes obtained correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user or invalid token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "The user does not have access to this notebook",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Notebook or notes not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping("/notebook/{notebookId}")
    public ResponseEntity<List<NoteDto>> getNotesByNotebook(@PathVariable("notebookId") Long notebookId) {
        String userId = getAuthenticatedUserId();
        List<Note> notes = noteServicePort.getNotesByNotebook(notebookId, userId);
        return ResponseEntity.ok(notes.stream().map(noteRestMapper::toDto).toList());
    }

    //Get a note by ID
    @Operation(
            summary = "Get a note by ID",
            description = "Retrieves a specific note belonging to the authenticated user using their unique identifier.",
            tags = {"Notes"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Note obtained correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The note with the specified ID was not found or does not belong to the authenticated user"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — the user is not authenticated or the token is invalid"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable("id") Long id) {
        String userId = getAuthenticatedUserId();
        Note note = noteServicePort.getNoteById(id, userId);
        return ResponseEntity.ok(noteRestMapper.toDto(note));
    }

    //Create a new note
    @Operation(
            summary = "Create a new note",
            description = "Allows the authenticated user to create a new note associated with one of their notebooks.",
            tags = {"Notes"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Data of the note to be created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoteDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Note created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request — the data provided is incorrect or incomplete"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — the user is not authenticated or the token is invalid"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<NoteDto> createNote(@RequestBody NoteDto dto) {
        String userId = getAuthenticatedUserId();
        dto.setUserId(userId);
        Note note = noteRestMapper.toDomain(dto);
        Note saved = noteServicePort.createNote(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(noteRestMapper.toDto(saved));
    }
    //Update an existing note
    @Operation(
            summary = "Update an existing note",
            description = "Allows the authenticated user to update the content or metadata of a specific note."
                    + "Only the owner of the note can edit it..",
            tags = {"Notes"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated note data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoteDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Note updated correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request — incorrect or missing data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — the user is not authenticated or the token is invalid"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden — the user is not the owner of the note"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found — There is no note with the specified ID"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(@PathVariable("id") Long id, @RequestBody NoteDto dto) {
        String userId = getAuthenticatedUserId();
        dto.setId(id);
        dto.setUserId(userId);
        Note note = noteRestMapper.toDomain(dto);
        Note updated = noteServicePort.updateNote(note, userId);
        return ResponseEntity.ok(noteRestMapper.toDto(updated));
    }
    //Delete a note by ID
    @Operation(
            summary = "Delete a note by ID",
            description = "Allows the authenticated user to delete an existing note."
                    + "Only the owner of the note can delete it.",
            tags = {"Notes"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Note successfully deleted (no content returned)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized — the user is not authenticated or the token is invalid"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden — the user is not the owner of the note"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not Found — There is no note with the specified ID"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable("id") Long id) {
        String userId = getAuthenticatedUserId();
        noteServicePort.deleteNote(id, userId);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
