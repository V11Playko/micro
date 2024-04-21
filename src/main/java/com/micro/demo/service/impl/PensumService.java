package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.Pensum;
import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.repository.IPensumRepository;
import com.micro.demo.repository.IProgramaAcademicoRepository;
import com.micro.demo.service.IPensumService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PensumService implements IPensumService {
    private final IPensumRepository pensumRepository;
    private final IProgramaAcademicoRepository programaAcademicoRepository;

    public PensumService(IPensumRepository pensumRepository, IProgramaAcademicoRepository programaAcademicoRepository) {
        this.pensumRepository = pensumRepository;
        this.programaAcademicoRepository = programaAcademicoRepository;
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


        pensum.setProgramaAcademico(programaAcademico);
        pensumRepository.save(pensum);
    }

    @Override
    public void updatePensum(Long id, Pensum pensum) {
        Pensum existingPensum = pensumRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingPensum.setCreditosTotales(pensum.getCreditosTotales());
        existingPensum.setFechaInicio(pensum.getFechaInicio());
        existingPensum.setFechaFinal(pensum.getFechaFinal());
        existingPensum.setEstatus(pensum.isEstatus());

        pensumRepository.save(existingPensum);
    }

    @Override
    public void deletePensum(Long id) {
        Pensum pensum = pensumRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (!pensum.getProgramaAcademico().getDirector().getCorreo().equals(correoUsuarioAutenticado)){
            throw new UnauthorizedException();
        }

        pensumRepository.deleteById(pensum.getId());
    }


    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
