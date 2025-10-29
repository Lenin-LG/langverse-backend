package com.microservice.speaking.infrastructure.adapters.in.rest.controller;

import com.microservice.speaking.application.ports.in.SpeakingPracticeServicePort;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.SpeakingPracticeDto;
import com.microservice.speaking.infrastructure.adapters.in.rest.mapper.SpeakingPracticeRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/practice")
public class SpeakingPracticeController {
    @Autowired
    private SpeakingPracticeServicePort speakingPracticeServicePort;
    @Autowired
    private SpeakingPracticeRestMapper speakingPracticeRestMapper;
    //Register a speaking practice of the authenticated user
    @Operation(
            summary = "Register a speaking practice of the authenticated user",
            description = "Allows the authenticated user to record a new pronunciation practice, including the " +
                    "practiced sentence, the recognized spoken text, and the calculated accuracy percentage.",
            tags = {"Speaking-Practice"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Speaking practice data to be recorded",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SpeakingPracticeDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Practice successfully recorded",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SpeakingPracticeDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Data is incomplete or poorly formatted."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. The user is not logged in or the token is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @PostMapping
    public ResponseEntity<SpeakingPracticeDto> savePractice(@RequestBody SpeakingPracticeDto dto) {
        String userId =getAuthenticatedUserId();

        dto.setUserId(userId);
        var practice = speakingPracticeRestMapper.toDomain(dto);
        var saved = speakingPracticeServicePort.savePractice(practice);
        return ResponseEntity.ok(speakingPracticeRestMapper.toDto(saved));
    }
    //Get all the authenticated user's speaking practices
    @Operation(
            summary = "Get all the authenticated user's speaking practices",
            description = "Returns a list of all pronunciation practices recorded by the user." +
                    "authenticated user, including information about the practiced phrase, spoken text and" +
                    " the precision obtained.",
            tags = {"Speaking-Practice"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of practices obtained correctly",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SpeakingPracticeDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. The user is not logged in or the token is invalid."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "No registered practices were found for the user."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @GetMapping("/me")
    public List<SpeakingPracticeDto> getPracticesByUser() {
        String userId = getAuthenticatedUserId();
        return speakingPracticeServicePort.getPracticesByUser(userId).stream()
                .map(speakingPracticeRestMapper::toDto)
                .toList();
    }
    private String getAuthenticatedUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
