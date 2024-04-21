package com.micro.demo.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UpdatePuedeDescargarPdfRequestDto {
    @NotNull
    private String nombrePrograma;

    @NotNull
    private boolean puedeDescargarPdf;
}

