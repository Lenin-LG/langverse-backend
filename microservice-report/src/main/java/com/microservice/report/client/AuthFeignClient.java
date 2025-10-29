package com.microservice.report.client;

import com.microservice.report.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "MSVC-AUTH")
public interface AuthFeignClient {

    @GetMapping("/keycloak/search")
    List<UserDto> getAllUsers(@RequestHeader("Authorization") String token);
}
