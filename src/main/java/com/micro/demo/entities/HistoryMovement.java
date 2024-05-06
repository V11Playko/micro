package com.micro.demo.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "historyMovement")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryMovement implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String correoDocente;

    @ManyToOne
    @JoinColumn(name = "asignatura_id")
    private Asignatura asignaturaAfectada;

    @ManyToOne
    @JoinColumn(name = "pensum_id")
    private Pensum pensum;

    @ManyToOne
    @JoinColumn(name = "programa_academico_id")
    private ProgramaAcademico programaAcademico;

    private Boolean cambiosAceptados;

    private boolean asignaturaAgregada;

    private boolean asignaturaActualizada;

    private boolean asignaturaRemovida;

    @ElementCollection
    @CollectionTable(name = "atributo_modificado", joinColumns = @JoinColumn(name = "history_movement_id"))
    @MapKeyColumn(name = "atributo")
    @Column(name = "valor")
    private Map<String, String> atributosModificados;

    private LocalDateTime fechaMovimiento;

    private Integer codigo;
}
