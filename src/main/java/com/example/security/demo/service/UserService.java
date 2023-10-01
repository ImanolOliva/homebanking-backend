package com.example.security.demo.service;


import com.example.security.demo.DTO.MovimientosDTO;
import com.example.security.demo.DTO.UserDTO;
import com.example.security.demo.Exepciones.ErroresCustom;
import com.example.security.demo.Exepciones.PasswordErronea;
import com.example.security.demo.model.Movimientos;
import com.example.security.demo.model.UserEntity;
import com.example.security.demo.repository.MovimientosRepository;
import com.example.security.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserService {



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovimientosRepository movimientosRepository;

    public UserEntity saveUser(UserEntity userEntity){
        boolean existe;
        try{
            if(userEntity.getPassword() > 99999){
                throw new PasswordErronea();
            }
        }catch(ErroresCustom erroresCustom){
            erroresCustom.getMessage();
            return null;
        }
        userRepository.save(userEntity);
        return userEntity;

    }

    public boolean existeUser(String email,Integer password){
        UserEntity user = this.userRepository.findByEmailAndPassword(email,password);
        return user != null;
    }

    public List<UserEntity> getAllUser(UserEntity userEntity){
        List<UserEntity> list = this.userRepository.findAll();
        if(list.isEmpty()){
            return null;
        }
        return list;
    }

    public String findUsernameByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        String userName = userEntity != null ? userEntity.getUserName() : null;
        System.out.println("Nombre de usuario obtenido: " + userName);
        return userName;
    }

    public Double findSalary(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);
        Double salary = userEntity != null ? userEntity.getSalary():null;
        System.out.println("SALARIO OBTENIDO: " + salary);
        return salary;
    }

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public boolean updateUser(UserDTO userDTO){
        UserEntity userEntity  = this.userRepository.findByEmail(userDTO.getEmail());

        if(userEntity != null){
            userEntity.setSalary(userDTO.getSalary());
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    public Movimientos saveOperaciones(Movimientos movimientos){
       return this.movimientosRepository.save(movimientos);
    }


    public List<Movimientos> gettAllListMovimientos(UserDTO userDTO){

        List<Movimientos> movimientosList = this.movimientosRepository.findAll();
        String email = userDTO.getEmail();

        List<Movimientos> movimientosConEmail = new ArrayList<>();

        for (Movimientos movimiento : movimientosList) {
            if (movimiento.getTitular().equals(email)) {
                movimientosConEmail.add(movimiento);
            }
        }

        return movimientosConEmail;
    }





}
