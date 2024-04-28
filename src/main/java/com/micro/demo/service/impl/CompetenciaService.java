package com.micro.demo.service.impl;

import com.micro.demo.entities.Competencia;
import com.micro.demo.entities.Pensum;
import com.micro.demo.repository.ICompetenciaRepository;
import com.micro.demo.service.ICompetenciaService;
import com.micro.demo.service.exceptions.CompetenciaNotFoundException;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CompetenciaService implements ICompetenciaService {

    private final ICompetenciaRepository competenciaRepository;

    public CompetenciaService(ICompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    @Override
    public List<Competencia> getAllCompetencias(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<Competencia> paginaCompetencias =
                competenciaRepository.findAll(PageRequest.of(pagina -1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaCompetencias.isEmpty()) {
            throw new NoDataFoundException();
        }

        return paginaCompetencias.getContent();
    }

    @Override
    public void saveCompetencia(Competencia competencia) {
        competenciaRepository.save(competencia);
    }

    @Override
    public void updateCompetencia(Long id, Competencia competencia) {
        Competencia competenciaExistente = competenciaRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        competenciaExistente.setNombre(competencia.getNombre());
        competenciaExistente.setDescripcion(competencia.getDescripcion());
        competenciaExistente.setEstatus(competencia.isEstatus());

        competenciaRepository.save(competenciaExistente);
    }

    @Override
    public void deleteCompetencia(Long id) {
        competenciaRepository.findById(id)
                .orElseThrow(CompetenciaNotFoundException::new);

        competenciaRepository.deleteById(id);
    }
}
