package com.micro.demo.service;

import com.micro.demo.entities.ResultadoAprendizaje;

import java.util.List;
import java.util.Map;

public interface IResultadoAprendizajeService {
    Map<String, Object> getAllResultado(Integer pagina, Integer elementosXpagina);
    void saveResultado(ResultadoAprendizaje resultadoAprendizaje);
    void updateResultado(Long id,ResultadoAprendizaje resultadoAprendizaje);
    void assignCompetencia(Long resultadoAprendizajeId, List<Long> competenciaIds);
    void deleteResultado(Long id);
}
