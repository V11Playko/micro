package com.micro.demo.repository;

import com.micro.demo.entities.Unidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUnidadRepository extends JpaRepository<Unidad, Long> {
}
