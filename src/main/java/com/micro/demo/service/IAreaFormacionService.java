package com.micro.demo.service;

import com.micro.demo.entities.AreaFormacion;

import java.util.List;

public interface IAreaFormacionService {

    List<AreaFormacion> getAllAreaFormacion(int pagina, int elementosXpagina);
    void saveAreaFormacion(AreaFormacion areaFormacion);
    void deleteAreaFormacion(Long id);
}
