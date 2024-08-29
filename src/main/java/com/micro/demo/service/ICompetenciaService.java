package com.micro.demo.service;

import com.micro.demo.controller.dto.CompetenciaDto;
import com.micro.demo.entities.Competencia;

import java.util.Map;

public interface ICompetenciaService {
    Map<String, Object> getAllCompetencias(Integer pagina, Integer elementosXpagina);
    Competencia getCompetencia(Long id);
    void saveCompetencia(CompetenciaDto competenciaDto);
    void updateCompetencia(Long id, Competencia competencia);
    void deleteCompetencia(Long id);
}
