package com.micro.demo.service;

import com.micro.demo.controller.dto.AsignaturaDto;
import com.micro.demo.entities.Asignatura;

import java.util.List;
import java.util.Map;

public interface IAsignaturaService {
    Map<String, Object> getAllAsignatura(Integer pagina, Integer elementosXpagina);
    Asignatura getAsignatura(Long id);
    void saveAsignatura(AsignaturaDto asignaturaDto);
    void updateAsignatura(Long id, AsignaturaDto asignaturaDto);
    void assignDocentes(Long asignaturaId, List<String> correoDocentes);
    void removeDocente(Long asignaturaId, String correoDocente);
    void deleteAsignatura(Long id);
}
