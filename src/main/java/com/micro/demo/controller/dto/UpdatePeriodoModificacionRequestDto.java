package com.micro.demo.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePeriodoModificacionRequestDto {
    @NotNull
    private String nombrePrograma;

    @NotNull
    private LocalDate fechaInicioModificacion;

    @NotNull
    @Positive
    private Integer duracionModificacion;
}
