package com.example.mycustomer.mapper;

import com.example.mycustomer.dto.CustomerDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CustomerMapperTest {

    @Autowired
    private CustomerMapper mapper;

    @Test
    public void testInsert() {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId("java4");
        customerDTO.setPw("4444");
        customerDTO.setName("d");
        customerDTO.setBirth("2004-01-01");
        customerDTO.setEmail("java4@gmail.com");
        mapper.insertCustomer(customerDTO);
    }


}