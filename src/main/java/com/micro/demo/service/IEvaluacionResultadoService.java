package com.micro.demo.service;

import com.micro.demo.controller.dto.EvaluacionResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.EvaluacionResultado;

import java.util.List;
import java.util.Map;

public interface IEvaluacionResultadoService {
    Map<String, Object> getAllEvaluacionResultados(Integer pagina, Integer elementosXpagina);
    UnidadResultadoResponseDTO getEvaluacionResultado(Long id);
    void saveEvaluacionResultados(List<EvaluacionResultadoDTO> evaluacionResultadoDTOS);
    void updateEvaluacionResultado(Long id, EvaluacionResultado evaluacionResultado);
    void deleteEvaluacionResultado(Long id);
}
