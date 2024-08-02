package com.micro.demo.service.impl;

import com.micro.demo.controller.dto.UnidadResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.entities.UnidadResultadoResultadoAprendizaje;
import com.micro.demo.repository.IResultadoAprendizajeRepository;
import com.micro.demo.repository.IUnidadResultadoRepository;
import com.micro.demo.repository.IUnidadResultadoResultadoAprendizajeRepository;
import com.micro.demo.service.IUnidadResultadoService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UnidadResultadoService implements IUnidadResultadoService {

    private final IUnidadResultadoRepository unidadResultadoRepository;
    private final IResultadoAprendizajeRepository resultadoAprendizajeRepository;
    private final IUnidadResultadoResultadoAprendizajeRepository unidadResultadoResultadoAprendizajeRepository;

    public UnidadResultadoService(IUnidadResultadoRepository unidadResultadoRepository, IResultadoAprendizajeRepository resultadoAprendizajeRepository, IUnidadResultadoResultadoAprendizajeRepository unidadResultadoResultadoAprendizajeRepository) {
        this.unidadResultadoRepository = unidadResultadoRepository;
        this.resultadoAprendizajeRepository = resultadoAprendizajeRepository;
        this.unidadResultadoResultadoAprendizajeRepository = unidadResultadoResultadoAprendizajeRepository;
    }

    /**
     * Obtiene las unidades de resultados
     *
     * @return Lista de unidades de resultados.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
    @Override
    public List<UnidadResultadoResponseDTO> getAllUnidadResultados(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        Page<UnidadResultado> paginaUnidadResultados =
                unidadResultadoRepository.findAll(PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending()));

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

            // Buscar los resultados de aprendizaje relacionados
            List<UnidadResultadoResultadoAprendizaje> intermedias =
                    unidadResultadoResultadoAprendizajeRepository.findByUnidadResultado(unidadResultado);

            List<ResultadoAprendizaje> resultados = intermedias.stream()
                    .map(UnidadResultadoResultadoAprendizaje::getResultadoAprendizaje)
                    .collect(Collectors.toList());

            dto.setResultados(resultados);

            responseDTOs.add(dto);
        }

        return responseDTOs;
    }



    /**
     * Guardar unidad de resultado
     *
     * @param unidadResultadoDTOs - Informacion de una unidad resultado
     * */
    @Override
    public void saveUnidadResultados(List<UnidadResultadoDTO> unidadResultadoDTOs) {
        for (UnidadResultadoDTO dto : unidadResultadoDTOs) {
            UnidadResultado unidadResultado = new UnidadResultado();
            unidadResultado.setTipoEvidencia(dto.getTipoEvidencia());
            unidadResultado.setInstrumentoEvaluacion(dto.getInstrumentoEvaluacion());
            unidadResultado.setCorteEvaluacion(dto.getCorteEvaluacion());
            unidadResultado.setEstatus(dto.isEstatus());

            // Save UnidadResultado
            unidadResultadoRepository.save(unidadResultado);

            // Save UnidadResultadoResultadoAprendizaje
            for (ResultadoAprendizaje resultado : dto.getResultados()) {
                UnidadResultadoResultadoAprendizaje intermedia = new UnidadResultadoResultadoAprendizaje();
                intermedia.setUnidadResultado(unidadResultado);
                intermedia.setResultadoAprendizaje(resultadoAprendizajeRepository.findById(resultado.getId()).orElse(null));
                unidadResultadoResultadoAprendizajeRepository.save(intermedia);
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
