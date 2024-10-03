package com.micro.demo.repository;

import com.micro.demo.entities.UnidadResultado;
import com.micro.demo.entities.UnidadResultadoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUnidadResultadoResultadoAprendizajeRepository extends JpaRepository<UnidadResultadoAprendizaje, Long> {
    List<UnidadResultadoAprendizaje> findByUnidadResultado(UnidadResultado unidadResultado);
}
