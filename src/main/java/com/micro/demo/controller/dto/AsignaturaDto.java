package com.micro.demo.controller.dto;

import com.micro.demo.entities.enums.Semestre;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AsignaturaDto {

    private Long id;
    private String nombre;
    private Integer codigo;
    private String accFormacionInv;
    private String bibliografia;
    private Integer creditos;
    private String hp;
    private String ht;
    private String had;
    private String hti;
    private String hadhti;
    private String justificacion;
    private String metodologia;
    private List<String> objetivos;
    private Semestre semestre;
    private String tipoCredito;
    private String tipoCurso;
    private String asignaturaSucesora;
    private String asignaturaPredecesora;
    private Long areaFormacionId;
    private Long competenciaId;
    private List<Long> preRequisitosIds;
}
