package com.micro.demo.repository;

import com.micro.demo.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCorreo(String correo);
    boolean existsByCorreo(String correo);
}