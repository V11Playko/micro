package com.micro.demo.service;

import com.micro.demo.controller.dto.AreaFormacionDto;
import com.micro.demo.entities.AreaFormacion;

import java.util.Map;

public interface IAreaFormacionService {

    Map<String, Object> getAllAreaFormacion(Integer pagina, Integer elementosXpagina);
    AreaFormacion getAreaFormacion(Long id);
    void saveAreaFormacion(AreaFormacionDto areaFormacionDto);
    void deleteAreaFormacion(Long id);
}
