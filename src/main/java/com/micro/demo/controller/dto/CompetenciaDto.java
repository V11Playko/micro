package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CompetenciaDto {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estatus;
    private String tipoCompetencia;
    private List<Long> resultados;
}
