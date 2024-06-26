package com.micro.demo.repository;

import com.micro.demo.entities.Pensum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPensumRepository extends JpaRepository<Pensum, Long> {
    Pensum findTopByOrderByIdDesc();
}
