package com.micro.demo.repository;

import com.micro.demo.entities.EvaluacionResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEvaluacionResultadoRepository extends JpaRepository<EvaluacionResultado, Long> {
}
