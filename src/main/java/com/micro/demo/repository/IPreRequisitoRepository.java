package com.micro.demo.repository;

import com.micro.demo.entities.PreRequisito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPreRequisitoRepository extends JpaRepository<PreRequisito, Long> {
}
