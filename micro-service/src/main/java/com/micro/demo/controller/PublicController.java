package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.UsuarioDto;
import com.micro.demo.controller.dto.record.MessageDto;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/public")
public class PublicController {

    private final IUsuarioService usuarioService;
    private final IPdfService pdfService;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPensumService pensumService;

    public PublicController(IUsuarioService usuarioService, IPdfService pdfService, IProgramaAcademicoService programaAcademicoService, IPensumService pensumService) {
        this.usuarioService = usuarioService;
        this.pdfService = pdfService;
        this.programaAcademicoService = programaAcademicoService;
        this.pensumService = pensumService;
    }

    @GetMapping("/messages")
    public ResponseEntity<MessageDto> publicMessages() {
        return ResponseEntity.ok(new MessageDto("public content"));
    }


    @Operation(summary = "Obtener usuario por correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/getUserByEmail")
    public ResponseEntity<Usuario> getUserByEmail(@RequestParam("correo") String correo) {
        return new ResponseEntity<>(usuarioService.getUserByCorreo(correo), HttpStatus.OK);
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
    @GetMapping("/programasAcademicos/{nombre}")
    public ResponseEntity<ProgramaAcademico> getProgramaByNombre(@PathVariable String nombre) {
        ProgramaAcademico programa = programaAcademicoService.getProgramaByNombre(nombre);
        return ResponseEntity.ok(programa);
    }

    /**
     *
     * PENSUM
     *
     * **/
    @Operation(summary = "Get all pensums")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pensums list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pensum already exists", content = @Content)
    })
    @GetMapping("/allPensums")
    public ResponseEntity<Map<String, Object>> getAllPensums(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = pensumService.getAllPensum(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }
}
