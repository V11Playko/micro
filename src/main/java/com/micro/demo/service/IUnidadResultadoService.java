package com.micro.demo.service;

import com.micro.demo.controller.dto.UnidadResultadoDTO;
import com.micro.demo.controller.dto.response.UnidadResultadoResponseDTO;
import com.micro.demo.entities.UnidadResultado;

import java.util.List;

public interface IUnidadResultadoService {
    List<UnidadResultadoResponseDTO> getAllUnidadResultados(int pagina, int elementosXpagina);
    void saveUnidadResultados(List<UnidadResultadoDTO> unidadResultadoDTOs);
    void updateUnidadResultado(Long id, UnidadResultado unidadResultado);
    void deleteUnidadResultado(Long id);
}
