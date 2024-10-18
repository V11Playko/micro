package com.micro.demo.service;

import com.micro.demo.controller.dto.TemaDto;
import com.micro.demo.entities.Tema;

import java.util.List;
import java.util.Map;

public interface ITemaService {
    Map<String, Object> getAllTemas(Integer pagina, Integer elementosXpagina);
    Tema getTema(Long id);
    void saveTema(TemaDto temaDto);
    void updateTema(Long id, TemaDto temaDto);
    void assignTemasToUnidad(Long unidadId, List<Long> temaIds);
    void deleteTema(Long id);
}
