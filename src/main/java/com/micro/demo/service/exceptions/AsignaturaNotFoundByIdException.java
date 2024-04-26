package com.micro.demo.service.exceptions;

public class AsignaturaNotFoundByIdException extends RuntimeException{
    private final Long id;

    public AsignaturaNotFoundByIdException(Long id) {
        super("La asignatura con el id'" + id + "' no existe.");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
