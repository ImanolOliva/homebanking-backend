package com.example.security.demo.DTO;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovimientosDTO {

    private String tipoOperacion;

    private Double monto;

    private Double cbu;

    private Date fechaEnvio;

    private String titular;
}
