package com.micro.demo.service;

import com.micro.demo.entities.Pensum;

import java.util.List;

public interface IPensumService {
    List<Pensum> getAllPensum(int pagina, int elementosXpagina);
    void savePensum(Pensum pensum);
    void updatePensum(Long id, Pensum pensum);
    void deletePensum(Long id);
}
