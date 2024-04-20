package com.micro.demo.controller;

import com.micro.demo.controller.dto.PageRequestDto;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.service.IProgramaAcademicoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/visitante")
public class VisitanteRestController {
    private final IProgramaAcademicoService programaAcademicoService;

    public VisitanteRestController(IProgramaAcademicoService programaAcademicoService) {
        this.programaAcademicoService = programaAcademicoService;
    }

    /**
     *
     * PROGRAMA ACADEMICO
     *
     * **/

    @Operation(summary = "Get all programas academicos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Programas list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Programa already exists", content = @Content)
    })
    @GetMapping("/allProgramasAcademicos")
    public ResponseEntity<List<ProgramaAcademico>> getAllProgramas(@Valid @RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(programaAcademicoService.getAll(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Get programa academico por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Programa returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Programa already exists", content = @Content)
    })
    @GetMapping("/{nombre}")
    public ResponseEntity<ProgramaAcademico> getProgramaByNombre(@PathVariable String nombre) {
        ProgramaAcademico programa = programaAcademicoService.getProgramaByNombre(nombre);
        return ResponseEntity.ok(programa);
    }
}
