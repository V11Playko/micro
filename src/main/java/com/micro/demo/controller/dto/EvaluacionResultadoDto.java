package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluacionResultadoDto {
    private Long id;
    private String tipoEvidencia;
    private String instrumentoEvaluacion;
    private String criterioDesempeno;
    private String corteEvaluacion;
    private boolean estatus;
    private List<Long> unidades;
}
