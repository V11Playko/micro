package com.micro.demo.repository;

import com.micro.demo.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {
    Role buscarPorNombre(String nombre);
}
