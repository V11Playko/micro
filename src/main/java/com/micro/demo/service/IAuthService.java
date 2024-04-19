package com.micro.demo.service;

import com.micro.demo.configuration.security.dto.JwtTokenResponseDto;
import com.micro.demo.configuration.security.dto.LoginRequestDto;

public interface IAuthService {
    JwtTokenResponseDto loginUser(LoginRequestDto loginRequestDto);
}
