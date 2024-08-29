package com.micro.demo.service;

import com.micro.demo.controller.dto.UnidadResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.UnidadResultado;

import java.util.List;
import java.util.Map;

public interface IUnidadResultadoService {
    Map<String, Object> getAllUnidadResultados(Integer pagina, Integer elementosXpagina);
    UnidadResultadoResponseDTO getUnidadResultado(Long id);
    void saveUnidadResultados(List<UnidadResultadoDTO> unidadResultadoDTOs);
    void updateUnidadResultado(Long id, UnidadResultado unidadResultado);
    void deleteUnidadResultado(Long id);
}
