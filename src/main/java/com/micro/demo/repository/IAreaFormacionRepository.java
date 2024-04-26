package com.micro.demo.repository;

import com.micro.demo.entities.AreaFormacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAreaFormacionRepository extends JpaRepository<AreaFormacion, Long> {
}
