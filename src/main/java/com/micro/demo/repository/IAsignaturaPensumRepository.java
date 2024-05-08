package com.micro.demo.repository;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.AsignaturaPensum;
import com.micro.demo.entities.Pensum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAsignaturaPensumRepository extends JpaRepository<AsignaturaPensum, Long> {
    List<AsignaturaPensum> findByPensumIdAndAsignaturaIdIn(Long pensumId, List<Long> asignaturaIds);
    AsignaturaPensum findByPensumIdAndAsignaturaId(Long pensumId, Long asignaturaId);
    List<AsignaturaPensum> findByPensumId(Long pensumId);
    boolean existsByAsignaturaAndPensum(Asignatura asignatura, Pensum pensum);
}
