package com.micro.demo.entities;

import jakarta.persistence.Column;
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
    private String had;
    private String hti;
    private String hadhti;
    private String justificacion;
    private String metodologia;
    private String objetivo;
    private String semestre;
    private String tipoCredito;
    private String tipoCurso;

    @ManyToOne
    @JoinColumn(name = "area_formacion_id")
    private AreaFormacion areaFormacion;

    @ManyToOne
    @JoinColumn(name = "pre_requisito_id")
    private PreRequisito preRequisito;

    @OneToMany(mappedBy = "asignatura")
    private List<AsignaturaPensum> asignaturaPensum;

    @OneToMany(mappedBy = "asignatura")
    private List<AsignaturaDocente> asignaturaDocentes;
}
