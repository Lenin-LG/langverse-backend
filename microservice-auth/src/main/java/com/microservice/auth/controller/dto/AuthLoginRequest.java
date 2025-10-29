package com.microservice.auth.controller.dto;


public record AuthLoginRequest(String username, String password) {
}