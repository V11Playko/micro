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
@Table(name = "unidad")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Unidad implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String had;
    private String hti;
    private String nombre;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL)
    private List<Tema> temas;

    @OneToMany(mappedBy = "unidad", cascade = CascadeType.ALL)
    private List<UnidadResultadoAprendizaje> resultadoAprendizaje;

    @ManyToOne
    @JoinColumn(name = "asignatura_codigo", referencedColumnName = "codigo")
    private Asignatura asignatura;
}
