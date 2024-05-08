package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IProgramaAcademicoRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IProgramaAcademicoService;
import com.micro.demo.service.exceptions.DirectorAlreadyAssignedException;
import com.micro.demo.service.exceptions.DirectorNotFoundException;
import com.micro.demo.service.exceptions.DuracionModificacionInvalidaException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PeriodoModificacionInvalidoException;
import com.micro.demo.service.exceptions.ProgramaAcademicoExistenteException;
import com.micro.demo.service.exceptions.ProgramaNotFoundException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ProgramaAcademicoService implements IProgramaAcademicoService {
    private final IUsuarioRepository usuarioRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;

    public ProgramaAcademicoService(IUsuarioRepository usuarioRepository, IProgramaAcademicoRepository programaAcademicoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
    }

    @Override
    public List<ProgramaAcademico> getAll(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<ProgramaAcademico> paginaProgramas =
                programaAcademicoRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaProgramas.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaProgramas.getContent();
    }

    @Override
    public ProgramaAcademico getProgramaByNombre(String nombre) {
        ProgramaAcademico programa = programaAcademicoRepository.findByNombre(nombre);
        if (programa == null) throw new ProgramaNotFoundException();
        return programa;
    }

    @Override
    public void saveProgramaAcademico(ProgramaAcademico programaAcademico) {
        ProgramaAcademico existingPrograma = programaAcademicoRepository.findByNombre(programaAcademico.getNombre());
        if (existingPrograma != null) {
            throw new ProgramaAcademicoExistenteException();
        }

        programaAcademico.setDirector(null);
        programaAcademico.setPuedeDescargarPdf(false);
        programaAcademico.setFechaInicioModificacion(null);
        programaAcademico.setDuracionModificacion(null);

        programaAcademicoRepository.save(programaAcademico);
    }

    @Override
    public void assignDirector(String correoDirector, String nombrePrograma) {
        ProgramaAcademico programa = programaAcademicoRepository.findByNombre(nombrePrograma);
        if (programa == null) {
            throw new ProgramaNotFoundException();
        }

        Usuario director = usuarioRepository.findByCorreo(correoDirector);
        if (director == null || !director.getRole().getNombre().equals("ROLE_DIRECTOR")) {
            throw new DirectorNotFoundException();
        }

        ProgramaAcademico programaAsignado = programaAcademicoRepository.findByDirector(director);
        if (programaAsignado != null) {
            throw new DirectorAlreadyAssignedException();
        }

        programa.setDirector(director);
        programaAcademicoRepository.save(programa);
    }

    /**
     *
     * Por si no lo entiendes :
     * - Verificar si el usuario autenticado es el director del programa.
     * - El periodo de modificación ya está en curso, solo agregamos más días.
     * - Establecemos los valores proporcionados como inicio del periodo de modificación.
     **/
    @Override
    public void updatePeriodoModificacion(String nombrePrograma, LocalDate fechaInicioModificacion, Integer duracionModificacion) {
        ProgramaAcademico programa = programaAcademicoRepository.findByNombre(nombrePrograma);
        if (programa == null) {
            throw new ProgramaNotFoundException();
        }

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (!programa.getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        LocalDate fechaInicioExistente = programa.getFechaInicioModificacion();
        Integer duracionExistente = programa.getDuracionModificacion();

        if (fechaInicioExistente != null && duracionExistente != null) {
            if (duracionModificacion == null || duracionModificacion <= 1) {
                throw new DuracionModificacionInvalidaException();
            }
            programa.setDuracionModificacion(duracionExistente + duracionModificacion);
        } else {
            if (fechaInicioModificacion == null || duracionModificacion == null || duracionModificacion <= 0) {
                throw new PeriodoModificacionInvalidoException();
            }
            fechaInicioModificacion = LocalDate.now();
            programa.setFechaInicioModificacion(fechaInicioModificacion);
            programa.setDuracionModificacion(duracionModificacion);
        }

        programaAcademicoRepository.save(programa);
    }

    @Override
    public void updatePuedeDescargarPdf(String nombrePrograma, boolean puedeDescargarPdf) {
        ProgramaAcademico programa = programaAcademicoRepository.findByNombre(nombrePrograma);
        if (programa == null) {
            throw new ProgramaNotFoundException();
        }

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (!programa.getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        programa.setPuedeDescargarPdf(puedeDescargarPdf);
        programaAcademicoRepository.save(programa);
    }

    @Override
    public void deleteProgramaAcademico(Long id) {
        ProgramaAcademico programaAcademico =
                programaAcademicoRepository.findById(id).orElseThrow(ProgramaNotFoundException::new);

        programaAcademicoRepository.deleteById(programaAcademico.getId());
    }

    // @Scheduled(cron = "0 * * * * *") // Se ejecuta cada minuto
    @Scheduled(cron = "0 0 0 * * *") // Se ejecuta todos los días a la medianoche
    public void verificarPeriodosDeModificacion() {
        List<ProgramaAcademico> programas = programaAcademicoRepository.findAll();
        LocalDate fechaActual = LocalDate.now();

        for (ProgramaAcademico programa : programas) {
            actualizarPeriodoDeModificacion(programa, fechaActual);
        }
    }

    private void actualizarPeriodoDeModificacion(ProgramaAcademico programa, LocalDate fechaActual) {
        LocalDate fechaInicioModificacion = programa.getFechaInicioModificacion();
        Integer duracionModificacion = programa.getDuracionModificacion();

        if (fechaInicioModificacion != null && duracionModificacion != null) {
            LocalDate fechaFinModificacion = fechaInicioModificacion.plusDays(duracionModificacion);
            if (fechaActual.isAfter(fechaFinModificacion)) {
                programa.setFechaInicioModificacion(null);
                programa.setDuracionModificacion(null);
                programaAcademicoRepository.save(programa);
            }
        }
    }

    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
