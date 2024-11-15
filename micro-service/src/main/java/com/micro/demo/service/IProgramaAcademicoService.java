package com.micro.demo.service;

import com.micro.demo.controller.dto.ProgramaAcademicoDto;
import com.micro.demo.entities.ProgramaAcademico;

import java.time.LocalDate;
import java.util.Map;


public interface IProgramaAcademicoService {
    Map<String, Object> getAll(Integer pagina, Integer elementosXpagina);
    ProgramaAcademico getProgramaByNombre(String nombre);
    ProgramaAcademico getPrograma(Long id);
    void saveProgramaAcademico(ProgramaAcademicoDto programaAcademicoDto);
    void assignDirector(String correoDirector, String nombrePrograma);
    void updatePeriodoModificacion(String nombrePrograma, LocalDate fechaInicioModificacion, Integer duracionModificacion);
    void updatePuedeDescargarPdf(String nombrePrograma, boolean puedeDescargarPdf);
    void deleteProgramaAcademico(Long id);
}
