package com.micro.demo.service;

import com.micro.demo.controller.dto.PensumDto;
import com.micro.demo.entities.Pensum;

import java.util.List;
import java.util.Map;

public interface IPensumService {
    Map<String, Object> getAllPensum(int pagina, int elementosXpagina);
    Map<String, Object> getPensumsNoModificadosDuranteUnAño(int pagina, int elementosXpagina);
    void savePensum(PensumDto pensumDto);
    void updatePensum(Long id, Pensum pensum);
    void assignAsignaturas(Long pensumId, List<Long> asignaturasId);
    void removeAsignaturaFromPensum(Long pensumId, Long asignaturaId);
    void deletePensum(Long id);
    void duplicatePensum(Long pensumId);
}
