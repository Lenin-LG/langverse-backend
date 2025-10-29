package com.microservice.notes.infrastructure.adapters.in.rest.controller;

import com.microservice.notes.application.ports.in.NoteBookServicePort;
import com.microservice.notes.domain.model.NoteBook;
import com.microservice.notes.infrastructure.adapters.in.rest.dto.NoteBookDto;
import com.microservice.notes.infrastructure.adapters.in.rest.mapper.NoteBookRestMapper;
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

@RestController
@RequestMapping("/notebook")
public class NoteBookController {
    @Autowired
    private NoteBookServicePort noteBookServicePort;
    @Autowired
    private NoteBookRestMapper noteBookRestMapper;
    //Get my notebooks
    @Operation(
            summary = "Get my notebooks",
            description = "Returns the list of all notebooks belonging to the authenticated user.",
            tags = {"Notebook"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of notebooks obtained successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = NoteBookDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user or invalid token",
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
    public ResponseEntity<List<NoteBookDto>> getMyNotebooks() {
        String userId = getAuthenticatedUserId();
        List<NoteBook> notebooks = noteBookServicePort.getMyNotebooks(userId);
        return ResponseEntity.ok(notebooks.stream().map(noteBookRestMapper::toDto).toList());
    }
    //Create a new notebook
    @Operation(
            summary = "Create a new notebook",
            description = "Allows the authenticated user to create a new notebook, automatically associating it with their account.",
            tags = {"Notebook"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Notebook created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteBookDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or incomplete data in the application",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user or invalid token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<NoteBookDto> createNotebook(@RequestBody NoteBookDto dto) {
        String userId = getAuthenticatedUserId();
        dto.setUserId(userId);


        NoteBook notebook = noteBookRestMapper.toDomain(dto);

        NoteBook saved = noteBookServicePort.createNotebook(notebook);

        NoteBookDto responseDto = noteBookRestMapper.toDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
    //delete a notebook
    @Operation(
            summary = "delete a notebook",
            description = "Allows the authenticated user to delete a notebook, automatically associating it with their account.",
            tags = {"Notebook"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Notebook deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoteBookDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or incomplete data in the application",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthenticated user or invalid token",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebook(@PathVariable("id") Long id) {
        String userId = getAuthenticatedUserId();
        noteBookServicePort.deleteNotebook(id, userId);
        return ResponseEntity.noContent().build();
    }

    private String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
