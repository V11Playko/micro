package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.controller.dto.AreaFormacionDto;
import com.micro.demo.controller.dto.PensumDto;
import com.micro.demo.controller.dto.PreRequisitoDto;
import com.micro.demo.controller.dto.ResultadoAprendizajeDto;
import com.micro.demo.controller.dto.TemaDto;
import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.controller.dto.UsuarioDto;
import com.micro.demo.controller.dto.request.AprobarRechazarCambiosRequestDto;
import com.micro.demo.controller.dto.AsignaturaDto;
import com.micro.demo.controller.dto.request.assign.AssignAsignaturasRequestDto;
import com.micro.demo.controller.dto.request.assign.AssignCompetenciaRequestDto;
import com.micro.demo.controller.dto.request.assign.AssignDocentesRequestDTO;
import com.micro.demo.controller.dto.request.assign.AssignTemasRequestDto;
import com.micro.demo.controller.dto.CompetenciaDto;
import com.micro.demo.controller.dto.request.remove.RemoveAsignaturaRequestDto;
import com.micro.demo.controller.dto.request.remove.RemoveDocenteRequestDto;
import com.micro.demo.controller.dto.EvaluacionResultadoDto;
import com.micro.demo.controller.dto.request.update.UpdatePeriodoModificacionRequestDto;
import com.micro.demo.controller.dto.request.update.UpdatePuedeDescargarPdfRequestDto;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
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
import com.micro.demo.service.IEvaluacionResultadoService;
import com.micro.demo.service.IUnidadService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/director")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
public class DirectorRestController {

    private final IUsuarioService usuarioService;
    private final IUnidadService unidadService;
    private final IProgramaAcademicoService programaAcademicoService;
    private final IPensumService pensumService;
    private final IAsignaturaService asignaturaService;
    private final IAreaFormacionService areaFormacionService;
    private final IPreRequisitoService preRequisitoService;
    private final ITemaService temaService;
    private final IEvaluacionResultadoService evaluacionResultadoService;
    private final IResultadoAprendizajeService resultadoAprendizajeService;
    private final ICompetenciaService competenciaService;
    private final IPdfService pdfService;
    private final IHistoryMovementService historyMovementService;
    private final IUsuarioRepository usuarioRepository;

    public DirectorRestController(IUsuarioService usuarioService, IUnidadService unidadService, IProgramaAcademicoService programaAcademicoService, IPensumService pensumService, IAsignaturaService asignaturaService, IAreaFormacionService areaFormacionService, IPreRequisitoService preRequisitoService, ITemaService temaService, IEvaluacionResultadoService evaluacionResultadoService, IResultadoAprendizajeService resultadoAprendizajeService, ICompetenciaService competenciaService, IPdfService pdfService, IHistoryMovementService historyMovementService, IUsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.unidadService = unidadService;
        this.programaAcademicoService = programaAcademicoService;
        this.pensumService = pensumService;
        this.asignaturaService = asignaturaService;
        this.areaFormacionService = areaFormacionService;
        this.preRequisitoService = preRequisitoService;
        this.temaService = temaService;
        this.evaluacionResultadoService = evaluacionResultadoService;
        this.resultadoAprendizajeService = resultadoAprendizajeService;
        this.competenciaService = competenciaService;
        this.pdfService = pdfService;
        this.historyMovementService = historyMovementService;
        this.usuarioRepository = usuarioRepository;
    }

