package com.microservice.auth.service.impl;

import com.microservice.auth.controller.dto.UserDTO;
import com.microservice.auth.service.IKeycloakService;
import com.microservice.auth.util.KeycloakProvider;
import com.microservice.auth.util.Mapper.UserMapper;
import jakarta.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class KeycloakServiceImpl implements IKeycloakService {
    @Value("${jwt.auth.converter.token-url}")
    private String TOKEN_URL;
    @Value("${jwt.auth.converter.token-validation}")
    private String TOKEN_VALIDATION;
    @Value("${jwt.auth.converter.resource-id}")
    private String CLIENT_ID;
    @Value("${jwt.auth.converter.client-secret}")
    private String CLIENT_SECRET;

    private final WebClient webClient;

    public KeycloakServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    /*
    *Method to list all keycloak users
    * @return List<UserRepresentation>
     * */
    @Override
    public List<UserRepresentation> finAllUsers() {
        return KeycloakProvider.getRealmResource()
                .users()
                .list();
    }
    /*
     *Method to list all users by username
     * @return List<UserRepresentation>
     * */
    @Override
    public List<UserRepresentation> searchUserByUsername(String username) {
        return KeycloakProvider.getRealmResource()
                .users()
                .searchByUsername(username,true);
    }
    /*
     *Method to create a new user in keycloak
     * @return String
     * */
    @Override
    public String createUser(@NonNull UserDTO userDTO) {

       int status=0;
        if (!userDTO.getUsername().matches("^[a-zA-Z0-9._-]{3,}$")) {
            return "Username must be alphanumeric and at least 3 characters.";
        }

        UsersResource usersResource=KeycloakProvider.getUsersResource();

        UserRepresentation userRepresentation=new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);

        Response response=usersResource.create(userRepresentation);
        status=response.getStatus();
        if(status == 201){
            String path=response.getLocation().getPath();
            String userId=path.substring(path.lastIndexOf("/")+1);

            CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
            credentialRepresentation.setTemporary(false);
            credentialRepresentation.setType(OAuth2Constants.PASSWORD);
            credentialRepresentation.setValue(userDTO.getPassword());

            usersResource.get(userId).resetPassword(credentialRepresentation);

            RealmResource realmResource=KeycloakProvider.getRealmResource();

            List<RoleRepresentation> roleRepresentations=null;

            if(userDTO.getRoles() ==null || userDTO.getRoles().isEmpty()){
                roleRepresentations= List.of(realmResource.roles().get("user").toRepresentation());
            }else{
                roleRepresentations= realmResource.roles()
                        .list()
                        .stream()
                        .filter(role -> userDTO.getRoles()
                                .stream()
                                .anyMatch( rolename -> rolename.equalsIgnoreCase(role.getName())))
                        .toList();
            }

            realmResource.users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(roleRepresentations);
            return "user created successfully!!";


        } else if (status ==409) {
            return  "User exist already!!";
        }else{
            return "Error creating user, please contact with the administrator";
        }
    }
    /*
     *Method to delete a user in keycloak
     * @return Void
     * */
    @Override
    public void deleteUser(String userId) {
        KeycloakProvider.getUsersResource()
                .get(userId)
                .remove();
    }
    /*
     *Method to update a user in keycloak
     * @return Void
     * */
    @Override
    public void updateUser(String userId, UserDTO userDTO) {
        CredentialRepresentation credentialRepresentation=new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(OAuth2Constants.PASSWORD);
        credentialRepresentation.setValue(userDTO.getPassword());

        UserRepresentation userRepresentation=new UserRepresentation();
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.setUsername(userDTO.getUsername());
        userRepresentation.setEmailVerified(true);
        userRepresentation.setEnabled(true);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));

        UserResource userResource=KeycloakProvider.getUsersResource().get(userId);
        userResource.update(userRepresentation);

    }
    /*
     * Method to authenticate a user in Keycloak using username and password
     * Sends a POST request to the token endpoint with the provided credentials
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return a Map containing the authentication response (access token, refresh token, etc.)
     * @throws RuntimeException if the credentials are invalid or the request fails
     */
    @Override
    public Map<String, Object> login(String username, String password) {
        try {
            return webClient.post()
                    .uri(TOKEN_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", CLIENT_ID)
                            .with("client_secret", CLIENT_SECRET)
                            .with("username", username)
                            .with("password", password))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Incorrect credentials");
        }
    }
    /*
     * Method to refresh an access token in Keycloak
     * Sends a POST request to the token endpoint using the provided refresh token
     *
     * @param refreshToken the refresh token previously obtained during login
     * @return a Map containing the new authentication response (access token, refresh token, etc.)
     */
    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        return webClient.post()
                .uri(TOKEN_URL)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", CLIENT_ID)
                        .with("client_secret", CLIENT_SECRET)
                        .with("refresh_token", refreshToken))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
    /*
     * Method to validate an access token in Keycloak
     * Sends a POST request to the token introspection endpoint to check if the token is valid
     *
     * @param token the access token to be validated
     * @return a Map containing the validation response (active status, expiration, user info, etc.)
     */
    @Override
    public Map<String, Object> validateToken(String token) {
        return webClient.post()
                .uri(TOKEN_VALIDATION)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters
                        .fromFormData("token", token)
                        .with("client_id", CLIENT_ID)
                        .with("client_secret", CLIENT_SECRET))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .block();
    }
    /*
     * Method to retrieve a user from Keycloak by their ID
     * Fetches the user representation and the list of effective realm-level roles,
     * then maps the data to a UserDTO
     *
     * @param userId the unique identifier of the user in Keycloak
     * @return a UserDTO containing user details and assigned roles
     */
    @Override
    public UserDTO getUserById(String userId) {
        UserRepresentation user = KeycloakProvider.getUsersResource().get(userId).toRepresentation();

        List<RoleRepresentation> roleList = KeycloakProvider
                .getRealmResource()
                .users()
                .get(userId)
                .roles()
                .realmLevel()
                .listEffective();

        return UserMapper.toDto(user, roleList);
    }
    /*
     * Method to assign realm-level roles to a user in Keycloak
     * Retrieves the role representations from the realm and assigns them to the specified user
     *
     * @param userId the unique identifier of the user in Keycloak
     * @param roles a list of role names to be assigned to the user
     * @return void
     */
    @Override
    public void assignRoles(String userId, List<String> roles) {
        RealmResource realm = KeycloakProvider.getRealmResource();
        List<RoleRepresentation> roleRepresentations = roles.stream()
                .map(roleName -> realm.roles().get(roleName).toRepresentation())
                .toList();

        realm.users().get(userId).roles().realmLevel().add(roleRepresentations);
    }
    /*
     * Method to enable or disable a user in Keycloak
     * Retrieves the user by ID, updates the "enabled" status, and persists the change
     *
     * @param userId the unique identifier of the user in Keycloak
     * @param enabled true to enable the user, false to disable the user
     * @return void
     */
    @Override
    public void setUserEnabled(String userId, boolean enabled) {
        UserResource userResource = KeycloakProvider.getUsersResource().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEnabled(enabled);
        userResource.update(user);
    }
}
