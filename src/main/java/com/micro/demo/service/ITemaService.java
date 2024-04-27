package com.micro.demo.service;

import com.micro.demo.entities.Tema;

import java.util.List;

public interface ITemaService {
    List<Tema> getAllTemas(int pagina, int elementosXpagina);
    void saveTema(Tema tema);
    void updateTema(Long id, Tema tema);
    void assignTemasToUnidad(Long unidadId, List<Long> temaIds);
    void deleteTema(Long id);
}
