package com.micro.demo.repository;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.HistoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHistoryMovementRepository extends JpaRepository<HistoryMovement, Long> {
    boolean existsByAsignaturaAfectadaAndAsignaturaAgregadaTrue(Asignatura asignatura);
    boolean existsByAsignaturaAfectadaAndAsignaturaRemovidaTrue(Asignatura asignatura);
    List<HistoryMovement> findByCambiosAceptadosIsNullAndCodigo(Integer codigo);
    List<HistoryMovement> findByCambiosAceptadosTrueAndCodigo(Integer codigo);

}
