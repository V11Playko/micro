package com.micro.demo.configuration;

import com.micro.demo.service.exceptions.AllAsignaturasAssignsException;
import com.micro.demo.service.exceptions.AllDocentesAssignsException;
import com.micro.demo.service.exceptions.AreaFormacionNotFound;
import com.micro.demo.service.exceptions.AsignaturaAlreadyForAdd;
import com.micro.demo.service.exceptions.AsignaturaAlreadyInPensum;
import com.micro.demo.service.exceptions.AsignaturaAlreadyRemoved;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.AsignaturaNotFoundByIdException;
import com.micro.demo.service.exceptions.AsignaturaNotFoundExceptionInPensum;
import com.micro.demo.service.exceptions.AtributosNotFound;
import com.micro.demo.service.exceptions.CambiosAceptadosNotFoundException;
import com.micro.demo.service.exceptions.CompetenciaNotFoundException;
import com.micro.demo.service.exceptions.DirectorAlreadyAssignedException;
import com.micro.demo.service.exceptions.DirectorNotFoundException;
import com.micro.demo.service.exceptions.DocenteNotAssignException;
import com.micro.demo.service.exceptions.DocenteNotFound;
import com.micro.demo.service.exceptions.DocenteNotFoundCorreoException;
import com.micro.demo.service.exceptions.DuracionModificacionInvalidaException;
import com.micro.demo.service.exceptions.FakeEstatusNotAllowed;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.MessageNotSendException;
import com.micro.demo.service.exceptions.ModificationPeriodDisabled;
import com.micro.demo.service.exceptions.ModificationPeriodWorking;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.NotFoundUserUnauthorized;
import com.micro.demo.service.exceptions.PdfDownloadNotAllowedException;
import com.micro.demo.service.exceptions.PensumNotActiveException;
import com.micro.demo.service.exceptions.PensumNotFoundByIdException;
import com.micro.demo.service.exceptions.PensumNotFoundException;
import com.micro.demo.service.exceptions.PeriodoModificacionInvalidoException;
import com.micro.demo.service.exceptions.PreRequisitoNotFound;
import com.micro.demo.service.exceptions.ProgramaAcademicoExistenteException;
import com.micro.demo.service.exceptions.ProgramaNotFoundException;
import com.micro.demo.service.exceptions.ResultadoAprendizajeNotFoundException;
import com.micro.demo.service.exceptions.RoleNotFoundException;
import com.micro.demo.service.exceptions.TemaNoAssignException;
import com.micro.demo.service.exceptions.TemasNotFoundException;
import com.micro.demo.service.exceptions.TipoCursoIncorrectoException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import com.micro.demo.service.exceptions.UnidadNotFoundException;
import com.micro.demo.service.exceptions.UserAlreadyExistsException;
import com.micro.demo.service.exceptions.UserNotFoundException;
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

