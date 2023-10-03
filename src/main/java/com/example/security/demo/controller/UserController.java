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


    /**
     * Genero las transferencias por cbu. A lo que actualizo los datos
     * en la base de datos y a su ves armo la tabla de movimientos con los datos
     * @param transferenciaDTO
     * @return
     */
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

    /**
     * Obtengo todos los movimientos del usuario en sesíon
     * @param userDTO
     * @return movimientos del usuario
     */
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

    /**
     * Realizo menu dinamico que  venga a buscar al backend
     * @return rutas del frontend
     *
     *
     */ 
    @GetMapping(
            path = "/user/v7"
    )
    public List<MenuItemDTO> getMenuItem(){

        List<MenuItemDTO> rutas = new ArrayList<>();

        rutas.add(new MenuItemDTO("Inicio","/home"));
        rutas.add(new MenuItemDTO("Retiro dinero", "/withdrawal"));
        rutas.add(new MenuItemDTO("Deposito","/deposit"));
        rutas.add(new MenuItemDTO("Tranferencias","/transfers"));
        rutas.add(new MenuItemDTO("Ultimos Movimientos","/transacciones"));

        rutas.add(new MenuItemDTO("Salida","/go-out"));

        return rutas;
    }


    /**
     * Devuelvo las tarjetas que el usuario en sesion tenga asociadas
     * @param tarjetasDTO
     * @return tarjetas
     */
    @PostMapping(
            path = "/user/v8"
    )
        public List<Tarjetas> getTarjetas(@RequestBody TarjetasDTO tarjetasDTO) {

            List<Tarjetas> tarjetas = new ArrayList<>();

            // Tarjeta de débito con datos del DTO
            Tarjetas tarjetaDebito = new Tarjetas();
            tarjetaDebito.setSaldo(tarjetasDTO.getSaldo());
            tarjetaDebito.setNombre(tarjetasDTO.getNombre());
            tarjetaDebito.setNroTarjeta("1234 4325 3423 5435");
            tarjetaDebito.setFechaVencimiento("12/25");
            tarjetaDebito.setTipo("DEBITO");
            tarjetas.add(tarjetaDebito);

            // Tarjeta de crédito hardcodeada
            Tarjetas tarjetaCredito = new Tarjetas();
            tarjetaCredito.setSaldo(8000.00);
            tarjetaCredito.setNombre("Tarjeta de Crédito");
            tarjetaCredito.setNroTarjeta("9876 5432 1234 5678");
            tarjetaCredito.setFechaVencimiento("06/28");
            tarjetaCredito.setTipo("CREDITO");
            tarjetas.add(tarjetaCredito);

            return tarjetas;
        }


    /** Automatizo salario para que el usuario cuando ingrese
     * no tenga su cuenta en 0
     * de igual manera estan todas las operaciones para realizar
     * @param email
     * @return salario
     */
    private double automatizarSalario(String email){
        return email.length() > 4?50000 : 20000;
    }




}


