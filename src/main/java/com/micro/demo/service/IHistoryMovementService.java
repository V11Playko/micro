package com.micro.demo.service;

import com.micro.demo.entities.HistoryMovement;

import java.util.Map;

public interface IHistoryMovementService {
    Map<String, Object> getAllMovements(Integer pagina, Integer elementosXpagina);
    HistoryMovement getHistoryMovement(Long id);
    void agregarAsignatura(HistoryMovement historyMovement);
    void removerAsignatura(HistoryMovement historyMovement);
    void actualizarAsignatura(HistoryMovement historyMovement);
    void aprobarRechazarCambiosDespuesPeriodoModificacion(boolean aceptarCambios, Integer codigo, String reasonMessage);
    void aplicarCambiosPropuestos(Integer codigo);
}
