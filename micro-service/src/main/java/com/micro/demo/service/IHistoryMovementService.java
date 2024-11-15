package com.micro.demo.service;

import com.micro.demo.controller.dto.HistoryMovementDto;
import com.micro.demo.entities.HistoryMovement;

import java.util.Map;

public interface IHistoryMovementService {
    Map<String, Object> getAllMovements(Integer pagina, Integer elementosXpagina);
    HistoryMovement getHistoryMovement(Long id);
    void agregarAsignatura(HistoryMovementDto historyMovementDto);
    void removerAsignatura(HistoryMovementDto historyMovementDto);
    void actualizarAsignatura(HistoryMovementDto historyMovementDto);
    void aprobarRechazarCambiosDespuesPeriodoModificacion(boolean aceptarCambios, Integer codigo, String reasonMessage);
    void aplicarCambiosPropuestos(Integer codigo);
}
