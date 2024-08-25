package com.micro.demo.service;

import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.entities.Unidad;

import java.util.Map;

public interface IUnidadService {
    Map<String, Object> getAllUnidad(Integer pagina, Integer elementosXpagina);
    Unidad getUnidad(Long id);
    void saveUnidad(UnidadDto unidadDto);
    void updateUnidad(Long id, Unidad unidad);
    void deleteUnidad(Long id);
}
