package com.micro.demo.service;

import com.micro.demo.entities.ResultadoAprendizaje;

import java.util.List;

public interface IResultadoAprendizajeService {
    List<ResultadoAprendizaje> getAllResultado(int pagina, int elementosXpagina);
    void saveResultado(ResultadoAprendizaje resultadoAprendizaje);
    void updateResultado(Long id,ResultadoAprendizaje resultadoAprendizaje);
    void assignCompetencia(Long resultadoAprendizajeId, List<Long> competenciaIds);
    void deleteResultado(Long id);
}
