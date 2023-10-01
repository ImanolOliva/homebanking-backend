package com.example.security.demo.controller;


import com.example.security.demo.DTO.*;
import com.example.security.demo.model.Movimientos;
import com.example.security.demo.model.Tarjetas;
import com.example.security.demo.model.UserEntity;
import com.example.security.demo.service.UserService;
import lombok.extern.flogger.Flogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        //Obtengo el nombre de la base de datos
        //Para trabajarlo de forma dinamica en pantalla

        String userName =  this.userService.findUsernameByEmail(userDTO.getEmail());
        userDTO.setUserName(userName);

        //Obtengo el salario de la base de datos
        //Para trabajarlo de forma dinamica en pantalla
        Double salary = this.userService.findSalary(userDTO.getEmail());
        userDTO.setSalary(salary);

        //Pregunto si existe en bd y lo devuelvo
        if(this.userService.existeUser(gmail,password)){
            this.getMenuItem();
            return  ResponseEntity.ok(userDTO);
        }

        return new ResponseEntity("El usuario no se encuetra registrado o las credenciales son incorrectas",HttpStatusCode.valueOf(404));
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

        //Voy armando la tabla de transacciones
        Movimientos movimientos = new Movimientos();
        movimientos.setMonto(userDTO.getSalary());
        movimientos.setFechaEnvio(new Date());
        movimientos.setTitular(userDTO.getEmail());
        movimientos.setTipoOperacion("EXTRACCION");
        this.userService.saveOperaciones(movimientos);
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
        //Voy armando la tabla de transacciones
        Movimientos movimientos = new Movimientos();
        movimientos.setMonto(userDTO.getSalary());
        movimientos.setFechaEnvio(new Date());
        movimientos.setTitular(userDTO.getEmail());
        movimientos.setTipoOperacion("DEPOSITO");
        this.userService.saveOperaciones(movimientos);

        return new ResponseEntity<>(userDTO, HttpStatusCode.valueOf(200));
    }


    @PostMapping(
            path = "/user/v5"
    )
    public ResponseEntity<UserDTO> transferencias(@RequestBody TransferenciaDTO transferenciaDTO){
        Double cbu = transferenciaDTO.getCbu();

        Double dinero = transferenciaDTO.getDinero();
        String email = transferenciaDTO.getEmail();
        Double salaryBd = this.userService.findSalary(email);
        salaryBd = salaryBd - dinero;


        //Actualizo los datos en la tabla
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setSalary(salaryBd);
        this.userService.updateUser(userDTO);

        //Voy armando la tabla de transacciones
        Movimientos movimientos = new Movimientos();
        movimientos.setMonto(userDTO.getSalary());
        movimientos.setFechaEnvio(new Date());
        movimientos.setDestino(cbu);
        movimientos.setTitular(userDTO.getEmail());
        movimientos.setTipoOperacion("TRANSFERENCIA");
        this.userService.saveOperaciones(movimientos);
        return new ResponseEntity(userDTO,HttpStatusCode.valueOf(200));
    }
    @PostMapping(  path = "/user/v6")
    public List<Movimientos> getAllUsers(@RequestBody UserDTO userDTO){
        try{
            List<Movimientos> movimientosList;
            movimientosList = this.userService.gettAllListMovimientos(userDTO);
            return  movimientosList;
        }catch(Exception e) {
            return null;
        }

    }

    @GetMapping(
            path = "/user/v7"
    )
    public List<MenuItemDTO> getMenuItem(){

        List<MenuItemDTO> rutas = new ArrayList<>();
        rutas.add(new MenuItemDTO("Inicio","/home"));
        rutas.add(new MenuItemDTO("Retiro dinero", "/withdrawal"));
        rutas.add(new MenuItemDTO("Deposito","/deposit"));
        rutas.add(new MenuItemDTO("Tranferencias","/transfers"));
        rutas.add(new MenuItemDTO("Salida","/go-out"));

        return rutas;
    }


    @PostMapping(
            path = "/user/v8"
    )
    public Tarjetas getTarjetas(@RequestBody TarjetasDTO tarjetasDTO){
        Tarjetas tarjetas = new Tarjetas();
        //COMPLETO EL DTO
        String fechaVencimiento = "12/25";
        String nroTarjeta = "1234 4325 3423 5435";
        tarjetasDTO.setNroTarjeta(nroTarjeta);
        tarjetasDTO.setFechaVencimiento(fechaVencimiento);

        //ASIGNO LOS DATOS A LA ENTIDAD
        tarjetas.setSaldo(tarjetasDTO.getSaldo());
        tarjetas.setNombre(tarjetasDTO.getNombre());
        tarjetas.setNroTarjeta(tarjetasDTO.getNroTarjeta());
        tarjetas.setFechaVencimiento(tarjetasDTO.getFechaVencimiento());

        //GUARDO LOS DATOS EN LA BASE DE DATOS
        this.userService.saveTarjetas(tarjetas);
        return tarjetas;
    }


    private double automatizarSalario(String email){
        return email.length() > 4?50000 : 20000;
    }




}


