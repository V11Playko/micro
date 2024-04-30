package com.micro.demo.repository;

import com.micro.demo.entities.AsignaturaPensum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAsignaturaPensumRepository extends JpaRepository<AsignaturaPensum, Long> {
    List<AsignaturaPensum> findByPensumIdAndAsignaturaIdIn(Long pensumId, List<Long> asignaturaIds);
    Optional<AsignaturaPensum> findByPensumIdAndAsignaturaId(Long pensumId, Long asignaturaId);
    List<AsignaturaPensum> findByPensumId(Long pensumId);
}
