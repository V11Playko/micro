package com.micro.demo.service;

import com.micro.demo.entities.UnidadResultado;

import java.util.List;

public interface IUnidadResultadoService {
    List<UnidadResultado> getAllUnidadResultados(int pagina, int elementosXpagina);
    void saveUnidadResultado(UnidadResultado unidadResultado);
    void updateUnidadResultado(Long id, UnidadResultado unidadResultado);
    void deleteUnidadResultado(Long id);
}
