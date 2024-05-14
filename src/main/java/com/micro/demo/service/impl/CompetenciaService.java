package com.micro.demo.service.impl;

import com.micro.demo.entities.Competencia;
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

    /**
     * Obtiene las competencias mediante la paginacion
     *
     * @param pagina numero de pagina.
     * @param elementosXpagina elementos que habran en cada pagina.
     * @return Lista de competencias.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1.
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
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

    /**
     * Guardar una competencia
     *
     * @param competencia - Informacion de la competencia.
     * */
    @Override
    public void saveCompetencia(Competencia competencia) {
        competenciaRepository.save(competencia);
    }

    /**
     * Actualizar una competencia
     *
     * @param id - Identificador unico de la competencia a actualizar.
     * @param competencia - Informacion de la competencia.
     * @throws CompetenciaNotFoundException - Se lanza si no se encuentra la competencia con el ID especificado.
     * */
    @Override
    public void updateCompetencia(Long id, Competencia competencia) {
        Competencia competenciaExistente = competenciaRepository.findById(id)
                .orElseThrow(CompetenciaNotFoundException::new);

        competenciaExistente.setNombre(competencia.getNombre());
        competenciaExistente.setDescripcion(competencia.getDescripcion());
        competenciaExistente.setEstatus(competencia.isEstatus());

        competenciaRepository.save(competenciaExistente);
    }

    /**
     * Elimina una competencia por su identificador único.
     *
     * @param id - Identificador único de la competencia a eliminar.
     * @throws CompetenciaNotFoundException - Se lanza si no se encuentra la competencia con el ID especificado.
     */
    @Override
    public void deleteCompetencia(Long id) {
        competenciaRepository.findById(id)
                .orElseThrow(CompetenciaNotFoundException::new);

        competenciaRepository.deleteById(id);
    }
}
