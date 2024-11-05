package com.micro.demo.configuration.security.exception;


public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }
}
