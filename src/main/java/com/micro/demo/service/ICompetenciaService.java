package com.micro.demo.service;

import com.micro.demo.entities.Competencia;

import java.util.List;

public interface ICompetenciaService {
    List<Competencia> getAllCompetencias(int pagina, int elementosXpagina);
    void saveCompetencia(Competencia competencia);
    void updateCompetencia(Long id, Competencia competencia);
    void deleteCompetencia(Long id);
}
