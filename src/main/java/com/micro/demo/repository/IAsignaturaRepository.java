package com.micro.demo.repository;

import com.micro.demo.entities.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAsignaturaRepository extends JpaRepository<Asignatura, Long> {
}
