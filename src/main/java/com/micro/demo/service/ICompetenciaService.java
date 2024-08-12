package com.micro.demo.service;

import com.micro.demo.controller.dto.CompetenciaDto;
import com.micro.demo.entities.Competencia;

import java.util.List;

public interface ICompetenciaService {
    List<Competencia> getAllCompetencias(int pagina, int elementosXpagina);
    void saveCompetencia(CompetenciaDto competenciaDto);
    void updateCompetencia(Long id, Competencia competencia);
    void deleteCompetencia(Long id);
}
