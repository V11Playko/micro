package com.micro.demo.service;

import com.micro.demo.controller.dto.UnidadDto;
import com.micro.demo.entities.Unidad;

import java.util.List;

public interface IUnidadService {
    List<Unidad> getAllUnidad(int pagina, int elementosXpagina);
    Unidad getUnidad(Long id);
    void saveUnidad(UnidadDto unidadDto);
    void updateUnidad(Long id, Unidad unidad);
    void deleteUnidad(Long id);
}
