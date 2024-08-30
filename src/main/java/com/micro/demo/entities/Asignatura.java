package com.micro.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.micro.demo.entities.enums.Semesters;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "asignatura")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Asignatura implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @Column(unique = true)
    private Integer codigo;
    private String accFormacionInv;
    private String bibliografia;
    private Integer creditos;
    private String hp;
    private String ht;
    private String had;
    private String hti;
    private String hadhti;
    private String justificacion;
    private String metodologia;

    @ElementCollection
    private List<String> objetivos;

    @Enumerated(EnumType.STRING)
    private Semesters semestre;

    private String tipoCredito;
    private String tipoCurso;

    @ManyToOne
    @JoinColumn(name = "area_formacion_id")
    private AreaFormacion areaFormacion;

    @ManyToOne
    @JoinColumn(name = "competencia_id")
    private Competencia competencia;

    @JsonIgnore
    @OneToMany(mappedBy = "asignatura")
    private List<AsignaturaPensum> asignaturaPensum;

    @JsonIgnore
    @OneToMany(mappedBy = "asignatura")
    private List<AsignaturaDocente> asignaturaDocentes;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AsignaturaPreRequisito> preRequisitos = new ArrayList<>();

}
