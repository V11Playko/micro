package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UnidadDto {

    private Long id;
    private String had;
    private String hti;
    private String nombre;
    private List<Long> temas;
    private List<Long> resultados;
    private Long asignatura;
}
