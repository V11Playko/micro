package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.EvaluacionResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.EvaluacionResultadoAprendizaje;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.EvaluacionResultado;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.repository.IEvaluacionResultadoRepository;
import com.micro.demo.repository.IUnidadRepository;
import com.micro.demo.repository.IUnidadResultadoAprendizajeRepository;
import com.micro.demo.service.IEvaluacionResultadoService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.ResultadoAprendizajeNotFoundException;
import com.micro.demo.service.exceptions.UnidadNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EvaluacionResultadoService implements IEvaluacionResultadoService {

    private final IEvaluacionResultadoRepository unidadResultadoRepository;
    private final IUnidadResultadoAprendizajeRepository unidadResultadoAprendizajeRepository;
    private final IUnidadRepository unidadRepository;

    public EvaluacionResultadoService(IEvaluacionResultadoRepository unidadResultadoRepository, IUnidadResultadoAprendizajeRepository unidadResultadoAprendizajeRepository, IUnidadRepository unidadRepository) {
        this.unidadResultadoRepository = unidadResultadoRepository;
        this.unidadResultadoAprendizajeRepository = unidadResultadoAprendizajeRepository;
        this.unidadRepository = unidadRepository;
    }

    /**
     * Obtiene las unidades de resultados
     *
     * @return Lista de unidades de resultados.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllEvaluacionResultados(Integer pagina, Integer elementosXpagina) {
        Page<EvaluacionResultado> paginaUnidadResultados;

        if (pagina == null || elementosXpagina == null) {
            // Recuperar todos los registros si la paginación es nula
            paginaUnidadResultados = new PageImpl<>(unidadResultadoRepository.findAll(Sort.by("id").ascending()));
        } else {
            if (pagina < 1) {
                throw new IlegalPaginaException();
            }

            paginaUnidadResultados = unidadResultadoRepository.findAll(
                    PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending())
            );
        }

        if (paginaUnidadResultados.isEmpty()) {
            throw new NoDataFoundException();
        }

        List<UnidadResultadoResponseDTO> responseDTOs = new ArrayList<>();

        for (EvaluacionResultado evaluacionResultado : paginaUnidadResultados) {
            UnidadResultadoResponseDTO dto = new UnidadResultadoResponseDTO();
            dto.setCorteEvaluacion(evaluacionResultado.getCorteEvaluacion());
            dto.setCriterioDesempeno(evaluacionResultado.getCriterioDesempeno());
            dto.setInstrumentoEvaluacion(evaluacionResultado.getInstrumentoEvaluacion());
            dto.setTipoEvidencia(evaluacionResultado.getTipoEvidencia());
            dto.setEstatus(evaluacionResultado.isEstatus());

            Unidad unidad = evaluacionResultado.getUnidad();

            List<EvaluacionResultadoAprendizaje> intermedias =
                    unidadResultadoAprendizajeRepository.findByUnidad(unidad);

            List<ResultadoAprendizaje> resultados = intermedias.stream()
                    .map(EvaluacionResultadoAprendizaje::getResultadoAprendizaje)
                    .collect(Collectors.toList());

            dto.setResultados(resultados);

            responseDTOs.add(dto);
        }

        // Crear el mapa de respuesta que incluye totalData y los datos de la página
        Map<String, Object> response = new HashMap<>();
        response.put("totalData", paginaUnidadResultados.getTotalElements());
        response.put("data", responseDTOs);

        return response;
    }

    @Override
    public UnidadResultadoResponseDTO getEvaluacionResultado(Long id) {
        EvaluacionResultado evaluacionResultado = unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        UnidadResultadoResponseDTO dto = new UnidadResultadoResponseDTO();
        dto.setTipoEvidencia(evaluacionResultado.getTipoEvidencia());
        dto.setInstrumentoEvaluacion(evaluacionResultado.getInstrumentoEvaluacion());
        dto.setCriterioDesempeno(evaluacionResultado.getCriterioDesempeno());
        dto.setCorteEvaluacion(evaluacionResultado.getCorteEvaluacion());
        dto.setEstatus(evaluacionResultado.isEstatus());

        Unidad unidad = evaluacionResultado.getUnidad();

        List<EvaluacionResultadoAprendizaje> intermedias =
                unidadResultadoAprendizajeRepository.findByUnidad(unidad);

        List<ResultadoAprendizaje> resultados = intermedias.stream()
                .map(EvaluacionResultadoAprendizaje::getResultadoAprendizaje)
                .collect(Collectors.toList());

        dto.setResultados(resultados);

        return dto;
    }




    /**
     * Guardar unidad de resultado
     *
     * @param evaluacionResultadoDTOS - Informacion de una unidad resultado
     * */
    @Override
    public void saveEvaluacionResultados(List<EvaluacionResultadoDTO> evaluacionResultadoDTOS) {
        for (EvaluacionResultadoDTO dto : evaluacionResultadoDTOS) {
            List<Long> unidadIds = dto.getUnidades();

            // Para cada ID de unidad, crear un EvaluacionResultado
            for (Long unidadId : unidadIds) {
                EvaluacionResultado evaluacionResultado = new EvaluacionResultado();
                evaluacionResultado.setTipoEvidencia(dto.getTipoEvidencia());
                evaluacionResultado.setInstrumentoEvaluacion(dto.getInstrumentoEvaluacion());
                evaluacionResultado.setCorteEvaluacion(dto.getCorteEvaluacion());
                evaluacionResultado.setCriterioDesempeno(dto.getCriterioDesempeno());
                evaluacionResultado.setEstatus(dto.isEstatus());

                Unidad unidad = unidadRepository.findById(unidadId)
                        .orElseThrow(UnidadNotFoundException::new);

                evaluacionResultado.setUnidad(unidad);

                unidadResultadoRepository.save(evaluacionResultado);
            }
        }
    }

    /**
     * Actualizar una unidad de resultado
     *
     * @param id - Identificador unico de una unidad resultado a actualizar.
     * @param evaluacionResultado - Informacion de una unidad de resultado.
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     * */
    @Override
    public void updateEvaluacionResultado(Long id, EvaluacionResultado evaluacionResultado) {
        EvaluacionResultado existingUnidad = unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingUnidad.setCorteEvaluacion(evaluacionResultado.getCorteEvaluacion());
        existingUnidad.setEstatus(evaluacionResultado.isEstatus());
        existingUnidad.setCriterioDesempeno(evaluacionResultado.getCriterioDesempeno());
        existingUnidad.setInstrumentoEvaluacion(evaluacionResultado.getInstrumentoEvaluacion());
        existingUnidad.setTipoEvidencia(evaluacionResultado.getTipoEvidencia());

        unidadResultadoRepository.save(existingUnidad);
    }

    /**
     * Elimina una unidad de resultado por su identificador único.
     *
     * @param id - Identificador único de una unidad de resultado a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     */
    @Override
    public void deleteEvaluacionResultado(Long id) {
        unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        unidadResultadoRepository.deleteById(id);
    }
}
