package com.micro.demo.repository;

import com.micro.demo.entities.Pensum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPensumRepository extends JpaRepository<Pensum, Long> {
}
