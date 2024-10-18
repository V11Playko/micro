package com.micro.demo.controller.dto;

import com.micro.demo.entities.EvaluacionResultadoAprendizaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ResultadoAprendizajeDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estatus;
}
