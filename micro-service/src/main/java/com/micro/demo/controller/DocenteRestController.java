package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.HistoryMovementDto;
import com.micro.demo.controller.dto.UsuarioDto;
import com.micro.demo.entities.HistoryMovement;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IHistoryMovementService;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IUsuarioService;
import com.micro.demo.service.exceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/docente")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCENTE')")
public class DocenteRestController {

    private final IUsuarioService usuarioService;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPdfService pdfService;
    private final IHistoryMovementService historyMovementService;


    public DocenteRestController(IUsuarioService usuarioService, IProgramaAcademicoService programaAcademicoService, IPdfService pdfService, IHistoryMovementService historyMovementService) {
        this.usuarioService = usuarioService;
        this.programaAcademicoService = programaAcademicoService;
        this.pdfService = pdfService;
        this.historyMovementService = historyMovementService;
    }

    /**
     *
     * USUARIOS
     *
     * **/
    @Operation(summary = "Updated user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody UsuarioDto usuarioDTO) {
        usuarioDTO.setRoleId(3L);
        usuarioService.updateUser(id,usuarioDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_UPDATED_MESSAGE));
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
    public ResponseEntity<Map<String, Object>> getAllProgramas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = programaAcademicoService.getAll(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
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


    /**
     *
     * PDF
     *
     * **/
    @Operation(summary = "Generate PDF",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF Generated",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Pdf not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/generatePdf/{pensumId}")
    public ResponseEntity<InputStreamResource> generatePdf(@PathVariable Long pensumId) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = pdfService.generatePdf(pensumId);

        String fileName = "ufpsPensum";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename="+ fileName +".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(byteArrayInputStream));
    }


    /**
     *
     * HISTORY MOVEMENT
     *
     * **/
    @Operation(summary = "Get all history movement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movements list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Movement already exists", content = @Content)
    })
    @GetMapping("/allHistoryMovement")
    public ResponseEntity<Map<String, Object>> getAllHistoryMovement(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = historyMovementService.getAllMovements(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener historial de movimiento por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial de movimiento encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Historial de movimiento no encontrado")
    })
    @GetMapping("/getHistoryMovement/{id}")
    public ResponseEntity<HistoryMovement> getHistoryMovement(@PathVariable Long id) {
        HistoryMovement historyMovement = historyMovementService.getHistoryMovement(id);
        return ResponseEntity.ok(historyMovement);
    }


    @Operation(summary = "Agregar asignatura, va al historial de movimiento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura agregada",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Asignatura not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/agregarAsignatura")
    public ResponseEntity<Map<String, String>> agregarAsignatura(@RequestBody HistoryMovementDto historyMovement) {
        historyMovementService.agregarAsignatura(historyMovement);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Remover asignatura, va al historial de movimiento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura removida",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Asignatura not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/removerAsignatura")
    public ResponseEntity<Map<String, String>> removerAsignatura(@RequestBody HistoryMovementDto historyMovement) {
        historyMovementService.removerAsignatura(historyMovement);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Actualizar asignatura, va al historial de movimiento",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura actualizada",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Asignatura not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/actualizarAsignatura")
    public ResponseEntity<Map<String, String>> actualizarAsignatura(@RequestBody HistoryMovementDto historyMovement) {
        historyMovementService.actualizarAsignatura(historyMovement);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }
}
