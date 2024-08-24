package com.micro.demo.service;

import com.micro.demo.entities.ProgramaAcademico;

import java.time.LocalDate;
import java.util.Map;


public interface IProgramaAcademicoService {
    Map<String, Object> getAll(int pagina, int elementosXpagina);
    ProgramaAcademico getProgramaByNombre(String nombre);
    void saveProgramaAcademico(ProgramaAcademico programaAcademico);
    void assignDirector(String correoDirector, String nombrePrograma);
    void updatePeriodoModificacion(String nombrePrograma, LocalDate fechaInicioModificacion, Integer duracionModificacion);
    void updatePuedeDescargarPdf(String nombrePrograma, boolean puedeDescargarPdf);
    void deleteProgramaAcademico(Long id);
}
