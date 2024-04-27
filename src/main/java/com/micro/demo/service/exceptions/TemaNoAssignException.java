package com.micro.demo.service.exceptions;

public class TemaNoAssignException extends RuntimeException{
    private final Long id;

    public TemaNoAssignException(Long id) {
        super("El tema con ID " + id + " no puede asignarse porque su estado es falso.");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
