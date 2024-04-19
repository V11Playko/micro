package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.dto.JwtTokenResponseDto;
import com.micro.demo.configuration.security.dto.LoginRequestDto;
import com.micro.demo.configuration.security.jwt.JwtUtils;
import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.service.IAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtTokenResponseDto loginUser(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getCorreo(), loginRequestDto.getContraseña()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtTokenResponseDto(jwt, userDetails.getUsername(), roles);
    }
}
