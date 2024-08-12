package com.micro.demo.service;

import com.micro.demo.controller.dto.AsignaturaDto;
import com.micro.demo.entities.Asignatura;

import java.util.List;

public interface IAsignaturaService {
    List<Asignatura> getAllAsignatura(int pagina, int elementosXpagina);
    void saveAsignatura(AsignaturaDto asignaturaDto);
    void updateAsignatura(Long id, Asignatura asignatura);
    void assignDocentes(Long asignaturaId, List<String> correoDocentes);
    void removeDocente(Long asignaturaId, String correoDocente);
    void deleteAsignatura(Long id);
}
