package com.micro.demo.service;

import com.micro.demo.entities.AreaFormacion;

import java.util.Map;

public interface IAreaFormacionService {

    Map<String, Object> getAllAreaFormacion(Integer pagina, Integer elementosXpagina);
    AreaFormacion getAreaFormacion(Long id);
    void saveAreaFormacion(AreaFormacion areaFormacion);
    void deleteAreaFormacion(Long id);
}
