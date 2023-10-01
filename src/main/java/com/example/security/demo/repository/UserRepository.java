package com.example.security.demo.repository;


import com.example.security.demo.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {

    @Query(value = "SELECT * FROM users WHERE id = :id",nativeQuery = true)
    UserEntity filterUserById(@Param("id")Long id);

    UserEntity findByEmailAndPassword(String email,Integer password);

    UserEntity findByEmail(String email);

    UserEntity findBySalary(Double salary);

    UserEntity findById(Double id);



}
