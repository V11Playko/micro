package com.micro.demo.controller.dto.request.assign;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignTemasRequestDto {
    @NotNull
    private Long unidadId;
    private List<Long> temaIds;
}
