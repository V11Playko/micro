package com.micro.demo.service;

import com.micro.demo.entities.Tema;

import java.util.List;
import java.util.Map;

public interface ITemaService {
    Map<String, Object> getAllTemas(Integer pagina, Integer elementosXpagina);
    Tema getTema(Long id);
    void saveTema(Tema tema);
    void updateTema(Long id, Tema tema);
    void assignTemasToUnidad(Long unidadId, List<Long> temaIds);
    void deleteTema(Long id);
}
