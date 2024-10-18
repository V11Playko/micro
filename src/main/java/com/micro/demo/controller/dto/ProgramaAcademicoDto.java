package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ProgramaAcademicoDto {
    private Long id;
    private String nombre;
    private Boolean puedeDescargarPdf;
    private LocalDate fechaInicioModificacion;
    private Integer duracionModificacion;
    private String correoDirector;
}
