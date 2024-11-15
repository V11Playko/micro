package com.micro.demo.service.exceptions;

public class AsignaturaNotFoundExceptionInPensum extends RuntimeException {
    private final Long asignaturaId;
    private final Long pensumId;

    public AsignaturaNotFoundExceptionInPensum(Long asignaturaId, Long pensumId) {
        super("La asignatura con el ID '" + asignaturaId + "' no se encontró en el pensum con el ID '" + pensumId + "'.");
        this.asignaturaId = asignaturaId;
        this.pensumId = pensumId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public Long getPensumId() {
        return pensumId;
    }
}

