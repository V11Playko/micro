package com.micro.demo.service.exceptions;

public class PensumNotFoundByIdException extends RuntimeException{

    private final Long id;

    public PensumNotFoundByIdException(Long id) {
        super("El pensum con el id'" + id + "' no existe.");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