    public String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        throw new RuntimeException("Error obteniendo el correo del token.");
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
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        return ResponseEntity.ok(usuarioService.getAllUsers(pagina, elementosXpagina));
    }

    @Operation(summary = "Obtener usuario por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/getUser/{id}")
    public ResponseEntity<Usuario> getUser(@PathVariable Long id) {
        Usuario usuario = usuarioService.getUser(id);
        return ResponseEntity.ok(usuario);
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
    public ResponseEntity<Map<String, String>> saveDocente(@Valid @RequestBody UsuarioDto usuarioDTO) {
        usuarioDTO.setRoleId(3L);
        usuarioService.saveUser(usuarioDTO);
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
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody UsuarioDto usuarioDTO) {
        usuarioDTO.setRoleId(2L);
        usuarioService.updateUser(id,usuarioDTO);
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
    public ResponseEntity<Map<String, Object>> getAllProgramas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = programaAcademicoService.getAll(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener programa academico por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Programa academico encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Programa academico no encontrado")
    })
    @GetMapping("/getPrograma/{id}")
    public ResponseEntity<ProgramaAcademico> getPrograma(@PathVariable Long id) {
        ProgramaAcademico programaAcademico = programaAcademicoService.getPrograma(id);
        return ResponseEntity.ok(programaAcademico);
    }


    @Operation(summary = "Update periodo de modificacion",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Periodo de modificacion actualizado",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Periodo de modificacion already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updatePeriodoModificacion")
    public ResponseEntity<Map<String, String>> updatePeriodoModificacion(@Valid @RequestBody UpdatePeriodoModificacionRequestDto updatePeriodoModificacionRequestDto) {
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
    public ResponseEntity<Map<String, Object>> getAllPensums(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = pensumService.getAllPensum(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener pensum por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pensum encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Pensum no encontrado")
    })
    @GetMapping("/getPensum/{id}")
    public ResponseEntity<Pensum> getPensum(@PathVariable Long id) {
        Pensum pensum = pensumService.getPensum(id);
        return ResponseEntity.ok(pensum);
    }

    @Operation(summary = "Get all pensums no modificados durante un año")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pensums list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "Pensum already exists", content = @Content)
    })
    @GetMapping("/allPensumsNoModificadosDuranteUnAño")
    public ResponseEntity<Map<String, Object>> getPensumsNoModificadosDuranteUnAño(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = pensumService.getPensumsNoModificadosDuranteUnAño(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add a new pensum",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pensum created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Pensum already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/savePensum")
    public ResponseEntity<Map<String, String>> savePensum(@Valid @RequestBody PensumDto pensumDto) {
        pensumService.savePensum(pensumDto);
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
    public ResponseEntity<Map<String, String>> updatePensum(@PathVariable Long id, @RequestBody PensumDto pensumDto) {
        pensumService.updatePensum(id,pensumDto);
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
    public ResponseEntity<Map<String, Object>> getAllAsignaturas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = asignaturaService.getAllAsignatura(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener asignatura por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrado")
    })
    @GetMapping("/getAsignatura/{id}")
    public ResponseEntity<Asignatura> getAsignatura(@PathVariable Long id) {
        Asignatura asignatura = asignaturaService.getAsignatura(id);
        return ResponseEntity.ok(asignatura);
    }

    @Operation(summary = "Add a new Asignatura",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Asignatura created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Asignatura already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveAsignatura")
    public ResponseEntity<Map<String, Object>> saveAsignatura(@Valid @RequestBody AsignaturaDto asignaturaDto) {
        Long idAsignatura = asignaturaService.saveAsignatura(asignaturaDto); // Cambiar para devolver el ID
        Map<String, Object> response = new HashMap<>();
        response.put("IdAsignatura", idAsignatura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(summary = "Updated asignatura",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Asignatura update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Asignatura already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateAsignatura/{id}")
    public ResponseEntity<Map<String, String>> updateAsignatura(@PathVariable Long id, @RequestBody AsignaturaDto asignatura) {
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
    public ResponseEntity<Map<String, Object>> getAllAreas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = areaFormacionService.getAllAreaFormacion(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener area de formacion por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Area de formacion encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Area de formacion no encontrado")
    })
    @GetMapping("/getAreaFormacion/{id}")
    public ResponseEntity<AreaFormacion> getAreaFormacion(@PathVariable Long id) {
        AreaFormacion areaFormacion = areaFormacionService.getAreaFormacion(id);
        return ResponseEntity.ok(areaFormacion);
    }

    @Operation(summary = "Add a new Area de formacion",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Area de formacion created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Area de formacion already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveAreaFormacion")
    public ResponseEntity<Map<String, String>> saveAreaFormacion(@Valid @RequestBody AreaFormacionDto areaFormacion) {
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
    public ResponseEntity<Map<String, Object>> getAllPreRequisitos(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = preRequisitoService.getAllPreRequisito(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener prerequisito por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prerequisito encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Prerequisito no encontrado")
    })
    @GetMapping("/getPrerequisito/{id}")
    public ResponseEntity<PreRequisito> getPrerequisito(@PathVariable Long id) {
        PreRequisito preRequisito = preRequisitoService.getPreRequisito(id);
        return ResponseEntity.ok(preRequisito);
    }

    @Operation(summary = "Add a new PreRequisito",
            responses = {
                    @ApiResponse(responseCode = "201", description = "PreRequisito created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "PreRequisito already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/savePreRequisito")
    public ResponseEntity<Map<String, String>> savePreRequisito(@Valid @RequestBody PreRequisitoDto preRequisitoDto) {
        preRequisitoService.savePreRequisito(preRequisitoDto);
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
    public ResponseEntity<Map<String, Object>> getAllUnidades(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        return ResponseEntity.ok(unidadService.getAllUnidad(pagina, elementosXpagina));
    }

    @Operation(summary = "Obtener unidad por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Unidad encontrada", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Unidad no encontrada")
    })
    @GetMapping("/getUnidad/{id}")
    public ResponseEntity<Unidad> getUnidad(@PathVariable Long id) {
        return new ResponseEntity<>(unidadService.getUnidad(id), HttpStatus.OK);
    }

    @Operation(summary = "Add a new unidad",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Unidad created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Unidad already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveUnidad")
    public ResponseEntity<Map<String, String>> saveUnidad(@Valid @RequestBody List<UnidadDto> unidadesDto) {
        for (UnidadDto unidadDto : unidadesDto) {
            unidadService.saveUnidad(unidadDto);
        }
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
    public ResponseEntity<Map<String, String>> updateUnidad(@PathVariable Long id, @RequestBody UnidadDto unidadDto) {
        unidadService.updateUnidad(id,unidadDto);
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
    public ResponseEntity<Map<String, Object>> getAllTemas(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = temaService.getAllTemas(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener tema por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tema encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Tema no encontrado")
    })
    @GetMapping("/getTema/{id}")
    public ResponseEntity<Tema> getTema(@PathVariable Long id) {
        Tema tema = temaService.getTema(id);
        return ResponseEntity.ok(tema);
    }

    @Operation(summary = "Add a new Tema",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tema created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Tema already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveTema")
    public ResponseEntity<Map<String, String>> saveTema(@Valid @RequestBody TemaDto temaDto) {
        temaService.saveTema(temaDto);
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
    public ResponseEntity<Map<String, String>> updateTema(@PathVariable Long id, @Valid @RequestBody TemaDto temaDto) {
        temaService.updateTema(id, temaDto);
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
        temaService.deleteTema(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }


    /**
     *
     * EVALUACION RESULTADO
     *
     * **/
    @Operation(summary = "Get all Evaluaciones de Resultados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "evaluaciones de resultados list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "evaluaciones de resultados already exists", content = @Content)
    })
    @GetMapping("/allEvaluacionResultados")
    public ResponseEntity<Map<String, Object>> getAllUnidadResultados(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = evaluacionResultadoService.getAllEvaluacionResultados(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener Evaluacion Resultado por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "evaluacion de resultado encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "evaluacion de resultado no encontrado")
    })
    @GetMapping("/getEvaluacionResultado/{id}")
    public ResponseEntity<UnidadResultadoResponseDTO> getUnidadResultado(@PathVariable Long id) {
        UnidadResultadoResponseDTO unidadResultado = evaluacionResultadoService.getEvaluacionResultado(id);
        return ResponseEntity.ok(unidadResultado);
    }

    @Operation(summary = "Add a new evaluacion de resultado",
            responses = {
                    @ApiResponse(responseCode = "201", description = "evaluacion de resultado created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "evaluacion de resultado already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveEvaluacionResultado")
    public ResponseEntity<Map<String, String>> saveEvaluacionResultado(@Valid @RequestBody List<EvaluacionResultadoDto> evaluacionResultadoDtos) {
        evaluacionResultadoService.saveEvaluacionResultados(evaluacionResultadoDtos);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.CREATED_MESSAGE));
    }

    @Operation(summary = "Updated evaluacion de resultado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "evaluacion de resultado update",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "evaluacion de resultado already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PutMapping("/updateEvaluacionResultado/{id}")
    public ResponseEntity<Map<String, String>> updateEvaluacionResultado(@PathVariable Long id, @RequestBody EvaluacionResultadoDto evaluacionResultado) {
        evaluacionResultadoService.updateEvaluacionResultado(id, evaluacionResultado);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.UPDATED_MESSAGE));
    }

    @Operation(summary = "Deleted evaluacion de resultado",
            responses = {
                    @ApiResponse(responseCode = "200", description = "evaluacion de resultado deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "evaluacion de resultado not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteEvaluacionResultado/{id}")
    public ResponseEntity<Map<String, String>> deleteEvaluacionResultado(@PathVariable Long id) {
        evaluacionResultadoService.deleteEvaluacionResultado(id);
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
    public ResponseEntity<Map<String, Object>> getAllResultadosAprendizaje(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ) {
        Map<String, Object> response = resultadoAprendizajeService.getAllResultado(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener resultado de aprendizaje por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de aprendizaje encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Resultado de aprendizaje no encontrado")
    })
    @GetMapping("/getResultado/{id}")
    public ResponseEntity<ResultadoAprendizaje> getResultado(@PathVariable Long id) {
        ResultadoAprendizaje resultadoAprendizaje = resultadoAprendizajeService.getResultado(id);
        return ResponseEntity.ok(resultadoAprendizaje);
    }


    @Operation(summary = "Add a new resultados de aprendizaje",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Resultado de aprendizaje created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Resultado de aprendizaje already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveResultadoAprendizaje")
    public ResponseEntity<Map<String, String>> saveResultadosAprendizaje(@Valid @RequestBody ResultadoAprendizajeDto resultadoAprendizaje) {
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
    public ResponseEntity<Map<String, String>> updateResultadoAprendizaje(@PathVariable Long id, @RequestBody ResultadoAprendizajeDto resultadoAprendizaje) {
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
    public ResponseEntity<Map<String, Object>> getAllCompetencias(
            @RequestParam(required = false) Integer pagina,
            @RequestParam(required = false) Integer elementosXpagina
    ){
        Map<String, Object> response = competenciaService.getAllCompetencias(pagina, elementosXpagina);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener competencia por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Competencia encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Competencia no encontrado")
    })
    @GetMapping("/getCompetencia/{id}")
    public ResponseEntity<Competencia> getCompetencia(@PathVariable Long id) {
        Competencia competencia = competenciaService.getCompetencia(id);
        return ResponseEntity.ok(competencia);
    }

    @Operation(summary = "Add a new Competencia",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Competencia created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Competencia already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/saveCompetencia")
    public ResponseEntity<Map<String, String>> saveCompetencia(@Valid @RequestBody CompetenciaDto competenciaDto) {
        competenciaService.saveCompetencia(competenciaDto);
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
    public ResponseEntity<Map<String, String>> updateCompetencia(@PathVariable Long id, @RequestBody CompetenciaDto competencia) {
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

    @Operation(summary = "Aprobar o Rechazar cambios propuestos por los docentes",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cambios aprobados o rechazados",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Cambios already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/aprobarRechazarCambios")
    public ResponseEntity<Map<String, String>> aprobarRechazarCambios(@Valid @RequestBody AprobarRechazarCambiosRequestDto aprobarRechazarCambiosRequestDto) {
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
        historyMovementService.aplicarCambiosPropuestos(codigo);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.APPLIED_CHANGES_MESSAGE));
    }
}
