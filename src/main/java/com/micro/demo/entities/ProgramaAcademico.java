package com.micro.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "programaAcademico")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProgramaAcademico implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Boolean puedeDescargarPdf;
    private LocalDate fechaInicioModificacion;
    private Integer duracionModificacion;
    @OneToOne
    @JoinColumn(name = "correo_director", referencedColumnName = "correo")
    private Usuario director;
}
