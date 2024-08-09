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
public class RemoveDocenteRequestDto {

    @NotNull(message = "El ID de la asignatura no puede ser nulo")
    private Long asignaturaId;

    @NotNull(message = "El correo del docente no puede ser nulo")
    private String correoDocente;
}
