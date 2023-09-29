package com.example.security.demo.Exepciones;

public class PasswordErronea extends ErroresCustom {

    public PasswordErronea(String leyenda) {
        super("La password es invalida");
    }

    public PasswordErronea(){

    }
}
