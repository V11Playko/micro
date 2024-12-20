package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.ProgramaAcademicoDto;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import com.micro.demo.mapper.ProgramaAcademicoMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProgramaAcademicoService implements IProgramaAcademicoService {
    private final IUsuarioRepository usuarioRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;
    private final ProgramaAcademicoMapper programaAcademicoMapper;

    public ProgramaAcademicoService(IUsuarioRepository usuarioRepository, IProgramaAcademicoRepository programaAcademicoRepository, ProgramaAcademicoMapper programaAcademicoMapper) {
        this.usuarioRepository = usuarioRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
        this.programaAcademicoMapper = programaAcademicoMapper;
    }

    /**
     * Obtiene los programas academicos mediante la paginacion
     *
     * @param pagina           numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de los programas academicos.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAll(Integer pagina, Integer elementosXpagina) {
        Page<ProgramaAcademico> paginaProgramas;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaProgramas = new PageImpl<>(programaAcademicoRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaProgramas = programaAcademicoRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaProgramas.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaProgramas.getTotalElements());
        response.put("data", paginaProgramas.getContent());

        return response;
    }



    /**
     * Obtiene un programa academico por su nombre
     *
     * @param nombre - Nombre del programa academico
     * @return Lista de areas de formacion
     * @throws ProgramaNotFoundException - Se lanza si el programa academico no se encuentra.
     */
    @Override
    public ProgramaAcademico getProgramaByNombre(String nombre) {
        ProgramaAcademico programa = programaAcademicoRepository.findByNombre(nombre);
        if (programa == null) throw new ProgramaNotFoundException();
        return programa;
    }

    @Override
    public ProgramaAcademico getPrograma(Long id) {
        return programaAcademicoRepository.findById(id).orElseThrow(ProgramaNotFoundException::new);
    }

    /**
     * Guardar un programa academico
     *
     * @param programaAcademicoDto - Informacion del programa academico
     * @throws ProgramaAcademicoExistenteException - Se lanza si ya existe un programa academico con el mismo nombre.
     * */
    @Override
    public void saveProgramaAcademico(ProgramaAcademicoDto programaAcademicoDto) {
        ProgramaAcademico existingPrograma = programaAcademicoRepository.findByNombre(programaAcademicoDto.getNombre());
        if (existingPrograma != null) {
            throw new ProgramaAcademicoExistenteException();
        }

        ProgramaAcademico programaAcademico = programaAcademicoMapper.toEntity(programaAcademicoDto);

        programaAcademico.setDirector(null);
        programaAcademico.setPuedeDescargarPdf(false);
        programaAcademico.setFechaInicioModificacion(null);
        programaAcademico.setDuracionModificacion(null);

        programaAcademicoRepository.save(programaAcademico);
    }

    /**
     * Asignar un director al programa academico
     *
     * @param correoDirector - Correo de el director al que se asignara el programa academico.
     * @param nombrePrograma - Nombre del programa.
     * @throws ProgramaNotFoundException - Se lanza si no se encuentra el programa academico.
     * @throws DirectorNotFoundException - Se lanza si el correo seleccionado no es de un usuario director.
     * @throws DirectorAlreadyAssignedException - Se lanza si el correo ya ha sido asignado a un programa academico actualmente.
     * */
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
     *  Actualizar el periodo de modificacion
     *
     * @param nombrePrograma - Nombre del programa
     * @param fechaInicioModificacion - Fecha de inicio
     * @param duracionModificacion - Duracion del periodo de modificacion.
     * @throws ProgramaNotFoundException - Se lanza si no se encuentra el programa academico.
     * @throws UnauthorizedException - Se lanza si no eres el director del programa academico.
     * @throws DuracionModificacionInvalidaException - Se lanza si la duracion es menor a 1.
     * @throws PeriodoModificacionInvalidoException - Se lanza si el periodo de modificacion no es valido.
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

    /**
     *  Habilitar o deshabilitar la descarga de PDFS
     *
     * @param nombrePrograma - Nombre del programa academico.
     * @param puedeDescargarPdf - Se habilita o deshabilita la descarga de PDFS.
     * @throws ProgramaNotFoundException - Se lanza si no se encuentra el programa academico.
     * @throws UnauthorizedException - Se lanza si no eres el director del programa academico.
     **/
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

    /**
     * Elimina un programa academico por su identificador único.
     *
     * @param id - Identificador único del programa academico a eliminar
     * @throws ProgramaNotFoundException - Se lanza si no se encuentra el programa academico con el ID especificado
     */
    @Override
    public void deleteProgramaAcademico(Long id) {
        ProgramaAcademico programaAcademico =
                programaAcademicoRepository.findById(id).orElseThrow(ProgramaNotFoundException::new);

        programaAcademicoRepository.deleteById(programaAcademico.getId());
    }

    /**
     * Metodo que se ejecuta todos los días a medianoche para saber si algun
     * programa academico ya ha cumplido su periodo de modificacion.
     **/
    // @Scheduled(cron = "0 * * * * *") // Se ejecuta cada minuto
    @Scheduled(cron = "0 0 0 * * *") // Se ejecuta todos los días a la medianoche
    public void verificarPeriodosDeModificacion() {
        List<ProgramaAcademico> programas = programaAcademicoRepository.findAll();
        LocalDate fechaActual = LocalDate.now();

        for (ProgramaAcademico programa : programas) {
            actualizarPeriodoDeModificacion(programa, fechaActual);
        }
    }

    /**
     * Se ejecuta si se ha cumplido la duracion del periodo de modificacion
     * de un programa academico y setea los valores en null.
     **/
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
        if (authentication != null && authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();  // El correo está en el principal
        }
        throw new RuntimeException("Error obteniendo el correo del token.");
    }

}
