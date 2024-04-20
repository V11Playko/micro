package com.micro.demo.controller;

import com.micro.demo.configuration.Constants;
import com.micro.demo.controller.dto.UserPageRequestDto;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.Usuario;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/director")
public class DirectorRestController {

    private final IUsuarioService usuarioService;
    private final IUnidadService unidadService;

    public DirectorRestController(IUsuarioService usuarioService, IUnidadService unidadService) {
        this.usuarioService = usuarioService;
        this.unidadService = unidadService;
    }

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Users list returned", content = @Content),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content)
    })
    @GetMapping("/allUsers")
    public ResponseEntity<List<Usuario>> getAllUsers(@Valid @RequestBody UserPageRequestDto pageRequestDto){
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
                    @ApiResponse(responseCode = "201", description = "Unidad update",
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
                    @ApiResponse(responseCode = "201", description = "Unidad deleted",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Map"))),
                    @ApiResponse(responseCode = "404", description = "Unidad not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(ref = "#/components/schemas/Error")))})
    @DeleteMapping("/deleteUnidad/{id}")
    public ResponseEntity<Map<String, String>> deleteUnidad(@PathVariable Long id) {
        unidadService.deleteUnidad(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap(Constants.RESPONSE_MESSAGE_KEY, Constants.DELETED_MESSAGE));
    }
}
