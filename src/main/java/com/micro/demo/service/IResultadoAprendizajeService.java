package com.micro.demo.service;

import com.micro.demo.controller.dto.ResultadoAprendizajeDto;
import com.micro.demo.entities.ResultadoAprendizaje;

import java.util.List;
import java.util.Map;

public interface IResultadoAprendizajeService {
    Map<String, Object> getAllResultado(Integer pagina, Integer elementosXpagina);
    ResultadoAprendizaje getResultado(Long id);
    void saveResultado(ResultadoAprendizajeDto resultadoAprendizajeDto);
    void updateResultado(Long id,ResultadoAprendizajeDto resultadoAprendizajeDto);
    void assignCompetencia(Long resultadoAprendizajeId, List<Long> competenciaIds);
    void deleteResultado(Long id);
}
