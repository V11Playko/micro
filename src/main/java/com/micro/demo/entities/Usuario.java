package com.micro.demo.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El campo 'nombre' es obligatorio")
    @Pattern(regexp = "^[^0-9]+$", message = "El campo 'name' no debe contener números")
    private String nombre;

    @NotBlank(message = "El campo 'apellido' es obligatorio")
    @Pattern(regexp = "^[^0-9]+$", message = "El campo 'surname' no debe contener números")
    private String apellido;

    @Pattern(regexp = "^\\+?57\\s(3[0-2]|7[0-1])\\d{8}$", message = "El campo 'phoneNumber' debe ser un número de teléfono válido. Introduzca el formato +57 3...")
    @NotBlank(message = "El campo 'numeroCelular' es obligatorio")
    @Column(unique = true)
    private String numeroCelular;

    @NotBlank(message = "El campo 'correo' es obligatorio")
    @Email(message = "El campo 'correo' debe ser una dirección de correo electrónico válida. Introduzca el formato nombre@ejemplo.com")
    @Column(unique = true)
    private String correo;

    @NotBlank(message = "El campo 'contraseña' es obligatorio")
    private String contraseña;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;
}