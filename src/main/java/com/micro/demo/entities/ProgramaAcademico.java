package com.micro.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicioModificacion;
    private Integer duracionModificacion;
    @OneToOne
    @JoinColumn(name = "correo_director", referencedColumnName = "correo")
    private Usuario director;
    @OneToMany(mappedBy = "programaAcademico", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pensum> pensums;
}
