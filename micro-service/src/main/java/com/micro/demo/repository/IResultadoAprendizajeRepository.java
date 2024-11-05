package com.micro.demo.repository;

import com.micro.demo.entities.ResultadoAprendizaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IResultadoAprendizajeRepository extends JpaRepository<ResultadoAprendizaje, Long> {
}
