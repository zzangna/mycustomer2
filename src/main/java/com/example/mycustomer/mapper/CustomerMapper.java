package com.example.mycustomer.mapper;

import com.example.mycustomer.dto.CustomerDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerMapper {
    // 회원가입
    void insertCustomer(CustomerDTO customerDTO);
    // 회원 전체 조회
    List<CustomerDTO> selectAll();
    // id로 회원 1명 조회
    CustomerDTO selectOne(String id);
    // 회원 정보 수정
    void updateCustomer(CustomerDTO customerDTO);
    // 회원 삭제
    void deleteCustomer(String id);
    // id, pw 일치 확인 (login) :
    CustomerDTO idPwCheck(@Param("id") String id, @Param("pw") String pw);





}
