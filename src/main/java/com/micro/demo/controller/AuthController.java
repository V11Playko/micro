package com.micro.demo.controller;

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
import com.micro.demo.controller.dto.TokenDto;
import com.micro.demo.controller.dto.UrlDto;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

@RestController
public class AuthController {

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientSecret}")
    private String clientSecret;


    private final IUsuarioRepository usuarioRepository;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMinutes;

    public AuthController(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
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
                    new NetHttpTransport(), new GsonFactory(),
                    clientId,
                    clientSecret,
                    code,
                    "http://localhost:4200"
            ).execute();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String idTokenString = tokenResponse.getIdToken();
        DecodedJWT decodedIdToken = JWT.decode(idTokenString);

        String email = decodedIdToken.getClaim("email").asString();

        if (email == null || !usuarioRepository.existsByCorreo(email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Usuario usuario = usuarioRepository.findByCorreo(email);

        // Crear un nuevo token con el tiempo de expiración extendido
        Date extendedExpirationTime = new Date(System.currentTimeMillis() + (long) jwtExpirationMinutes * 60 * 1000);

        JWTCreator.Builder tokenBuilder = JWT.create()
                .withExpiresAt(extendedExpirationTime);

        // Añadir todas las reclamaciones del token original
        Map<String, Claim> claims = decodedIdToken.getClaims();
        for (Map.Entry<String, Claim> entry : claims.entrySet()) {
            String key = entry.getKey();
            Claim claim = entry.getValue();
            if (claim.asString() != null) {
                tokenBuilder.withClaim(key, claim.asString());
            } else if (claim.asBoolean() != null) {
                tokenBuilder.withClaim(key, claim.asBoolean());
            } else if (claim.asInt() != null) {
                tokenBuilder.withClaim(key, claim.asInt());
            } else if (claim.asLong() != null) {
                tokenBuilder.withClaim(key, claim.asLong());
            } else if (claim.asDate() != null) {
                tokenBuilder.withClaim(key, claim.asDate());
            }
            // Añadir otros tipos de reclamaciones según sea necesario
        }

        // Añadir roles específicos del usuario
        tokenBuilder.withClaim("roles", usuario.getRole().getNombre());
        tokenBuilder.withClaim("access_token", tokenResponse.getAccessToken());

        String extendedToken = tokenBuilder.sign(Algorithm.HMAC256(jwtSecret));

        // Configurar el tokenResponse con el nuevo token y tiempo de expiración extendido
        tokenResponse.setIdToken(extendedToken);
        tokenResponse.setExpiresInSeconds(extendedExpirationTime.getTime());

        System.out.println(tokenResponse.getAccessToken());

        return ResponseEntity.ok(new TokenDto(tokenResponse.getAccessToken()));
    }

}