package com.micro.demo.repository;

import com.micro.demo.entities.AsignaturaDocente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAsignaturaDocenteRepository extends JpaRepository<AsignaturaDocente, Long> {
}
