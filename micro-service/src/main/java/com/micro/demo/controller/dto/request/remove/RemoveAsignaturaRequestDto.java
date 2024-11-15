package com.micro.demo.controller.dto.request.remove;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RemoveAsignaturaRequestDto {
    @NotNull
    private Long pensumId;
    @NotNull
    private Long asignaturaId;
}
