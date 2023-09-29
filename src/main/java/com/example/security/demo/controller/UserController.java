package com.example.security.demo.controller;


import com.example.security.demo.DTO.UserDTO;
import com.example.security.demo.model.UserEntity;
import com.example.security.demo.service.UserService;
import lombok.extern.flogger.Flogger;
import org.apache.catalina.User;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileDescriptor;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {


    @Autowired
    private UserService userService;

    @PostMapping( path = "/user/v1")
    public ResponseEntity<?> saveUser(@RequestBody UserEntity userEntity){
      try{
          double salarioAutomatizado = automatizarSalario(String.valueOf(userEntity.getEmail()));
          userEntity.setSalary(salarioAutomatizado);
          this.userService.saveUser(userEntity);
      }catch (Exception exception){
          return new ResponseEntity(exception.getMessage(),HttpStatusCode.valueOf(404));
      }
      return new ResponseEntity<>(userEntity,HttpStatusCode.valueOf(200));
    }
    @PostMapping( path = "/user/v2" )
    public ResponseEntity<UserDTO> verifyUser(@RequestBody UserDTO userDTO){
        String gmail = userDTO.getEmail();
        Integer password = userDTO.getPassword();

        double salarioAutomatizado = automatizarSalario(String.valueOf(userDTO.getEmail()));
        userDTO.setSalary(salarioAutomatizado);

        String userName =  this.userService.findUsernameByEmail(userDTO.getEmail());
        userDTO.setUserName(userName);

        if(this.userService.existeUser(gmail,password)){
            return  ResponseEntity.ok(userDTO);
        }
        return new ResponseEntity("El usuario no se encuetra registrado o las credenciales son incorrectas",HttpStatusCode.valueOf(404));
    }


    @GetMapping(  path =  "/user")
    public ResponseEntity<?> getAllUsers(UserEntity userEntity){
        List<UserEntity> listUser =   this.userService.getAllUser(userEntity);

        if(listUser == null){
            return new ResponseEntity<>("No existen personas registradas",HttpStatusCode.valueOf(200));
        }
        return new ResponseEntity<>(listUser,HttpStatusCode.valueOf(200));
    }


    @PostMapping(
            path= "/user/v3"
    )
    public ResponseEntity<UserDTO> WithdrawalDiner(@RequestBody UserDTO userDTO){
        // Obtengo el Email que me viene en el frontend
        String email = userDTO.getEmail();
        //Creo una variable auxiliar
        Double salaryBd;
        //Este metodo me va a traer el salario del usuario que mando del frontend
        salaryBd = this.userService.findSalary(email);
        //
        if(userDTO.getSalary() > salaryBd || userDTO.getSalary() < 0){
            return new ResponseEntity<>(HttpStatusCode.valueOf(400));
        }
        userDTO.setSalary(salaryBd-userDTO.getSalary());
        //Guardo los datos actualizados en la base de datos
        this.userService.updateUser(userDTO);

        return new ResponseEntity<>(userDTO, HttpStatusCode.valueOf(200));
    }

    @PostMapping(
            path="/user/v4"
    )
    public ResponseEntity<UserDTO> DepositDinero(@RequestBody UserDTO userDTO){
        // Obtengo el Email que me viene en el frontend
        String email = userDTO.getEmail();
        //Creo una variable auxiliar
        Double salaryBd;
        //Este metodo me va a traer el salario del usuario que mando del frontend
        salaryBd = this.userService.findSalary(email);
        //
        if(userDTO.getSalary() < 0){
            return new ResponseEntity("El ingreso de dinero no es valido",HttpStatusCode.valueOf(400));
        }
        userDTO.setSalary(salaryBd+userDTO.getSalary());
        //Guardo los datos actualizados en la base de datos
        this.userService.updateUser(userDTO);

        return new ResponseEntity<>(userDTO, HttpStatusCode.valueOf(200));
    }

    private double automatizarSalario(String email){
        return email.length() > 4?50000 : 20000;
    }


}


