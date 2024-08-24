package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.request.assign.AssignDirectorRequestDto;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IUsuarioService;
import com.micro.demo.service.exceptions.UnauthorizedException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/admin")
public class AdminRestController {
    private final IUsuarioService usuarioService;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPdfService pdfService;
    private final IPensumService pensumService;
    private final IUsuarioRepository usuarioRepository;

    public AdminRestController(IUsuarioService usuarioService, IProgramaAcademicoService programaAcademicoService, IPdfService pdfService, IPensumService pensumService, IUsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.programaAcademicoService = programaAcademicoService;
        this.pdfService = pdfService;
        this.pensumService = pensumService;
        this.usuarioRepository = usuarioRepository;
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
    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Users list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @GetMapping("/allUsers")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam int pagina,
            @RequestParam int elementosXpagina){
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        return ResponseEntity.ok(usuarioService.getAllUsers(pagina, elementosXpagina));
    }

    @Operation(summary = "Obtener usuario por correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/getUser")
    public ResponseEntity<Usuario> getUserByEmail(@RequestParam("correo") String correo) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        return new ResponseEntity<>(usuarioService.getUserByCorreo(correo), HttpStatus.OK);
    }


    @Operation(summary = "Add a new user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveDirector")
    public ResponseEntity<Map<String, String>> saveDirector(@Valid @RequestBody Usuario user) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        usuarioService.saveUser(user, "ROLE_DIRECTOR");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_CREATED_MESSAGE));
    }

    @Operation(summary = "Updated user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        usuarioService.updateUser(id,usuarioActualizado);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        usuarioService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_DELETED_MESSAGE));
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
            @RequestParam int pagina,
            @RequestParam int elementosXpagina
    ) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
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
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        ProgramaAcademico programa = programaAcademicoService.getProgramaByNombre(nombre);
        return ResponseEntity.ok(programa);
    }

    @Operation(summary = "Add a new programa academico",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Programa created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Programa already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveProgramaAcademico")
    public ResponseEntity<Map<String, String>> saveProgramaAcademico(@Valid @RequestBody ProgramaAcademico programaAcademico) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        programaAcademicoService.saveProgramaAcademico(programaAcademico);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Assign Director",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Assign Director",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Director already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/assignDirector")
    public ResponseEntity<Map<String, String>> assignDirector(@Valid @RequestBody AssignDirectorRequestDto assignDirectorRequestDto) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        programaAcademicoService.assignDirector(assignDirectorRequestDto.getCorreoDirector(), assignDirectorRequestDto.getNombrePrograma());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted programa academico",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Programa deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Programa not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteProgramaAcademico/{id}")
    public ResponseEntity<Map<String, String>> deleteProgramaAcademico(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        programaAcademicoService.deleteProgramaAcademico(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
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
        checkUserRole(Arrays.asList("ROLE_ADMIN"));

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
     *
     * PENSUM
     *
     **/
    @Operation(summary = "Get all pensums no modificados durante un año")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pensums list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pensum already exists", content = @Content)
    })
    @GetMapping("/allPensumsNoModificadosDuranteUnAño")
    public ResponseEntity<Map<String, Object>> getPensumsNoModificadosDuranteUnAño(
            @RequestParam int pagina,
            @RequestParam int elementosXpagina
    ) {
        checkUserRole(Arrays.asList("ROLE_ADMIN"));
        Map<String, Object> response = pensumService.getPensumsNoModificadosDuranteUnAño(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

}
