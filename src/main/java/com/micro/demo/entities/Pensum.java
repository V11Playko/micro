package com.micro.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "pensum")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Pensum implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer creditosTotales;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private boolean estatus;

    @ManyToOne
    @JoinColumn(name = "programa_academico_id")
    private ProgramaAcademico programaAcademico;

    @OneToMany(mappedBy = "pensum")
    private List<Asignatura> asignaturas;
}
