package com.example.security.demo.Exepciones;

public class UserNameInvalid extends ErroresCustom{

    public UserNameInvalid(String leyenda) {
        super("el userName no es valido");
    }
}
