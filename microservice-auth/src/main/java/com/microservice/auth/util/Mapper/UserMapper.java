package com.microservice.auth.util.Mapper;

import com.microservice.auth.controller.dto.UserDTO;
import lombok.experimental.UtilityClass;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class UserMapper {
    public UserDTO toDto(UserRepresentation user, List<RoleRepresentation> roles) {
        return UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(rolesToNames(roles))
                .build();
    }

    private Set<String> rolesToNames(List<RoleRepresentation> roles) {
        return roles.stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toSet());
    }
}
