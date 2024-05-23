package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.PageRequestDto;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IUsuarioService;
import com.micro.demo.service.exceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/visitante")
public class VisitanteRestController {
    private final IUsuarioService usuarioService;
    private final IUsuarioRepository usuarioRepository;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPdfService pdfService;

    public VisitanteRestController(IUsuarioService usuarioService, IUsuarioRepository usuarioRepository, IProgramaAcademicoService programaAcademicoService, IPdfService pdfService) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.programaAcademicoService = programaAcademicoService;
        this.pdfService = pdfService;
    }

    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerTokenAuthentication) {
            BearerTokenAuthentication bearerTokenAuthentication = (BearerTokenAuthentication) authentication;
            return (String) bearerTokenAuthentication.getTokenAttributes().get("email");
        }
        return null;
    }

    private boolean isUserInAnyRole(String email, List<String> roles) {
        Usuario usuario = usuarioRepository.findByCorreo(email);
        if (usuario == null) {
            return false;
        }
        for (String role : roles) {
            if (usuario.getRole().getNombre().contains(role)) {
                return true;
            }
        }
        return false;
    }

    private void checkUserRole(List<String> requiredRoles) {
        String email = getCorreoUsuarioAutenticado();
        if (email == null || !isUserInAnyRole(email, requiredRoles)) {
            throw new UnauthorizedException();
        }
    }

    /**
     *
     * USUARIOS
     *
     * **/
    @Operation(summary = "Add a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveVisitante")
    public ResponseEntity<Map<String, String>> saveVisitante(@Valid @RequestBody Usuario user) {
        checkUserRole(Arrays.asList("ROLE_VISITANTE", "ROLE_ADMIN"));
        usuarioService.saveUser(user, "ROLE_VISITANTE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_CREATED_MESSAGE));
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
    public ResponseEntity<List<ProgramaAcademico>> getAllProgramas(@Valid @RequestBody PageRequestDto pageRequestDto) {
        checkUserRole(Arrays.asList("ROLE_VISITANTE", "ROLE_ADMIN"));
        return ResponseEntity.ok(programaAcademicoService.getAll(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Get programa academico por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Programa returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Programa already exists", content = @Content)
    })
    @GetMapping("/{nombre}")
    public ResponseEntity<ProgramaAcademico> getProgramaByNombre(@PathVariable String nombre) {
        checkUserRole(Arrays.asList("ROLE_VISITANTE", "ROLE_ADMIN"));
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
    public ResponseEntity<Map<String, String>> generatePdf(@PathVariable Long pensumId) throws IOException {
        checkUserRole(Arrays.asList("ROLE_VISITANTE", "ROLE_ADMIN"));
        pdfService.generatePdf(pensumId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }
}
