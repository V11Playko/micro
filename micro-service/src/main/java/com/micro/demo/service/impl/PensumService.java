package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.PensumDto;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.mapper.PensumMapper;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
import com.micro.demo.repository.IHistoryMovementRepository;
import com.micro.demo.repository.IPensumRepository;
import com.micro.demo.repository.IProgramaAcademicoRepository;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.exceptions.AllAsignaturasAssignsException;
import com.micro.demo.service.exceptions.AsignaturaNotFound;
import com.micro.demo.service.exceptions.AsignaturaNotFoundExceptionInPensum;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.PensumNotActiveException;
import com.micro.demo.service.exceptions.PensumNotFoundByIdException;
import com.micro.demo.service.exceptions.PensumNotFoundException;
import com.micro.demo.service.exceptions.ProgramaNotFoundException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PensumService implements IPensumService {
    private final IPensumRepository pensumRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;
    private final IHistoryMovementRepository historyMovementRepository;
    private final PensumMapper pensumMapper;

    public PensumService(IPensumRepository pensumRepository, IProgramaAcademicoRepository programaAcademicoRepository, IAsignaturaRepository asignaturaRepository, IAsignaturaPensumRepository asignaturaPensumRepository, IHistoryMovementRepository historyMovementRepository, PensumMapper pensumMapper) {
        this.pensumRepository = pensumRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
        this.historyMovementRepository = historyMovementRepository;
        this.pensumMapper = pensumMapper;
    }

    /**
     * Obtiene los pensums mediante la paginacion
     *
     * @param pagina           numero de pagina.
     * @param elementosXpagina elementos que habran en cada pagina.
     * @return Lista de pensums.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1.
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllPensum(Integer pagina, Integer elementosXpagina) {
        Page<Pensum> paginaPensums;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaPensums = new PageImpl<>(pensumRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaPensums = pensumRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaPensums.isEmpty()) {
            throw new NoDataFoundException();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaPensums.getTotalElements());
        response.put("data", paginaPensums.getContent());

        return response;
    }


    /**
     * Obtiene los pensums que no han sido modificados durante un año mediante la paginacion
     *
     * @param pagina           numero de pagina.
     * @param elementosXpagina elementos que habran en cada pagina.
     * @return Lista de competencias.
     * @return Lista de pensums no modificados durante un año
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1.
     * @throws NoDataFoundException - Si no se encuentra datos.
     * @return Lista de pensums no modificados durante un año
     */
    @Override
    public Map<String, Object> getPensumsNoModificadosDuranteUnAño(Integer pagina, Integer elementosXpagina) {
        Page<Pensum> paginaPensums;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaPensums = new PageImpl<>(pensumRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaPensums = pensumRepository.findAll(PageRequest.of(pagina - 1, elementosXpagina));
        }

        if (paginaPensums.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Obtener la fecha de hace un año
        LocalDateTime fechaLimite = LocalDateTime.now().minus(1, ChronoUnit.YEARS);

        // Obtener todos los pensums que han sido modificados en el último año
        List<Long> pensumsModificados = historyMovementRepository
                .findByFechaMovimientoAfter(fechaLimite)
                .stream()
                .map(historyMovement -> historyMovement.getPensum().getId())
                .toList();

        // Filtrar los pensums que no han sido modificados por el cuerpo docente durante más de un año
        List<Pensum> pensumsNoModificados = paginaPensums.getContent()
                .stream()
                .filter(pensum -> !pensumsModificados.contains(pensum.getId()))
                .toList();

        // Crear el mapa de respuesta que incluye totalData y los datos de la página o lista completa
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", pensumsNoModificados.size());
        response.put("data", pensumsNoModificados);

        return response;
    }

    @Override
    public Pensum getPensum(Long id) {
        return pensumRepository.findById(id).orElseThrow(PensumNotFoundException::new);
    }


    /**
     * Guardar un pensum
     *
     * @param pensumDto - Informacion del pensum.
     * @throws UnauthorizedException - Se lanza si el correo no es el del director asociado al programa academico.
     * */
    @Override
    public void savePensum(PensumDto pensumDto) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();

        ProgramaAcademico programaAcademico = programaAcademicoRepository.findById(pensumDto.getProgramaAcademicoId())
                .orElseThrow(ProgramaNotFoundException::new);

        if (!programaAcademico.getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        Pensum pensum = pensumMapper.toEntity(pensumDto);
        pensum.setProgramaAcademico(programaAcademico);

        pensumRepository.save(pensum);
    }

    /**
     * Actualizar un pensum
     *
     * @param id - Identificador unico del pensum a actualizar.
     * @param pensumDto - Informacion del pensum.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * */
    @Override
    public void updatePensum(Long id, PensumDto pensumDto) {
        Pensum existingPensum = pensumRepository.findById(id)
                .orElseThrow(PensumNotFoundException::new);

        existingPensum.setCreditosTotales(pensumDto.getCreditosTotales());
        existingPensum.setFechaInicio(LocalDate.now());
        existingPensum.setFechaFinal(pensumDto.getFechaFinal());
        existingPensum.setEstatus(pensumDto.isEstatus());

        pensumRepository.save(existingPensum);
    }

    /**
     * Asignar asignaturas a un pensum
     *
     * @param pensumId - Identificador unico del pensum.
     * @param asignaturasId - Lista de identificadores unicos de las asignaturas que se asignaran al pensum.
     * @throws PensumNotFoundException - Se lanza si el pensum indicado no se encuentra.
     * @throws PensumNotActiveException - Se lanza si el pensum no esta activo.
     * @throws AsignaturaNotFound - Se lanza si la asignatura indicada no se encuentra.
     * @throws AllAsignaturasAssignsException - Se lanza si todas las asignaturas ya han sido asignadas al pensum.
     * */
    @Override
    public void assignAsignaturas(Long pensumId, List<Long> asignaturasId) {
        Pensum pensum = pensumRepository.findById(pensumId)
                .orElseThrow(PensumNotFoundException::new);

        if (!pensum.isEstatus()) {
            throw new PensumNotActiveException();
        }

        // Obtener las asignaturas por sus IDs
        List<Asignatura> asignaturas = asignaturaRepository.findAllById(asignaturasId);

        // Verificar si todas las asignaturas existen
        if (asignaturas.size() != asignaturasId.size()) {
            throw new AsignaturaNotFound();
        }

        // Verificar si las asignaturas ya están asignadas al pensum
        List<AsignaturaPensum> asignaturasYaAsignadas = asignaturaPensumRepository.findByPensumIdAndAsignaturaIdIn(pensumId, asignaturasId);
        if (!asignaturasYaAsignadas.isEmpty()) {
            throw new AllAsignaturasAssignsException();
        }

        // Crear las relaciones entre el pensum y las asignaturas
        List<AsignaturaPensum> asignaturaPensums = new ArrayList<>();
        for (Asignatura asignatura : asignaturas) {
            AsignaturaPensum asignaturaPensum = new AsignaturaPensum();
            asignaturaPensum.setAsignatura(asignatura);
            asignaturaPensum.setPensum(pensum);
            asignaturaPensums.add(asignaturaPensum);
        }

        // Guardar las relaciones en la base de datos
        asignaturaPensumRepository.saveAll(asignaturaPensums);
    }

    /**
     * Remover una asignatura de un pensum.
     *
     * @param pensumId - Identificador unico del pensum.
     * @param asignaturaId - Identificador unico de la asignatura.
     * @throws PensumNotFoundByIdException - Se lanza si el pensum no se encuentra.
     * @throws AsignaturaNotFoundExceptionInPensum - Se lanza si la asignatura no existe en el pensum.
     * */
    @Override
    public void removeAsignaturaFromPensum(Long pensumId, Long asignaturaId) {
        pensumRepository.findById(pensumId)
                .orElseThrow(() -> new PensumNotFoundByIdException(pensumId));

        // Verificar si la asignatura existe en el pensum
        AsignaturaPensum asignaturaPensum = asignaturaPensumRepository.findByPensumIdAndAsignaturaId(pensumId, asignaturaId);

        if (asignaturaPensum == null) throw new AsignaturaNotFoundExceptionInPensum(asignaturaId, pensumId);

        // Eliminar la relación entre la asignatura y el pensum
        asignaturaPensumRepository.delete(asignaturaPensum);
    }

    /**
     * Elimina un pensum por su identificador unico.
     *
     * @param id - Identificador único del pensum a eliminar.
     * @throws PensumNotFoundException - Se lanza si no se encuentra el pensum con el ID especificado.
     */
    @Override
    public void deletePensum(Long id) {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(PensumNotFoundException::new);

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (!pensum.getProgramaAcademico().getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        List<AsignaturaPensum> asignaturaPensums = pensum.getAsignaturaPensum();
        asignaturaPensumRepository.deleteAll(asignaturaPensums);

        pensumRepository.deleteById(pensum.getId());
    }

    /**
     * Duplicar un pensum
     *
     * @param pensumId - Identificador único de el pensum que se va a duplicar
     * @throws PensumNotFoundException - Se lanza si no se encuentra el pensum con el ID especificado.
     */
    @Override
    public void duplicatePensum(Long pensumId) {
        Pensum originalPensum = pensumRepository.findById(pensumId)
                .orElseThrow(PensumNotFoundException::new);

        Pensum nuevoPensum = new Pensum();
        nuevoPensum.setCreditosTotales(originalPensum.getCreditosTotales());
        nuevoPensum.setFechaInicio(originalPensum.getFechaInicio());
        nuevoPensum.setFechaFinal(originalPensum.getFechaFinal());
        nuevoPensum.setEstatus(originalPensum.isEstatus());
        nuevoPensum.setProgramaAcademico(originalPensum.getProgramaAcademico());

        Pensum savedPensum = pensumRepository.save(nuevoPensum);

        // Copiar las asignaturas asociadas al pensum original
        List<AsignaturaPensum> asignaturasPensumOriginales = asignaturaPensumRepository.findByPensumId(pensumId);
        for (AsignaturaPensum ap : asignaturasPensumOriginales) {
            AsignaturaPensum nuevaAsignaturaPensum = new AsignaturaPensum();
            nuevaAsignaturaPensum.setAsignatura(ap.getAsignatura());
            nuevaAsignaturaPensum.setPensum(savedPensum);

            asignaturaPensumRepository.save(nuevaAsignaturaPensum);
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
