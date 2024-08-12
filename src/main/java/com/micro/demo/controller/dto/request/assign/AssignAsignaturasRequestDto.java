package com.micro.demo.controller.dto.request.assign;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AssignAsignaturasRequestDto {
    @NotNull
    private Long pensumId;
    @NotNull
    private List<Long> asignaturasId;
}
