package com.micro.demo.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AprobarRechazarCambiosRequestDto {

    @NotNull
    private boolean aceptarCambios;
    @NotNull
    private Integer codigo;
}
