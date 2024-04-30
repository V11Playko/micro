package com.micro.demo.service;

import com.micro.demo.entities.Pensum;

import java.util.List;

public interface IPensumService {
    List<Pensum> getAllPensum(int pagina, int elementosXpagina);
    void savePensum(Pensum pensum);
    void updatePensum(Long id, Pensum pensum);
    void assignAsignaturas(Long pensumId, List<Long> asignaturasId);
    void removeAsignaturaFromPensum(Long pensumId, Long asignaturaId);
    void deletePensum(Long id);
    void duplicatePensum(Long pensumId);
}
