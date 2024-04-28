package com.micro.demo.repository;

import com.micro.demo.entities.UnidadResultado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnidadResultadoRepository extends JpaRepository<UnidadResultado, Long> {
}
