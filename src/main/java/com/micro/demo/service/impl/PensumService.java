package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.repository.IAsignaturaPensumRepository;
import com.micro.demo.repository.IAsignaturaRepository;
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
import com.micro.demo.service.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PensumService implements IPensumService {
    private final IPensumRepository pensumRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;
    private final IAsignaturaRepository asignaturaRepository;
    private final IAsignaturaPensumRepository asignaturaPensumRepository;

    public PensumService(IPensumRepository pensumRepository, IProgramaAcademicoRepository programaAcademicoRepository, IAsignaturaRepository asignaturaRepository, IAsignaturaPensumRepository asignaturaPensumRepository) {
        this.pensumRepository = pensumRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.asignaturaPensumRepository = asignaturaPensumRepository;
    }

    @Override
    public List<Pensum> getAllPensum(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<Pensum> paginaPensums =
                pensumRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaPensums.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaPensums.getContent();
    }

    @Override
    public void savePensum(Pensum pensum) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        ProgramaAcademico programaAcademico = programaAcademicoRepository.findByDirectorCorreo(correoUsuarioAutenticado);

        if (!programaAcademico.getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        pensum.setFechaInicio(LocalDate.now());
        pensum.setProgramaAcademico(programaAcademico);
        pensumRepository.save(pensum);
    }

    @Override
    public void updatePensum(Long id, Pensum pensum) {
        Pensum existingPensum = pensumRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingPensum.setCreditosTotales(pensum.getCreditosTotales());
        existingPensum.setFechaInicio(LocalDate.now());
        existingPensum.setFechaFinal(pensum.getFechaFinal());
        existingPensum.setEstatus(pensum.isEstatus());

        pensumRepository.save(existingPensum);
    }

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

    @Override
    public void deletePensum(Long id) {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (!pensum.getProgramaAcademico().getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        List<AsignaturaPensum> asignaturaPensums = pensum.getAsignaturaPensum();
        asignaturaPensumRepository.deleteAll(asignaturaPensums);

        pensumRepository.deleteById(pensum.getId());
    }

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
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
