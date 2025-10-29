package com.microservice.auth.controller;

import com.microservice.auth.controller.dto.AuthLoginRequest;
import com.microservice.auth.controller.dto.UserDTO;
import com.microservice.auth.service.IKeycloakService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private IKeycloakService iKeycloakService;

    /*
    * USER LAYER
    *
    * */
    //Obtain data from your profile
    @Operation(
            summary = "Get current user profile",
            description = "Retrieve the details of the currently authenticated user based on the access token.",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User profile retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class),
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"username\": \"admintest\",\n" +
                                                    "  \"email\": \"admintest@example.com\",\n" +
                                                    "  \"firstName\": \"John\",\n" +
                                                    "  \"lastName\": \"Doe\",\n" +
                                                    "  \"password\": null,\n" +
                                                    "  \"roles\": [\"offline_access\", \"default-roles-microservices-realm\", \"user\", \"uma_authorization\"]\n" +
                                                    "}"

                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or expired token"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        String userId = getUserIdFromToken();
        return ResponseEntity.ok(iKeycloakService.getUserById(userId));
    }

    //Update a user's data - user
    @Operation(
            summary = "Update current user profile",
            description = "Update the profile information of the currently authenticated user. The user ID is extracted from the access token.",
            tags = {"User"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User data to update",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"email\": \"adminTest@example.com\",\n" +
                                            "  \"firstName\": \"Lenin\",\n" +
                                            "  \"lastName\": \"Garcia\",\n" +
                                            "  \"password\": \"1234\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"User Update Successfully!\"")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad Request - invalid data"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or expired token"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Conflict - email already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"Conflict: user data already exists.\"")
                            )
                    )

            }
    )
    @PutMapping("/me")
    public ResponseEntity<?>  updateUser(@RequestBody UserDTO userDTO) {
        String userId = getUserIdFromToken();
        iKeycloakService.updateUser(userId, userDTO);
        return ResponseEntity.ok("User updated successfully!");
    }

    //Delete an entire user/account
    @Operation(
            summary = "Delete current user account",
            description = "Delete the account of the currently authenticated user. The user ID is extracted from the access token.",
            tags = {"User"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "User account deleted successfully (no content returned)"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or expired token"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - user not allowed to delete account"
                    )
            }
    )
    @DeleteMapping("/me")
    public ResponseEntity<?>  deleteUser() {
        String userId = getUserIdFromToken();
        iKeycloakService.deleteUser(userId);
        return  ResponseEntity.noContent().build();
    }

    //Register for the admin app - User
    @Operation(
            summary = "Register a new user",
            tags = {"Authentication"},
            description = "Create a new user account. This endpoint is typically used to register users in the system.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User data for registration",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDTO.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"username\": \"adminTest\",\n" +
                                            "  \"email\": \"adminTest@example.com\",\n" +
                                            "  \"firstName\": \"John\",\n" +
                                            "  \"lastName\": \"Doe\",\n" +
                                            "  \"password\": \"adminTest\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "user created successfully"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "User exist already"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "error due to the api"
                    )
            }
    )
    @PostMapping("/create")
    public ResponseEntity<?>  createUser(@RequestBody UserDTO userDTO) throws URISyntaxException {
        String response= iKeycloakService.createUser(userDTO);
        return  ResponseEntity.created(new URI("/auth/user/create")).body(response);
    }

    //Log in and return a user-admin token
    @Operation(
            summary = "Login User",
            description = "Authenticate a user with username and password and return a Keycloak access/refresh token.",
            tags = {"Authentication"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Login credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthLoginRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"username\": \"adminTest\",\n" +
                                            "  \"password\": \"adminTest\"\n" +
                                            "}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successful authentication",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = "{\n" +
                                                    "  \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6...\",\n" +
                                                    "  \"expires_in\": 300,\n" +
                                                    "  \"refresh_expires_in\": 1800,\n" +
                                                    "  \"refresh_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6...\",\n" +
                                                    "  \"token_type\": \"Bearer\",\n" +
                                                    "  \"not-before-policy\": 0,\n" +
                                                    "  \"session_state\": \"feecf4da-1c7b-42e9-924f-68b51a6b55bf\",\n" +
                                                    "  \"scope\": \"profile email\"\n" +
                                                    "}"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid username or password",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"Incorrect credentials\"")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - user account disabled or blocked",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = @ExampleObject(value = "\"User account is disabled or does not have access\"")
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest loginRequest) {
        Map<String, Object> token = iKeycloakService.login(
                loginRequest.username(),
                loginRequest.password()
        );
        return ResponseEntity.ok(token);
    }

    //Refresh token
    @Operation(
            summary = "Refresh Token",
            description = "Use a refresh token to obtain a new access token.",
            tags = {"User"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{\"refresh_token\": \"eyJhbGciOi...\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "New access token generated",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Missing or invalid refresh token"
                    )
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refresh_token");
        return ResponseEntity.ok(iKeycloakService.refreshToken(refreshToken));
    }

    //Activate or deactivate a user by ID
    @Operation(
            summary = "Enable or disable a user",
            tags = {"User"},
            description = "Enable or disable a user by ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"enabled\": true}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User status updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Missing parameters")
            }
    )
    @PutMapping("/enabled")
    public ResponseEntity<?> setUserEnabled( @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.getOrDefault("enabled", false);
        String userId = getUserIdFromToken();
        iKeycloakService.setUserEnabled(userId, enabled);
        return ResponseEntity.ok("Status updated successfully");
    }

    /*
     * ADMINISTRATOR LAYER
     *
     * */
    //List all users -admin
    @Operation(
            summary = "List all users",
            description = "Allows an admin to retrieve a list of all users registered in the system.",
            tags = {"Admin"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of users retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))
                            )
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden - only admins can access this endpoint")
            }
    )
    @GetMapping("/admin")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> getAllUsers(){
        return  ResponseEntity.ok(iKeycloakService.finAllUsers());
    }

    //Search for a user by their username - admin
    @Operation(
            summary = "Search user by username",
            description = "Allows an admin to search for a specific user by their username.",
            tags = {"Admin"},
            parameters = {
                    @Parameter(
                            name = "username",
                            description = "The username of the user to search",
                            required = true,
                            example = "lenin327"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "User not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - only admins can access this endpoint")
            }
    )
    @GetMapping("/admin/{username}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> getUserByUsername(@PathVariable("username") String username){
        return  ResponseEntity.ok(iKeycloakService.searchUserByUsername(username));
    }

    //Assign roles to a user - admin
    @Operation(
            summary = "Assign roles to the authenticated admin user",
            description = "Allows an admin to assign one or more roles to their own account. "
                    + "Roles must exist in Keycloak beforehand.",
            tags = {"Admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "List of roles to assign",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "[\"admin\", \"user\"]"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Roles assigned successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(responseCode = "403", description = "Forbidden - only admins can access this endpoint")
            }
    )
    @PostMapping("/admin/assign-roles/{userId}")
    @PreAuthorize("hasRole('admin_client_role')")
    public ResponseEntity<?> assignRoles( @RequestBody List<String> roles,@PathVariable("userId") String userId) {
        iKeycloakService.assignRoles(userId, roles);
        return ResponseEntity.ok("Roles assigned correctly");
    }

    //Activate a user if I deactivate their account
    @Operation(
            summary = "Enable or disable a user by admin",
            description = "Allows an admin to activate or deactivate any user account by providing the userId and the enabled flag.",
            tags = {"Admin"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Payload with the userId and status flag",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    example = "{ \"userId\": \"123e4567-e89b-12d3-a456-426614174000\", \"enabled\": true }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User status updated successfully",
                            content = @Content(mediaType = "application/json")
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request - userId is missing"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - only admins can access this endpoint")
            }
    )
    @PutMapping("/admin/enabled")
    public ResponseEntity<?> setUserEnabledByAdmin(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        boolean enabled = (Boolean) body.getOrDefault("enabled", false);

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("userId is required");
        }

        iKeycloakService.setUserEnabled(userId, enabled);
        return ResponseEntity.ok("User " + (enabled ? "enabled" : "disabled") + " correctly");
    }

    private String getUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getSubject();
    }
}
