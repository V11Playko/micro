package com.micro.demo.controller.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserPageRequestDto {
    @Positive
    private int pagina;
    @Positive
    private int elementosXpagina;
}
