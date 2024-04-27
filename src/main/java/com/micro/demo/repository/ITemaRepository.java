package com.micro.demo.repository;

import com.micro.demo.entities.Tema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITemaRepository extends JpaRepository<Tema,Long> {
    List<Tema> findAllByIdInAndEstatusIsTrue(List<Long> ids);
}