import static com.micro.demo.configuration.Constants.ALL_ASIGNATURAS_ASSIGNS_MESSAGE;
import static com.micro.demo.configuration.Constants.ALL_DOCENTES_ASSIGNS_MESSAGE;
import static com.micro.demo.configuration.Constants.APPLIED_CHANGES_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.AREA_FORMACION_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.ASIGNATURA_ALREADY_FOR_ADD;
import static com.micro.demo.configuration.Constants.ASIGNATURA_ALREADY_IN_PENSUM;
import static com.micro.demo.configuration.Constants.ASIGNATURA_ALREADY_REMOVED;
import static com.micro.demo.configuration.Constants.ASIGNATURA_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.ATRIBUTOS_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.COMPETENCIA_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.DIRECTOR_ALREADY_ASSIGN_MESSAGE;
import static com.micro.demo.configuration.Constants.DIRECTOR_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.DOCENTE_NOT_ASSIGN_MESSAGE;
import static com.micro.demo.configuration.Constants.DOCENTE_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.DURACION_INVALIDA_MESSAGE;
import static com.micro.demo.configuration.Constants.ESTATUS_FAKE_PENSUM_MESSAGE;
import static com.micro.demo.configuration.Constants.FAKE_ESTATUS_NOT_ALLOWED;
import static com.micro.demo.configuration.Constants.MESSAGE_NOT_SEND;
import static com.micro.demo.configuration.Constants.MODIFICATION_PERIOD_DISABLED;
import static com.micro.demo.configuration.Constants.MODIFICATION_PERIOD_WORKING;
import static com.micro.demo.configuration.Constants.NO_DATA_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.PAGINA_ILEGAL_MESSAGE;
import static com.micro.demo.configuration.Constants.PDF_DOWNLOAD_NOT_ALLOWED_MESSAGE;
import static com.micro.demo.configuration.Constants.PENSUM_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.PERIODO_MODIFICACION_INVALIDO_MESSAGE;
import static com.micro.demo.configuration.Constants.PRE_REQUISITO_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.PROGRAMA_EXISTENTE_MESSAGE;
import static com.micro.demo.configuration.Constants.PROGRAMA_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.RESPONSE_MESSAGE_KEY;
import static com.micro.demo.configuration.Constants.RESULTADO_APRENDIZAJE_NOT_FOUND;
import static com.micro.demo.configuration.Constants.ROLE_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.TEMAS_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.TIPO_CURSO_BAD_MESSAGE;
import static com.micro.demo.configuration.Constants.UNAUTHORIZED_MESSAGE;
import static com.micro.demo.configuration.Constants.UNIDAD_NOT_FOUND_MESSAGE;
import static com.micro.demo.configuration.Constants.USER_ALREADY_EXISTS_MESSAGE;
import static com.micro.demo.configuration.Constants.USER_NOT_FOUND_MESSAGE;
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

    @ExceptionHandler(ProgramaAcademicoExistenteException.class)
    public ResponseEntity<Map<String, String>> programaAcademicoExistenteException(
            ProgramaAcademicoExistenteException programaAcademicoExistenteException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PROGRAMA_EXISTENTE_MESSAGE));
    }

    @ExceptionHandler(DocenteNotFound.class)
    public ResponseEntity<Map<String, String>> docenteNotFound(
            DocenteNotFound docenteNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, DOCENTE_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(DocenteNotFoundCorreoException.class)
    public ResponseEntity<Map<String, String>> handleDocenteNotFoundCorreo
            (DocenteNotFoundCorreoException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PensumNotFoundByIdException.class)
    public ResponseEntity<Map<String, String>> handlePensumNotFoundById
            (PensumNotFoundByIdException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AsignaturaNotFoundExceptionInPensum.class)
    public ResponseEntity<Map<String, String>> handleAsignaturaNotFoundExceptionInPensum
            (AsignaturaNotFoundExceptionInPensum exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(AsignaturaNotFoundByIdException.class)
    public ResponseEntity<Map<String, String>> handleAsignaturaNotFoundByIdException
            (AsignaturaNotFoundByIdException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PensumNotFoundException.class)
    public ResponseEntity<Map<String, String>> pensumNotFoundException(
            PensumNotFoundException pensumNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PENSUM_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(AsignaturaNotFound.class)
    public ResponseEntity<Map<String, String>> asignaturaNotFound(
            AsignaturaNotFound asignaturaNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ASIGNATURA_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(AllAsignaturasAssignsException.class)
    public ResponseEntity<Map<String, String>> allAsignaturasAssignsException(
            AllAsignaturasAssignsException allAsignaturasAssignsException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ALL_ASIGNATURAS_ASSIGNS_MESSAGE));
    }

    @ExceptionHandler(AllDocentesAssignsException.class)
    public ResponseEntity<Map<String, String>> allDocentesAssignsException(
            AllDocentesAssignsException allDocentesAssignsException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ALL_DOCENTES_ASSIGNS_MESSAGE));
    }

    @ExceptionHandler(DocenteNotAssignException.class)
    public ResponseEntity<Map<String, String>> docenteNotAssignException(
            DocenteNotAssignException docenteNotAssignException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, DOCENTE_NOT_ASSIGN_MESSAGE));
    }

    @ExceptionHandler(AreaFormacionNotFound.class)
    public ResponseEntity<Map<String, String>> areaFormacionNotFound(
            AreaFormacionNotFound areaFormacionNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, AREA_FORMACION_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(PreRequisitoNotFound.class)
    public ResponseEntity<Map<String, String>> preRequisitoNotFound(
            PreRequisitoNotFound preRequisitoNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PRE_REQUISITO_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(TemasNotFoundException.class)
    public ResponseEntity<Map<String, String>> temasNotFoundException(
            TemasNotFoundException temasNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, TEMAS_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(UnidadNotFoundException.class)
    public ResponseEntity<Map<String, String>> unidadNotFoundException(
            UnidadNotFoundException unidadNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, UNIDAD_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(TemaNoAssignException.class)
    public ResponseEntity<Map<String, String>> handleTemaNoAssignException
            (TemaNoAssignException exception) {
        Map<String, String> response = new HashMap<>();
        response.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ResultadoAprendizajeNotFoundException.class)
    public ResponseEntity<Map<String, String>> resultadoAprendizajeNotFoundException(
            ResultadoAprendizajeNotFoundException resultadoAprendizajeNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, RESULTADO_APRENDIZAJE_NOT_FOUND));
    }

    @ExceptionHandler(CompetenciaNotFoundException.class)
    public ResponseEntity<Map<String, String>> competenciaNotFoundException(
            CompetenciaNotFoundException competenciaNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, COMPETENCIA_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(FakeEstatusNotAllowed.class)
    public ResponseEntity<Map<String, String>> fakeEstatusNotAllowed(
            FakeEstatusNotAllowed fakeEstatusNotAllowed) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, FAKE_ESTATUS_NOT_ALLOWED));
    }

    @ExceptionHandler(PensumNotActiveException.class)
    public ResponseEntity<Map<String, String>> pensumNotActiveException(
            PensumNotActiveException pensumNotActiveException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ESTATUS_FAKE_PENSUM_MESSAGE));
    }

    @ExceptionHandler(PdfDownloadNotAllowedException.class)
    public ResponseEntity<Map<String, String>> pdfDownloadNotAllowedException(
            PdfDownloadNotAllowedException pdfDownloadNotAllowedException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, PDF_DOWNLOAD_NOT_ALLOWED_MESSAGE));
    }

    @ExceptionHandler(AsignaturaAlreadyForAdd.class)
    public ResponseEntity<Map<String, String>> asignaturaAlreadyAddedToPensum(
            AsignaturaAlreadyForAdd asignaturaAlreadyForAdd) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ASIGNATURA_ALREADY_FOR_ADD));
    }

    @ExceptionHandler(AsignaturaAlreadyInPensum.class)
    public ResponseEntity<Map<String, String>> asignaturaAlreadyInPensum(
            AsignaturaAlreadyInPensum asignaturaAlreadyInPensum) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ASIGNATURA_ALREADY_IN_PENSUM));
    }

    @ExceptionHandler(AsignaturaAlreadyRemoved.class)
    public ResponseEntity<Map<String, String>> asignaturaAlreadyRemoved(
            AsignaturaAlreadyRemoved asignaturaAlreadyRemoved) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ASIGNATURA_ALREADY_REMOVED));
    }

    @ExceptionHandler(AtributosNotFound.class)
    public ResponseEntity<Map<String, String>> atributosNotFound(
            AtributosNotFound atributosNotFound) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, ATRIBUTOS_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(ModificationPeriodDisabled.class)
    public ResponseEntity<Map<String, String>> modificationPeriodDisabled(
            ModificationPeriodDisabled modificationPeriodDisabled) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, MODIFICATION_PERIOD_DISABLED));
    }

    @ExceptionHandler(CambiosAceptadosNotFoundException.class)
    public ResponseEntity<Map<String, String>> cambiosAceptadosNotFoundException(
            CambiosAceptadosNotFoundException cambiosAceptadosNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, APPLIED_CHANGES_NOT_FOUND_MESSAGE));
    }

    @ExceptionHandler(ModificationPeriodWorking.class)
    public ResponseEntity<Map<String, String>> modificationPeriodWorking(
            ModificationPeriodWorking modificationPeriodWorking) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, MODIFICATION_PERIOD_WORKING));
    }

    @ExceptionHandler(MessageNotSendException.class)
    public ResponseEntity<Map<String, String>> handleMessageNotSendException(
            MessageNotSendException messageNotSendException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, MESSAGE_NOT_SEND));
    }

    @ExceptionHandler(TipoCursoIncorrectoException.class)
    public ResponseEntity<Map<String, String>> tipoCursoIncorrectoException(
            TipoCursoIncorrectoException tipoCursoIncorrectoException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, TIPO_CURSO_BAD_MESSAGE));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> userNotFoundException(
            UserNotFoundException userNotFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap(RESPONSE_MESSAGE_KEY, USER_NOT_FOUND_MESSAGE));
    }
}
