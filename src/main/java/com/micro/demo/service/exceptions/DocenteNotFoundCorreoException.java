package com.micro.demo.service.exceptions;

public class DocenteNotFoundCorreoException extends RuntimeException{
    private final String correo;

    public DocenteNotFoundCorreoException(String correo) {
        super("El docente con el correo '" + correo + "' no existe.");
        this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }
}
