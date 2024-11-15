package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class HistoryMovementDto {
    private Long id;
    private String correoDocente;
    private Long asignaturaAfectadaId;
    private Long pensumId;
    private Long programaAcademicoId;
    private Boolean cambiosAceptados;
    private boolean asignaturaAgregada;
    private boolean asignaturaActualizada;
    private boolean asignaturaRemovida;
    private Map<String, String> atributosModificados;
    private LocalDateTime fechaMovimiento;
    private Integer codigo;
}