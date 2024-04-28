package com.micro.demo.repository;

import com.micro.demo.entities.CompetenciaResultado;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompetenciaResultadoRepository extends JpaRepository<CompetenciaResultado, Long> {
}
