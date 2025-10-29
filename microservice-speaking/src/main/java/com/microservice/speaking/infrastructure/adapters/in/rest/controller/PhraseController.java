package com.microservice.speaking.infrastructure.adapters.in.rest.controller;

import com.microservice.speaking.application.ports.in.PhraseServicePort;
import com.microservice.speaking.domain.model.Phrase;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.PhraseDto;
import com.microservice.speaking.infrastructure.adapters.in.rest.mapper.PhraseRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/phrase")
@RestController
public class PhraseController {
    @Autowired
    private PhraseServicePort phraseServicePort;
    @Autowired
    private PhraseRestMapper phraseRestMapper;
    //Get all phrases by topic
    @Operation(
            summary = "Get all phrases by topic",
            description = "Retrieves the complete list of phrases associated with a specific topic, "
                    + "identified by its ID. Each sentence belongs to a topic within the speaking practice.",
            tags = {"Phrases"},
            parameters = {
                    @Parameter(
                            name = "topicId",
                            description = "Unique identifier of the topic from which you want to obtain the phrases.",
                            required = true,
                            example = "5"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of phrases obtained correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PhraseDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No phrases were found for the specified topic"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @GetMapping("/topic/{topicId}")
    public List<PhraseDto> getPhrasesByTopic(@PathVariable ("topicId") Long topicId) {
        return phraseServicePort.getAllPhrasesByTopicId(topicId).stream()
                .map(phraseRestMapper::toDto)
                .toList();
    }
    //Get a phrase by ID
    @Operation(
            summary = "Get a phrase by ID",
            description = "Returns the complete data for a specific phrase based on its unique identifier. "
                    + "Allows you to consult details such as the original text and its translation associated with the corresponding topic.",
            tags = {"Phrases"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique identifier of the phrase you wish to query.",
                            required = true,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Phrase found and returned correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PhraseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No phrase was found with the specified ID"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @GetMapping("/{id}")
    public PhraseDto getPhraseById(@PathVariable Long id) {
        return phraseRestMapper.toDto(
                phraseServicePort.getPhraseById(id)
        );
    }
    //Create a new phrase (administrators only)
    @Operation(
            summary = "Create a new phrase (administrators only)",
            description = "Allows a user with an administrator role to register a new phrase in the system. "
                    + "Each sentence can be associated with a topic and contain text in different languages.",
            tags = {"Phrases-admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Data of the new phrase to be registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PhraseDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Phrase created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PhraseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can create phrases."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Required fields are missing or the data is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @PostMapping("/create")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<PhraseDto> createPhrase(@RequestBody PhraseDto dto) {
        Phrase phrase = phraseRestMapper.toDomain(dto);
        Phrase saved = phraseServicePort.createPhrase(phrase);
        return ResponseEntity.ok(phraseRestMapper.toDto(saved));
    }
    //Update an existing phrase (administrators only)
    @Operation(
            summary = "Update an existing phrase (administrators only)",
            description = "Allows a user with administrator role to modify the data of an existing phrase.",
            tags = {"Phrases-admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated phrase data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PhraseDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sentence updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PhraseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The phrase with the specified ID was not found"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can update phrases."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Data is incomplete or poorly formatted."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error"
                    )
            }
    )
    @PostMapping("/update")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<PhraseDto> updatePhrase(@PathVariable Long id, @RequestBody PhraseDto dto) {
        dto.setPhraseId(id);
        Phrase phrase = phraseRestMapper.toDomain(dto);
        Phrase updated = phraseServicePort.updatePhrase(phrase);
        return ResponseEntity.ok(phraseRestMapper.toDto(updated));
    }
    //Delete an existing phrase (administrators only)
    @Operation(
            summary = "Delete an existing phrase (administrators only)",
            description = "Allows a user with an administrator role to delete a specific phrase using its ID.",
            tags = {"Phrases-admin"},
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID of the phrase you want to delete",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Sentence successfully deleted. No content returned in the response."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The phrase with the specified ID was not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can delete phrases."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. The ID provided is not valid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<Void> deletePhrase(@PathVariable Long id) {
        phraseServicePort.deletePhrase(id);
        return ResponseEntity.noContent().build();
    }
}
