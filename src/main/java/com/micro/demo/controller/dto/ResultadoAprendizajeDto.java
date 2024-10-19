package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResultadoAprendizajeDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estatus;
}
