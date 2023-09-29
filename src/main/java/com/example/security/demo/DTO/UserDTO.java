package com.example.security.demo.DTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class UserDTO {

    private Long id;
    private Double salary;
    private String userName;
    private String email;
    private Integer password;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }

}
