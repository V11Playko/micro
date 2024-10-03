package com.micro.demo.repository;

import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Unidad;
import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.entities.UnidadResultadoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnidadResultadoAprendizajeRepository extends JpaRepository<UnidadResultadoAprendizaje, Long> {
    List<UnidadResultadoAprendizaje> findByResultadoAprendizaje(ResultadoAprendizaje resultadoAprendizaje);
    List<UnidadResultadoAprendizaje> findByUnidad(Unidad unidad);
}
