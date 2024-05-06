package com.micro.demo.service;

import com.micro.demo.entities.HistoryMovement;

import java.util.List;

public interface IHistoryMovementService {
    List<HistoryMovement> getAllMovements(int pagina, int elementosXpagina);
    void agregarAsignatura(HistoryMovement historyMovement);
    void removerAsignatura(HistoryMovement historyMovement);
    void actualizarAsignatura(HistoryMovement historyMovement);
    void aprobarRechazarCambiosDespuesPeriodoModificacion(boolean aceptarCambios, Integer codigo);
    void aplicarCambiosPropuestos(Integer codigo);
}
