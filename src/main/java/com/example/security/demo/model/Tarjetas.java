package com.example.security.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tarjetas")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tarjetas {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private Double saldo;

    private String nroTarjeta;

    private String fechaVencimiento;

    private String tipo;


}
