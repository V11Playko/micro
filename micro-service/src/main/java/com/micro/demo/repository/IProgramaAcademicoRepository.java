package com.micro.demo.repository;

import com.micro.demo.entities.ProgramaAcademico;
import com.micro.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProgramaAcademicoRepository extends JpaRepository<ProgramaAcademico, Long> {
    ProgramaAcademico findByNombre(String nombre);
    ProgramaAcademico findByDirector(Usuario director);
    ProgramaAcademico findByDirectorCorreo(String correo);
}
