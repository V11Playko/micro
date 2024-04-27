package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.AssignAsignaturasRequestDto;
import com.micro.demo.controller.dto.AssignDocentesRequestDTO;
import com.micro.demo.controller.dto.PageRequestDto;
import com.micro.demo.controller.dto.RemoveAsignaturaRequestDto;
import com.micro.demo.controller.dto.RemoveDocenteRequestDto;
import com.micro.demo.controller.dto.UpdatePeriodoModificacionRequestDto;
import com.micro.demo.controller.dto.UpdatePuedeDescargarPdfRequestDto;
import com.micro.demo.entities.AreaFormacion;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.PreRequisito;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.Usuario;
import com.micro.demo.service.IAreaFormacionService;
import com.micro.demo.service.IAsignaturaService;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.IPreRequisitoService;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.IUnidadService;
import com.micro.demo.service.IUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    public DirectorRestController(IUsuarioService usuarioService, IUnidadService unidadService, IProgramaAcademicoService programaAcademicoService, IPensumService pensumService, IAsignaturaService asignaturaService, IAreaFormacionService areaFormacionService, IPreRequisitoService preRequisitoService) {
        this.usuarioService = usuarioService;
        this.unidadService = unidadService;
        this.programaAcademicoService = programaAcademicoService;
        this.pensumService = pensumService;
        this.asignaturaService = asignaturaService;
        this.areaFormacionService = areaFormacionService;
        this.preRequisitoService = preRequisitoService;
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
        usuarioService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.USER_DELETED_MESSAGE));
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
    public ResponseEntity<List<Unidad>> getAllUnidades(){
        return ResponseEntity.ok(unidadService.getAllUnidad());
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
    public ResponseEntity<Map<String, String>> saveUnidad(@Valid @RequestBody Unidad unidad) {
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
        unidadService.deleteUnidad(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
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
    public ResponseEntity<List<Pensum>> getAllPensums(@Valid @RequestBody PageRequestDto pageRequestDto){
        return ResponseEntity.ok(pensumService.getAllPensum(pageRequestDto.getPagina(), pageRequestDto.getElementosXpagina()));
    }

    @Operation(summary = "Add a new pensum",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pensum created",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "409", description = "Pensum already exists",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @PostMapping("/savePensum")
    public ResponseEntity<Map<String, String>> savePensum(@Valid @RequestBody Pensum pensum) {
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
    public ResponseEntity<List<AreaFormacion>> getAllAreas(@Valid @RequestBody PageRequestDto pageRequestDto){
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
    public ResponseEntity<List<PreRequisito>> getAllPreRequisitos(@Valid @RequestBody PageRequestDto pageRequestDto){
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
        preRequisitoService.deletePrerequisito(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }
}
