package com.example.security.demo.repository;

import com.example.security.demo.model.Tarjetas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarjetasRepository extends JpaRepository<Tarjetas,Long> {
}
