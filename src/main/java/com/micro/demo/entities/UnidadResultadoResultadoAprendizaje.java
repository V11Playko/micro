package com.micro.demo.entities;

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
@Table(name = "unidad_resultado_resultado_aprendizaje")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnidadResultadoResultadoAprendizaje implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "unidad_resultado_id")
    private UnidadResultado unidadResultado;

    @ManyToOne
    @JoinColumn(name = "resultado_aprendizaje_id")
    private ResultadoAprendizaje resultadoAprendizaje;
}
