package com.example.mycustomer.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CustomerDTO {

    private String id;
    private String pw;
    private String name;
    private String email;
    private LocalDateTime reg;
}
