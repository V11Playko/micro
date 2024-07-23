package com.micro.demo.controller.dto;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.ResultadoAprendizaje;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UnidadResultadoDTO {
    private String tipoEvidencia;
    private String instrumentoEvaluacion;
    private String corteEvaluacion;
    private boolean estatus;
    private List<ResultadoAprendizaje> resultados;
    private Asignatura asignatura;
}
