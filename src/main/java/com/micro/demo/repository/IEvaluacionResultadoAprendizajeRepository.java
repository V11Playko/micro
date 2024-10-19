package com.micro.demo.repository;

import com.micro.demo.entities.EvaluacionResultadoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEvaluacionResultadoAprendizajeRepository extends JpaRepository<EvaluacionResultadoAprendizaje, Long> {
}
