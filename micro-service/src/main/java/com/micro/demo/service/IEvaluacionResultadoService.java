package com.micro.demo.service;

import com.micro.demo.controller.dto.EvaluacionResultadoDto;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;

import java.util.List;
import java.util.Map;

public interface IEvaluacionResultadoService {
    Map<String, Object> getAllEvaluacionResultados(Integer pagina, Integer elementosXpagina);
    UnidadResultadoResponseDTO getEvaluacionResultado(Long id);
    void saveEvaluacionResultados(List<EvaluacionResultadoDto> evaluacionResultadoDtos);
    void updateEvaluacionResultado(Long id, EvaluacionResultadoDto evaluacionResultadoDto);
    void deleteEvaluacionResultado(Long id);
}
