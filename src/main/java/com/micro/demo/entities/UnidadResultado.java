package com.micro.demo.entities;

import jakarta.persistence.CascadeType;
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
import java.util.List;

@Entity
@Table(name = "unidadResultado")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnidadResultado implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String corteEvaluacion;
    private String criterioDesempeno;
    private String instrumentoEvaluacion;
    private String tipoEvidencia;
    private boolean estatus;

    @ManyToOne
    @JoinColumn(name = "unidad_id")
    private Unidad unidad;
}
