package com.micro.demo.repository;

import com.micro.demo.entities.EvaluacionResultadoAprendizaje;
import com.micro.demo.entities.ResultadoAprendizaje;
import com.micro.demo.entities.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnidadResultadoAprendizajeRepository extends JpaRepository<EvaluacionResultadoAprendizaje, Long> {
    List<EvaluacionResultadoAprendizaje> findByResultadoAprendizaje(ResultadoAprendizaje resultadoAprendizaje);
    List<EvaluacionResultadoAprendizaje> findByUnidad(Unidad unidad);
}
