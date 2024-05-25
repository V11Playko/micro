package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.AprobarRechazarCambiosRequestDto;
import com.micro.demo.controller.dto.AssignAsignaturasRequestDto;
import com.micro.demo.controller.dto.AssignCompetenciaRequestDto;
import com.micro.demo.controller.dto.AssignDocentesRequestDTO;
import com.micro.demo.controller.dto.AssignTemasRequestDto;
import com.micro.demo.controller.dto.PageRequestDto;
import com.micro.demo.controller.dto.RemoveAsignaturaRequestDto;
import com.micro.demo.controller.dto.RemoveDocenteRequestDto;
import com.micro.demo.controller.dto.UpdatePeriodoModificacionRequestDto;
import com.micro.demo.controller.dto.UpdatePuedeDescargarPdfRequestDto;
import com.micro.demo.entities.AreaFormacion;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Competencia;
import com.micro.demo.entities.HistoryMovement;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.PreRequisito;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Tema;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAreaFormacionService;
import com.micro.demo.service.IAsignaturaService;
import com.micro.demo.service.ICompetenciaService;
import com.micro.demo.service.IHistoryMovementService;
import com.micro.demo.service.IPdfService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.IPreRequisitoService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IResultadoAprendizajeService;
import com.micro.demo.service.ITemaService;
import com.micro.demo.service.IUnidadResultadoService;
import com.micro.demo.service.IUnidadService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/director")
public class DirectorRestController {

    private final IUsuarioService usuarioService;
    private final IUnidadService unidadService;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPensumService pensumService;
    private final IAsignaturaService asignaturaService;
    private final IAreaFormacionService areaFormacionService;
    private final IPreRequisitoService preRequisitoService;
    private final ITemaService temaService;
    private final IUnidadResultadoService unidadResultadoService;
    private final IResultadoAprendizajeService resultadoAprendizajeService;
    private final ICompetenciaService competenciaService;
    private final IPdfService pdfService;
    private final IHistoryMovementService historyMovementService;
    private final IUsuarioRepository usuarioRepository;

    public DirectorRestController(IUsuarioService usuarioService, IUnidadService unidadService, IProgramaAcademicoService programaAcademicoService, IPensumService pensumService, IAsignaturaService asignaturaService, IAreaFormacionService areaFormacionService, IPreRequisitoService preRequisitoService, ITemaService temaService, IUnidadResultadoService unidadResultadoService, IResultadoAprendizajeService resultadoAprendizajeService, ICompetenciaService competenciaService, IPdfService pdfService, IHistoryMovementService historyMovementService, IUsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.unidadService = unidadService;
        this.programaAcademicoService = programaAcademicoService;
        this.pensumService = pensumService;
        this.asignaturaService = asignaturaService;
        this.areaFormacionService = areaFormacionService;
        this.preRequisitoService = preRequisitoService;
        this.temaService = temaService;
        this.unidadResultadoService = unidadResultadoService;
        this.resultadoAprendizajeService = resultadoAprendizajeService;
        this.competenciaService = competenciaService;
        this.pdfService = pdfService;
        this.historyMovementService = historyMovementService;
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
    public ResponseEntity<List<Usuario>> getAllUsers(@Valid @RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(usuarioService.getAllUsers(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new user docente",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveDocente")
    public ResponseEntity<Map<String, String>> saveDocente(@Valid @RequestBody Usuario user) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        usuarioService.saveUser(user, "ROLE_DOCENTE");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_CREATED_MESSAGE));
    }

