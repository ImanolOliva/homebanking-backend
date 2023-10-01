package com.example.security.demo.DTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TarjetasDTO {

    private String nombre;

    private Double saldo;

    private Double nroTarjeta;

    private String fechaVencimiento;



}
