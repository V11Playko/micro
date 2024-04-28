package com.micro.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "asociacionCompetenciaResultado")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AsociacionCompetenciaResultado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "resultado_aprendizaje_id")
    private ResultadoAprendizaje resultadoAprendizaje;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;
}
