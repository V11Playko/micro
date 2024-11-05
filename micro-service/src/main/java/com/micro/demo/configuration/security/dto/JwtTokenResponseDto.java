package com.micro.demo.configuration.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class JwtTokenResponseDto {
    private String jwtToken;
    private String email;
    private List<String> roles;
}

