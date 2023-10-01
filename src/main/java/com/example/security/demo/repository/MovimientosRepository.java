package com.example.security.demo.repository;

import com.example.security.demo.model.Movimientos;
import com.example.security.demo.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientosRepository extends JpaRepository<Movimientos,Long> {

}
