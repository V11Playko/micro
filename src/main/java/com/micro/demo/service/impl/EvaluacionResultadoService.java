package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.UnidadResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.entities.UnidadResultadoAprendizaje;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.repository.IUnidadResultadoRepository;
import com.micro.demo.repository.IUnidadResultadoAprendizajeRepository;
import com.micro.demo.service.IUnidadResultadoService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.ResultadoAprendizajeNotFoundException;
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
public class UnidadResultadoService implements IUnidadResultadoService {

    private final IUnidadResultadoRepository unidadResultadoRepository;
    private final IResultadoAprendizajeRepository resultadoAprendizajeRepository;
    private final IUnidadResultadoAprendizajeRepository unidadResultadoAprendizajeRepository;

    public UnidadResultadoService(IUnidadResultadoRepository unidadResultadoRepository, IResultadoAprendizajeRepository resultadoAprendizajeRepository, IUnidadResultadoAprendizajeRepository unidadResultadoAprendizajeRepository) {
        this.unidadResultadoRepository = unidadResultadoRepository;
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
        this.unidadResultadoAprendizajeRepository = unidadResultadoAprendizajeRepository;
    }

    /**
     * Obtiene las unidades de resultados
     *
     * @return Lista de unidades de resultados.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException  - Si no se encuentra datos.
     */
    @Override
    public Map<String, Object> getAllUnidadResultados(Integer pagina, Integer elementosXpagina) {
        Page<UnidadResultado> paginaUnidadResultados;

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

        for (UnidadResultado unidadResultado : paginaUnidadResultados) {
            UnidadResultadoResponseDTO dto = new UnidadResultadoResponseDTO();
            dto.setCorteEvaluacion(unidadResultado.getCorteEvaluacion());
            dto.setCriterioDesempeno(unidadResultado.getCriterioDesempeno());
            dto.setInstrumentoEvaluacion(unidadResultado.getInstrumentoEvaluacion());
            dto.setTipoEvidencia(unidadResultado.getTipoEvidencia());
            dto.setEstatus(unidadResultado.isEstatus());

            // Obtener la Unidad relacionada
            Unidad unidad = unidadResultado.getUnidad();

            // Buscar los resultados de aprendizaje relacionados con la Unidad
            List<UnidadResultadoAprendizaje> intermedias =
                    unidadResultadoAprendizajeRepository.findByUnidad(unidad);

            List<ResultadoAprendizaje> resultados = intermedias.stream()
                    .map(UnidadResultadoAprendizaje::getResultadoAprendizaje)
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
    public UnidadResultadoResponseDTO getUnidadResultado(Long id) {
        UnidadResultado unidadResultado = unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        UnidadResultadoResponseDTO dto = new UnidadResultadoResponseDTO();
        dto.setTipoEvidencia(unidadResultado.getTipoEvidencia());
        dto.setInstrumentoEvaluacion(unidadResultado.getInstrumentoEvaluacion());
        dto.setCriterioDesempeno(unidadResultado.getCriterioDesempeno());
        dto.setCorteEvaluacion(unidadResultado.getCorteEvaluacion());
        dto.setEstatus(unidadResultado.isEstatus());

        Unidad unidad = unidadResultado.getUnidad();

        List<UnidadResultadoAprendizaje> intermedias =
                unidadResultadoAprendizajeRepository.findByUnidad(unidad);

        List<ResultadoAprendizaje> resultados = intermedias.stream()
                .map(UnidadResultadoAprendizaje::getResultadoAprendizaje)
                .collect(Collectors.toList());

        dto.setResultados(resultados);

        return dto;
    }




    /**
     * Guardar unidad de resultado
     *
     * @param unidadResultadoDTOs - Informacion de una unidad resultado
     * */
    @Override
    public void saveUnidadResultados(List<UnidadResultadoDTO> unidadResultadoDTOs) {
        for (UnidadResultadoDTO dto : unidadResultadoDTOs) {
            // Crear una nueva instancia de UnidadResultado
            UnidadResultado unidadResultado = new UnidadResultado();
            unidadResultado.setTipoEvidencia(dto.getTipoEvidencia());
            unidadResultado.setInstrumentoEvaluacion(dto.getInstrumentoEvaluacion());
            unidadResultado.setCorteEvaluacion(dto.getCorteEvaluacion());
            unidadResultado.setEstatus(dto.isEstatus());

            // Guardar la entidad UnidadResultado
            unidadResultadoRepository.save(unidadResultado);

            // Guardar las relaciones en UnidadResultadoAprendizaje (tabla intermedia)
            for (Long resultadoId : dto.getResultados()) {
                UnidadResultadoAprendizaje intermedia = new UnidadResultadoAprendizaje();
                intermedia.setUnidad(unidadResultado.getUnidad()); // Asignar la unidad a la intermedia
                intermedia.setResultadoAprendizaje(resultadoAprendizajeRepository.findById(resultadoId)
                        .orElseThrow(ResultadoAprendizajeNotFoundException::new));
                unidadResultadoAprendizajeRepository.save(intermedia);
            }
        }
    }

    /**
     * Actualizar una unidad de resultado
     *
     * @param id - Identificador unico de una unidad resultado a actualizar.
     * @param unidadResultado - Informacion de una unidad de resultado.
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     * */
    @Override
    public void updateUnidadResultado(Long id, UnidadResultado unidadResultado) {
        UnidadResultado existingUnidad = unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        existingUnidad.setCorteEvaluacion(unidadResultado.getCorteEvaluacion());
        existingUnidad.setEstatus(unidadResultado.isEstatus());
        existingUnidad.setCriterioDesempeno(unidadResultado.getCriterioDesempeno());
        existingUnidad.setInstrumentoEvaluacion(unidadResultado.getInstrumentoEvaluacion());
        existingUnidad.setTipoEvidencia(unidadResultado.getTipoEvidencia());

        unidadResultadoRepository.save(existingUnidad);
    }

    /**
     * Elimina una unidad de resultado por su identificador único.
     *
     * @param id - Identificador único de una unidad de resultado a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     */
    @Override
    public void deleteUnidadResultado(Long id) {
        unidadResultadoRepository.findById(id)
                .orElseThrow(NoDataFoundException::new);

        unidadResultadoRepository.deleteById(id);
    }
}
