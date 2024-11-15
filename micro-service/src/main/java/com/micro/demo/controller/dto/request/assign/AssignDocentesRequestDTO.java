package com.micro.demo.controller.dto.request.assign;

import jakarta.validation.constraints.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignDocentesRequestDTO {
    @NotNull
    private Long asignaturaId;
    @NotNull
    private List<String> correoDocentes;

}
