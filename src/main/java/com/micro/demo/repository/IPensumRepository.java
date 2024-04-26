package com.micro.demo.repository;

import com.micro.demo.entities.Pensum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

@Repository
public interface IPensumRepository extends JpaRepository<Pensum, Long> {
}
