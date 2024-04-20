package com.micro.demo.configuration;

import com.micro.demo.service.exceptions.DirectorAlreadyAssignedException;
import com.micro.demo.service.exceptions.DirectorNotFoundException;
import com.micro.demo.service.exceptions.DuracionModificacionInvalidaException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.NotFoundUserUnauthorized;
import com.micro.demo.service.exceptions.PeriodoModificacionInvalidoException;
import com.micro.demo.service.exceptions.ProgramaNotFoundException;
import com.micro.demo.service.exceptions.RoleNotFoundException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import com.micro.demo.service.exceptions.UserAlreadyExistsException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.micro.demo.configuration.Constants.DIRECTOR_ALREADY_ASSIGN_MESSAGE;
import static com.micro.demo.configuration.Constants.DIRECTOR_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.DURACION_INVALIDA_MESSAGE;
import static com.micro.demo.configuration.Constants.NO_DATA_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.PAGINA_ILEGAL_MESSAGE;
import static com.micro.demo.configuration.Constants.PERIODO_MODIFICACION_INVALIDO_MESSAGE;
import static com.micro.demo.configuration.Constants.PROGRAMA_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.RESPONSE_MESSAGE_KEY;
import static com.micro.demo.configuration.Constants.ROLE_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.UNAUTHORIZED_MESSAGE;
import static com.micro.demo.configuration.Constants.USER_ALREADY_EXISTS_MESSAGE;
import static com.micro.demo.configuration.Constants.USER_NOT_FOUND_UNAUTHORIZED_MESSAGE;

@ControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();

        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            errors.put(fieldName, errorMessage);
        }

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBindExceptions(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(NoDataFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoDataFoundException(
            NoDataFoundException noDataFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, NO_DATA_FOUND_MESSAGE));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> unauthorizedException(
            UnauthorizedException unauthorizedException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, UNAUTHORIZED_MESSAGE));
    }

    @ExceptionHandler(NotFoundUserUnauthorized.class)
    public ResponseEntity<Map<String, String>> notFoundUserUnauthorized(
            NotFoundUserUnauthorized notFoundUserUnauthorized) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, USER_NOT_FOUND_UNAUTHORIZED_MESSAGE));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExistsException(
            UserAlreadyExistsException userAlreadyExistsException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, USER_ALREADY_EXISTS_MESSAGE));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, String>> roleNotFoundException(
            RoleNotFoundException roleNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ROLE_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(IlegalPaginaException.class)
    public ResponseEntity<Map<String, String>> ilegalPaginaException(
            IlegalPaginaException ilegalPaginaException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PAGINA_ILEGAL_MESSAGE));
    }

    @ExceptionHandler(ProgramaNotFoundException.class)
    public ResponseEntity<Map<String, String>> programaNotFoundException(
            ProgramaNotFoundException programaNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PROGRAMA_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(DirectorNotFoundException.class)
    public ResponseEntity<Map<String, String>> directorNotFoundException(
            DirectorNotFoundException directorNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, DIRECTOR_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(DirectorAlreadyAssignedException.class)
    public ResponseEntity<Map<String, String>> directorAlreadyAssignedException(
            DirectorAlreadyAssignedException directorAlreadyAssignedException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, DIRECTOR_ALREADY_ASSIGN_MESSAGE));
    }

    @ExceptionHandler(DuracionModificacionInvalidaException.class)
    public ResponseEntity<Map<String, String>> duracionModificacionInvalidaException(
            DuracionModificacionInvalidaException duracionModificacionInvalidaException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, DURACION_INVALIDA_MESSAGE));
    }

    @ExceptionHandler(PeriodoModificacionInvalidoException.class)
    public ResponseEntity<Map<String, String>> periodoModificacionInvalidoException(
            PeriodoModificacionInvalidoException periodoModificacionInvalidoException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PERIODO_MODIFICACION_INVALIDO_MESSAGE));
    }
}
