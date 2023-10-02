package com.example.security.demo.service;


import com.example.security.demo.DTO.MovimientosDTO;
import com.example.security.demo.DTO.TarjetasDTO;
import com.example.security.demo.DTO.UserDTO;
import com.example.security.demo.Exepciones.ErroresCustom;
import com.example.security.demo.Exepciones.PasswordErronea;
import com.example.security.demo.model.Movimientos;
import com.example.security.demo.model.Tarjetas;
import com.example.security.demo.model.UserEntity;
import com.example.security.demo.repository.MovimientosRepository;
import com.example.security.demo.repository.TarjetasRepository;
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

    @Autowired
    private TarjetasRepository tarjetasRepository;


    //METODO PARA PERSISTIR LOS USUARIOS QUE SE REGISTREN
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

    /** METODO PARA REALIZAR EL LOGIN.
     * pregunto si el email (Que debe ser unico, customizado por validociones)
     * y la password existen en la base de datos
     */

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

    /**
     * Traer el nombre de la base de datos buscando por Email, que será unico para cada usuario
     * @param email
     * @return userName
     */

    public String findUsernameByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        String userName = userEntity != null ? userEntity.getUserName() : null;
        System.out.println("Nombre de usuario obtenido: " + userName);
        return userName;
    }

    /**
     *Traer el salario de la base de datos realizando la misma logica,yendo a buscar por email
     * @param email
     * @return salary
     */
    public Double findSalary(String email) {

        UserEntity userEntity = userRepository.findByEmail(email);
        Double salary = userEntity != null ? userEntity.getSalary():null;
        System.out.println("SALARIO OBTENIDO: " + salary);
        return salary;
    }

    /**
     * Buscar por email
     * @param id
     * @return id
     */

    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Actualizar usuario y persistirlo en la base de datos con su informacion actualizada
     * @param userDTO
     * @return
     */
    public boolean updateUser(UserDTO userDTO){
        UserEntity userEntity  = this.userRepository.findByEmail(userDTO.getEmail());

        if(userEntity != null){
            userEntity.setSalary(userDTO.getSalary());
            userRepository.save(userEntity);
            return true;
        }
        return false;
    }

    /**
     * Persistencia de la tabla movimientos la cual registrara los movimientos
     * echos por el usuario
     * @param movimientos
     * @return movimientos
     */
    public Movimientos saveOperaciones(Movimientos movimientos){
       return this.movimientosRepository.save(movimientos);
    }


    /**
     * Recorro la lista de movimientos segun el usuario que la alla usado.
     * Verifico el usuario que la utilizo por su email. Y me traigo los movimientos
     * realizaados por el usuario.
     * @param userDTO
     * @return movimientos(Lista de transacciones)
     */
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

    /**
     * Persisto en base de datos las tarjetas del usuario
     * @param tarjetas
     * @return
     */
    public Tarjetas saveTarjetas(Tarjetas tarjetas){
       return this.tarjetasRepository.save(tarjetas);
    }





}
