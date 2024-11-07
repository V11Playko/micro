package com.playko.auth.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.playko.auth.client.UserClient;
import com.playko.auth.client.dto.User;
import com.playko.auth.dtos.TokenDto;
import com.playko.auth.dtos.UrlDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientSecret}")
    private String clientSecret;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMinutes;

    private final UserClient userClient;

    public AuthController(UserClient userClient) {
        this.userClient = userClient;
    }


    @GetMapping("/auth/url")
    public ResponseEntity<UrlDto> auth() {
        String url = new GoogleAuthorizationCodeRequestUrl(clientId,
                "http://localhost:4200",
                Arrays.asList(
                        "email",
                        "profile",
                        "openid")).build();

        return ResponseEntity.ok(new UrlDto(url));
    }

    @GetMapping("/auth/callback")
    public ResponseEntity<TokenDto> callback(@RequestParam("code") String code) throws URISyntaxException {
        GoogleTokenResponse tokenResponse;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    new GsonFactory(),
                    clientId,
                    clientSecret,
                    code,
                    "http://localhost:4200"
            ).execute();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String idTokenString = tokenResponse.getIdToken();
        String googleAccessToken = tokenResponse.getAccessToken();

        DecodedJWT decodedIdToken = JWT.decode(idTokenString);
        String email = decodedIdToken.getClaim("email").asString();

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User usuario;
        List<String> roles;
        try {
            usuario = userClient.getUser(email);
            roles = List.of(usuario.getRole().getNombre());
        } catch (Exception e) {
            usuario = new User();
            usuario.setCorreo(email);
            roles = List.of("ROLE_VISITANTE");
        }

        Date issuedAt = new Date();
        Date expirationTime = new Date(issuedAt.getTime() + jwtExpirationMinutes * 60 * 1000L);

        String jwtToken = Jwts.builder()
                .setSubject(usuario.getCorreo())
                .claim("roles", roles)
                .claim("google_access_token", googleAccessToken)
                .setIssuedAt(issuedAt)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();

        System.out.println(jwtToken);

        return ResponseEntity.ok(new TokenDto(googleAccessToken, jwtToken));
    }

}
