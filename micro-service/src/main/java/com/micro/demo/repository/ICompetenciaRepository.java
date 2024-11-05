package com.micro.demo.repository;

import com.micro.demo.entities.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICompetenciaRepository extends JpaRepository<Competencia, Long> {
}