    @Operation(summary = "Updated user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "User already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error"))),
                    @ApiResponse(responseCode = "403", description = "Role not allowed for user creation",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        usuarioService.updateUser(id,usuarioActualizado);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted user",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "User not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
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
            @ApiResponse(responseCode = "200", description = "Programas list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Programa already exists", content = @Content)
    })
    @GetMapping("/allProgramasAcademicos")
    public ResponseEntity<List<ProgramaAcademico>> getAllProgramas(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(programaAcademicoService.getAll(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Update periodo de modificacion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Periodo de modificacion actualizado",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Periodo de modificacion already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updatePeriodoModificacion")
    public ResponseEntity<Map<String, String>> updatePeriodoModificacion(@Valid @RequestBody UpdatePeriodoModificacionRequestDto updatePeriodoModificacionRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        programaAcademicoService.updatePeriodoModificacion(
                updatePeriodoModificacionRequestDto.getNombrePrograma(),
                updatePeriodoModificacionRequestDto.getFechaInicioModificacion(),
                updatePeriodoModificacionRequestDto.getDuracionModificacion()
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Update puedeDescargarPdf",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update puedeDescargarPdf",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Atributte PuedeDescargarPdf already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updatePuedeDescargarPdf")
    public ResponseEntity<Map<String, String>> updatePuedeDescargarPdf(@Valid @RequestBody UpdatePuedeDescargarPdfRequestDto updatePuedeDescargarPdfRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        programaAcademicoService.updatePuedeDescargarPdf(updatePuedeDescargarPdfRequestDto.getNombrePrograma(), updatePuedeDescargarPdfRequestDto.isPuedeDescargarPdf());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
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
    public ResponseEntity<List<Pensum>> getAllPensums(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(pensumService.getAllPensum(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Get all pensums no modificados durante un año")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pensums list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pensum already exists", content = @Content)
    })
    @GetMapping("/allPensumsNoModificadosDuranteUnAño")
    public ResponseEntity<List<Pensum>> getPensumsNoModificadosDuranteDosSemestres(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(pensumService.getPensumsNoModificadosDuranteUnAño(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new pensum",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pensum created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Pensum already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/savePensum")
    public ResponseEntity<Map<String, String>> savePensum(@Valid @RequestBody Pensum pensum) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.savePensum(pensum);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated pensum",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pensum update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Pensum already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updatePensum/{id}")
    public ResponseEntity<Map<String, String>> updatePensum(@PathVariable Long id, @RequestBody Pensum pensum) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.updatePensum(id,pensum);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Assign asignaturas",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignaturas update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Asignatura already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/assignAsignatura")
    public ResponseEntity<Map<String, String>> assignAsignaturas(@Valid @RequestBody AssignAsignaturasRequestDto asignaturasRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.assignAsignaturas(asignaturasRequestDto.getPensumId(), asignaturasRequestDto.getAsignaturasId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Asignatura removed",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura removed",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Asignatura not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/removeAsignaturaFromPensum")
    public ResponseEntity<Map<String, String>> removeAsignaturaFromPensum(@Valid @RequestBody RemoveAsignaturaRequestDto removeAsignaturaRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.removeAsignaturaFromPensum(removeAsignaturaRequestDto.getPensumId(), removeAsignaturaRequestDto.getAsignaturaId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }

    @Operation(summary = "Deleted pensum",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pensum deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Pensum not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deletePensum/{id}")
    public ResponseEntity<Map<String, String>> deletePensum(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.deletePensum(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }

    @Operation(summary = "Duplicate Pensum",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pensum Duplicate",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Pensum not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/duplicatePensum/{id}")
    public ResponseEntity<Map<String, String>> duplicatePensum(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pensumService.duplicatePensum(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }


    /**
     *
     * ASIGNATURA
     *
     * **/
    @Operation(summary = "Get all asignaturas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignaturas list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Asignatura already exists", content = @Content)
    })
    @GetMapping("/allAsignaturas")
    public ResponseEntity<List<Asignatura>> getAllAsignaturas(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(asignaturaService.getAllAsignatura(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new Asignatura",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Asignatura created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Asignatura already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveAsignatura")
    public ResponseEntity<Map<String, String>> saveAsignatura(@Valid @RequestBody Asignatura asignatura) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        asignaturaService.saveAsignatura(asignatura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated asignatura",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Asignatura already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateAsignatura/{id}")
    public ResponseEntity<Map<String, String>> updateAsignatura(@PathVariable Long id, @RequestBody Asignatura asignatura) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        asignaturaService.updateAsignatura(id,asignatura);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Assign Docentes",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Docentes updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Docente already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/assignDocentes")
    public ResponseEntity<Map<String, String>> assignDocentes(@Valid @RequestBody AssignDocentesRequestDTO assignDocentesRequestDTO) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        asignaturaService.assignDocentes(assignDocentesRequestDTO.getAsignaturaId(), assignDocentesRequestDTO.getCorreoDocentes());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Docente removed",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Docente removed",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Docente not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/removeDocentesFromAsignatura")
    public ResponseEntity<Map<String, String>> removeDocentesFromAsignatura(@Valid @RequestBody RemoveDocenteRequestDto removeDocenteRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        asignaturaService.removeDocente(removeDocenteRequestDto.getAsignaturaId(), removeDocenteRequestDto.getCorreoDocente());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }

    @Operation(summary = "Deleted asignatura",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Asignatura not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteAsignatura/{id}")
    public ResponseEntity<Map<String, String>> deleteAsignatura(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        asignaturaService.deleteAsignatura(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * AREA FORMACION
     *
     * **/
    @Operation(summary = "Get all areas de formacion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Areas de formacion list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Areas de formacion already exists", content = @Content)
    })
    @GetMapping("/allAreasFormacion")
    public ResponseEntity<List<AreaFormacion>> getAllAreas(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(areaFormacionService.getAllAreaFormacion(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new Area de formacion",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Area de formacion created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Area de formacion already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveAreaFormacion")
    public ResponseEntity<Map<String, String>> saveAreaFormacion(@Valid @RequestBody AreaFormacion areaFormacion) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        areaFormacionService.saveAreaFormacion(areaFormacion);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }
    @Operation(summary = "Deleted area de formacion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Area de formacion deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Area de formacion not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteAreaFormacion/{id}")
    public ResponseEntity<Map<String, String>> deleteAreaFormacion(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        areaFormacionService.deleteAreaFormacion(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * PRE REQUISITO
     *
     * **/
    @Operation(summary = "Get all pre-requisitos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PreRequisitos list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "PreRequisitos already exists", content = @Content)
    })
    @GetMapping("/allPreRequisitos")
    public ResponseEntity<List<PreRequisito>> getAllPreRequisitos(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(preRequisitoService.getAllPreRequisito(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new PreRequisito",
            responses = {
                    @ApiResponse(responseCode = "201", description = "PreRequisito created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "PreRequisito already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/savePreRequisito")
    public ResponseEntity<Map<String, String>> savePreRequisito(@Valid @RequestBody PreRequisito preRequisito) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        preRequisitoService.savePreRequisito(preRequisito);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }
    @Operation(summary = "Deleted PreRequisito",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PreRequisito deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "PreRequisito not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deletePreRequisito/{id}")
    public ResponseEntity<Map<String, String>> deletePreRequisito(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        preRequisitoService.deletePrerequisito(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * UNIDADES
     *
     * **/
    @Operation(summary = "Get all unidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Unidades list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Unidad already exists", content = @Content)
    })
    @GetMapping("/allUnidades")
    public ResponseEntity<List<Unidad>> getAllUnidades(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(unidadService.getAllUnidad(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Obtener unidad por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad encontrada", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    @GetMapping("/getUnidad/{id}")
    public ResponseEntity<Unidad> getUnidad(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return new ResponseEntity<>(unidadService.getUnidad(id), HttpStatus.OK);
    }

    @Operation(summary = "Add a new unidad",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Unidad created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Unidad already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveUnidad")
    public ResponseEntity<Map<String, String>> saveUnidad(@Valid @RequestBody Unidad unidad) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadService.saveUnidad(unidad);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated unidad",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Unidad already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateUnidad/{id}")
    public ResponseEntity<Map<String, String>> updateUnidad(@PathVariable Long id, @RequestBody Unidad unidad) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadService.updateUnidad(id,unidad);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted unidad",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Unidad not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteUnidad/{id}")
    public ResponseEntity<Map<String, String>> deleteUnidad(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadService.deleteUnidad(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * TEMAS
     *
     * **/
    @Operation(summary = "Get all temas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temas list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Tema already exists", content = @Content)
    })
    @GetMapping("/allTemas")
    public ResponseEntity<List<Tema>> getAllTemas(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(temaService.getAllTemas(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new Tema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tema created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Tema already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveTema")
    public ResponseEntity<Map<String, String>> saveTema(@Valid @RequestBody Tema tema) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        temaService.saveTema(tema);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated Tema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tema update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Tema already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateTema/{id}")
    public ResponseEntity<Map<String, String>> updateTema(@PathVariable Long id, @RequestBody Tema tema) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        temaService.updateTema(id,tema);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Assign Temas to Unidad",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Temas updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Temas already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/assignTemasToUnidad")
    public ResponseEntity<Map<String, String>> assignTemasToUnidad(@Valid @RequestBody AssignTemasRequestDto assignTemasRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        temaService.assignTemasToUnidad(assignTemasRequestDto.getUnidadId(), assignTemasRequestDto.getTemaIds());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted Tema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tema deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Tema not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteTema/{id}")
    public ResponseEntity<Map<String, String>> deleteTema(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        temaService.deleteTema(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * UNIDAD RESULTADO
     *
     * **/
    @Operation(summary = "Get all resultados de unidades")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de unidades list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Resultados de unidades already exists", content = @Content)
    })
    @GetMapping("/allUnidadResultados")
    public ResponseEntity<List<UnidadResultado>> getAllUnidadResultados(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(unidadResultadoService.getAllUnidadResultados(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new resultado de unidad",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resultado de unidad created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Resultado de unidad already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveUnidadResultado")
    public ResponseEntity<Map<String, String>> saveUnidadResultado(@Valid @RequestBody UnidadResultado unidadResultado) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadResultadoService.saveUnidadResultado(unidadResultado);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated Unidad Resultado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad Resultado update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Unidad Resultado already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateUnidadResultado/{id}")
    public ResponseEntity<Map<String, String>> updateUnidadResultado(@PathVariable Long id, @RequestBody UnidadResultado unidadResultado) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadResultadoService.updateUnidadResultado(id,unidadResultado);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted Unidad Resultado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Unidad Resultado deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Unidad Resultado not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteUnidadResultado/{id}")
    public ResponseEntity<Map<String, String>> deleteUnidadResultado(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        unidadResultadoService.deleteUnidadResultado(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * RESULTADOS DE APRENDIZAJE
     *
     * **/
    @Operation(summary = "Get all resultados de aprendizaje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultados de aprendizaje list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Resultados de aprendizaje already exists", content = @Content)
    })
    @GetMapping("/allResultadosAprendizaje")
    public ResponseEntity<List<ResultadoAprendizaje>> getAllResultadosAprendizaje(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(resultadoAprendizajeService.getAllResultado(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new resultados de aprendizaje",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resultado de aprendizaje created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Resultado de aprendizaje already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveResultadoAprendizaje")
    public ResponseEntity<Map<String, String>> saveResultadosAprendizaje(@Valid @RequestBody ResultadoAprendizaje resultadoAprendizaje) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        resultadoAprendizajeService.saveResultado(resultadoAprendizaje);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated resultado de aprendizaje",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultado aprendizaje update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Resultado aprendizaje already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateResultadoAprendizaje/{id}")
    public ResponseEntity<Map<String, String>> updateResultadoAprendizaje(@PathVariable Long id, @RequestBody ResultadoAprendizaje resultadoAprendizaje) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        resultadoAprendizajeService.updateResultado(id,resultadoAprendizaje);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted resultado de aprendizaje",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultado aprendizaje deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Resultado aprendizaje not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteResultadoAprendizaje/{id}")
    public ResponseEntity<Map<String, String>> deleteResultadoAprendizaje(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        resultadoAprendizajeService.deleteResultado(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * COMPETENCIAS
     *
     * **/
    @Operation(summary = "Get all Competencias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competencias list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Competencia already exists", content = @Content)
    })
    @GetMapping("/allCompetencias")
    public ResponseEntity<List<Competencia>> getAllCompetencias(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(competenciaService.getAllCompetencias(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new Competencia",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Competencia created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Competencia already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveCompetencia")
    public ResponseEntity<Map<String, String>> saveCompetencia(@Valid @RequestBody Competencia competencia) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        competenciaService.saveCompetencia(competencia);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated Competencia",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Competencia update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Competencia already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateCompetencia/{id}")
    public ResponseEntity<Map<String, String>> updateCompetencia(@PathVariable Long id, @RequestBody Competencia competencia) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        competenciaService.updateCompetencia(id,competencia);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Assign Competencias to ResultadoAprendizaje",
            responses = {
                    @ApiResponse(responseCode = "200", description = "CompetenciaResultado updated",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "CompetenciaResultado already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/assignCompetencias")
    public ResponseEntity<Map<String, String>> assignCompetenciasToResultadoAprendizaje(@Valid @RequestBody AssignCompetenciaRequestDto assignCompetenciaRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        resultadoAprendizajeService.assignCompetencia(assignCompetenciaRequestDto.getResultadoAprendizajeId(), assignCompetenciaRequestDto.getCompetenciaIds());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted Competencia",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Competencia deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Competencia not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteCompetencia/{id}")
    public ResponseEntity<Map<String, String>> deleteCompetencia(@PathVariable Long id) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        competenciaService.deleteCompetencia(id);
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
    public ResponseEntity<Map<String, String>> generatePdf(@PathVariable Long pensumId) throws IOException {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        pdfService.generatePdf(pensumId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
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
    public ResponseEntity<List<HistoryMovement>> getAllHistoryMovement(@Valid @RequestBody PageRequestDto pageRequestDto){
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        return ResponseEntity.ok(historyMovementService.getAllMovements(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Aprobar o Rechazar cambios propuestos por los docentes",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cambios aprobados o rechazados",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Cambios already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/aprobarRechazarCambios")
    public ResponseEntity<Map<String, String>> aprobarRechazarCambios(@Valid @RequestBody AprobarRechazarCambiosRequestDto aprobarRechazarCambiosRequestDto) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        historyMovementService.aprobarRechazarCambiosDespuesPeriodoModificacion(aprobarRechazarCambiosRequestDto.isAceptarCambios(), aprobarRechazarCambiosRequestDto.getCodigo(), aprobarRechazarCambiosRequestDto.getReasonMessage());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.APPLIED_CHANGES_MESSAGE));
    }

    @Operation(summary = "Aplicar cambios propuestos",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cambios propuestos aplicados",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Cambios already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/aplicarCambiosPropuestos")
    public ResponseEntity<Map<String, String>> aplicarCambiosPropuestos(@RequestParam Integer codigo) {
        checkUserRole(Arrays.asList("ROLE_DIRECTOR", "ROLE_ADMIN"));
        historyMovementService.aplicarCambiosPropuestos(codigo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.APPLIED_CHANGES_MESSAGE));
    }
}
