package com.microservice.speaking.infrastructure.adapters.in.rest.controller;

import com.microservice.speaking.application.ports.in.TopicServicePort;
import com.microservice.speaking.infrastructure.adapters.in.rest.dto.TopicDto;
import com.microservice.speaking.infrastructure.adapters.in.rest.mapper.TopicRestMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {
    @Autowired
    private TopicServicePort topicServicePort;
    @Autowired
    private TopicRestMapper topicRestMapper;

    //Get all available themes
    @Operation(
            summary = "Get all available themes",
            description = "Returns a list of all topics registered in the system. This endpoint can " +
                    "be accessed by any authenticated user.",
            tags = {"Topics"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of topics obtained successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = TopicDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "There are no topics registered in the system."
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. The user is not logged in or their token has expired."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @GetMapping("/all")
    public List<TopicDto> getAllTopics() {
        return topicServicePort.getAllTopics().stream()
                .map(topicRestMapper::toDto)
                .toList();
    }
    //Create a new topic (administrators only)
    @Operation(
            summary = "Create a new topic (administrators only)",
            description = "Allows a user with administrator role to register a new topic in the system." +
                    "The topic may be related to phrases or speaking practices.",
            tags = {"Topics-admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Data of the new topic to be created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TopicDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Theme created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TopicDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. The subject data is incomplete or poorly formatted."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can create topics."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @PostMapping
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<TopicDto> createTopic(@RequestBody TopicDto dto) {
        var domain = topicRestMapper.toDomain(dto);
        var saved = topicServicePort.createTopic(domain);
        return ResponseEntity.ok(topicRestMapper.toDto(saved));
    }

    //Update an existing theme (administrators only)
    @Operation(
            summary = "Update an existing theme (administrators only))",
            description = "Allows a user with administrator role to modify the data of an existing theme," +
                    "as your name or description.",
            tags = {"Topics-admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Updated topic data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TopicDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Theme updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TopicDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The topic with the specified ID was not found."
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. The data submitted is incorrect or incomplete."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can update topics."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<TopicDto> updateTopic(@PathVariable Long id, @RequestBody TopicDto dto) {
        dto.setTopicId(id);
        var updated = topicServicePort.updateTopic(topicRestMapper.toDomain(dto));
        return ResponseEntity.ok(topicRestMapper.toDto(updated));
    }
    //Delete a topic (administrators only)
    @Operation(
            summary = "Delete a topic (administrators only)",
            description = "Allows a user with the administrator role to delete an existing topic by its ID." +
                    "This action is irreversible.",
            tags = {"Topics-admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Topic successfully deleted. No content returned in the reply."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "The topic with the specified ID was not found."
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access denied. Only administrators can delete topics."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error."
                    )
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicServicePort.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }
}
