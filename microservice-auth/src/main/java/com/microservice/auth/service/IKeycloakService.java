package com.microservice.auth.service;

import com.microservice.auth.controller.dto.UserDTO;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Map;

public interface IKeycloakService {

    List<UserRepresentation> finAllUsers();
    List<UserRepresentation> searchUserByUsername(String username);
    String createUser(UserDTO userDTO);
    void deleteUser(String userId);
    void updateUser(String userId,UserDTO userDTO);
    Map<String, Object> login(String username, String password);
    Map<String, Object> refreshToken(String refreshToken);
    Map<String, Object> validateToken(String token);
    UserDTO getUserById(String userId);
    void assignRoles(String userId, List<String> roles);
    void setUserEnabled(String userId, boolean enabled);
}
