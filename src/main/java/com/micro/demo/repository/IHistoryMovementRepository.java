package com.micro.demo.repository;

import com.micro.demo.entities.Asignatura;
import com.micro.demo.entities.HistoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IHistoryMovementRepository extends JpaRepository<HistoryMovement, Long> {
    boolean existsByAsignaturaAfectadaAndAsignaturaAgregadaTrueAndCambiosAceptadosNull(Asignatura asignatura);
    boolean existsByAsignaturaAfectadaAndAsignaturaRemovidaTrueAndCambiosAceptadosNull(Asignatura asignatura);
    List<HistoryMovement> findByCambiosAceptadosIsNullAndCodigo(Integer codigo);
    List<HistoryMovement> findByCambiosAceptadosTrueAndCodigo(Integer codigo);
    List<HistoryMovement> findByPensumIdAndCambiosAceptadosIsTrue(Long pensumId);

}
