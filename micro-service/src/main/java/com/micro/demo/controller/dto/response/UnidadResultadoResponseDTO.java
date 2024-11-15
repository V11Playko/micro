package com.micro.demo.controller.dto.response;

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
public class UnidadResultadoResponseDTO {
    private List<ResultadoAprendizaje> resultados;
    private String tipoEvidencia;
    private String instrumentoEvaluacion;
    private String corteEvaluacion;
    private String criterioDesempeno;
    private boolean estatus;
}
