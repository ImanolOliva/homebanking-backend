package com.example.security.demo.Exepciones;

public class ErroresCustom extends Throwable{

    private String leyenda;

    public ErroresCustom(String leyenda){
        this.leyenda = leyenda;
    }

    public ErroresCustom(){

    }
}
