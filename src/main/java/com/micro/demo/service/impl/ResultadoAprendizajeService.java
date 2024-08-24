package com.micro.demo.service.impl;

import com.micro.demo.entities.Competencia;
import com.micro.demo.entities.CompetenciaResultado;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.repository.ICompetenciaRepository;
import com.micro.demo.repository.ICompetenciaResultadoRepository;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.service.IResultadoAprendizajeService;
import com.micro.demo.service.exceptions.CompetenciaNotFoundException;
import com.micro.demo.service.exceptions.FakeEstatusNotAllowed;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.ResultadoAprendizajeNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ResultadoAprendizajeService implements IResultadoAprendizajeService {
    private final IResultadoAprendizajeRepository resultadoAprendizajeRepository;
    private final ICompetenciaRepository competenciaRepository;
    private final ICompetenciaResultadoRepository competenciaResultadoRepository;

    public ResultadoAprendizajeService(IResultadoAprendizajeRepository resultadoAprendizajeRepository, ICompetenciaRepository competenciaRepository, ICompetenciaResultadoRepository competenciaResultadoRepository) {
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
        this.competenciaRepository = competenciaRepository;
        this.competenciaResultadoRepository = competenciaResultadoRepository;
    }

    /**
     * Obtiene los resultados de aprendizaje mediante la paginacion
     *
     * @param pagina           numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de los resultados de aprendizaje.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllResultado(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<ResultadoAprendizaje> paginaResultados =
                resultadoAprendizajeRepository.findAll(PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending()));

        if (paginaResultados.isEmpty()) {
            throw new NoDataFoundException();
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaResultados.getTotalElements());
        response.put("data", paginaResultados.getContent());

        return response;
    }


    /**
     * Guardar un resultado de aprendizaje
     *
     * @param resultadoAprendizaje - Informacion del resultado de aprendizaje
     * */
    @Override
    public void saveResultado(ResultadoAprendizaje resultadoAprendizaje) {
        resultadoAprendizajeRepository.save(resultadoAprendizaje);
    }

    /**
     * Actualizar un resultado de aprendizaje
     *
     * @param id - Identificador unico del resultado de aprendizaje a actualizar.
     * @param resultadoAprendizaje - Informacion del resultado de aprendizaje.
     * @throws ResultadoAprendizajeNotFoundException - Se lanza si el resultado de aprendizaje no se encuentra.
     * */
    @Override
    public void updateResultado(Long id,ResultadoAprendizaje resultadoAprendizaje) {
        ResultadoAprendizaje existingResultado = resultadoAprendizajeRepository
                .findById(id).orElseThrow(ResultadoAprendizajeNotFoundException::new);

        existingResultado.setNombre(resultadoAprendizaje.getNombre());
        existingResultado.setDescripcion(resultadoAprendizaje.getDescripcion());
        existingResultado.setEstatus(resultadoAprendizaje.isEstatus());

        resultadoAprendizajeRepository.save(existingResultado);
    }


    /**
     * Asignar competencia al resultado de aprendizaje
     *
     * @param resultadoAprendizajeId - Identificador unico del resultado de aprendizaje
     * @param competenciaIds  - Lista de Identificadores unicos de competencias
     * @throws ResultadoAprendizajeNotFoundException - Se lanza si el resultado de aprendizaje no se encuentra.
     * @throws CompetenciaNotFoundException - Se lanza si no se encuentra la competencia con el ID especificado.
     * @throws FakeEstatusNotAllowed - Se lanza si el resultado de aprendizaje o la competencia tienen estatus false.
     * */
    @Override
    public void assignCompetencia(Long resultadoAprendizajeId, List<Long> competenciaIds) {
        ResultadoAprendizaje resultadoAprendizaje = resultadoAprendizajeRepository.findById(resultadoAprendizajeId)
                .orElseThrow(ResultadoAprendizajeNotFoundException::new);

        for (Long competenciaId : competenciaIds) {
            Competencia competencia = competenciaRepository.findById(competenciaId)
                    .orElseThrow(CompetenciaNotFoundException::new);

            if (!resultadoAprendizaje.isEstatus() || !competencia.isEstatus()) {
                throw new FakeEstatusNotAllowed();
            }

            CompetenciaResultado asociacion = new CompetenciaResultado();
            asociacion.setResultadoAprendizaje(resultadoAprendizaje);
            asociacion.setCompetencia(competencia);

            competenciaResultadoRepository.save(asociacion);
        }

        resultadoAprendizajeRepository.save(resultadoAprendizaje);
    }

    /**
     * Elimina un resultado de aprendizaje por su identificador único.
     *
     * @param id - Identificador único de un resultado de aprendizaje a eliminar.
     * @throws ResultadoAprendizajeNotFoundException - Se lanza si no se encuentra un resultado de aprendizaje con el ID especificado.
     */
    @Override
    public void deleteResultado(Long id) {
        resultadoAprendizajeRepository.findById(id)
                .orElseThrow(ResultadoAprendizajeNotFoundException::new);

        resultadoAprendizajeRepository.deleteById(id);
    }
}
