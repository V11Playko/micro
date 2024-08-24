package com.micro.demo.service;

import com.micro.demo.entities.AreaFormacion;

import java.util.Map;

public interface IAreaFormacionService {

    Map<String, Object> getAllAreaFormacion(int pagina, int elementosXpagina);
    void saveAreaFormacion(AreaFormacion areaFormacion);
    void deleteAreaFormacion(Long id);
}
