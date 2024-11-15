package com.micro.demo.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PreRequisitoDto {
    private Long id;
    private String credito;
    private Integer codigoAsignatura;
}