package com.playko.auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String nombre;
    private String apellido;
    private String numeroCelular;
    private String correo;
    private String contraseña;
    private Role role;

    public User() {

    }
}
