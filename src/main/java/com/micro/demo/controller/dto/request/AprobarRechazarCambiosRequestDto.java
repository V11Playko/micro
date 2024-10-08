package com.micro.demo.controller.dto.request;

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
    @NotNull
    private String reasonMessage;
}
