package com.neu.alliance.dto;
import lombok.Data;

/**
 * @Title: LoginDto
 * @Author 曦
 * @Date 2025/6/17 14:18
  */

@Data
public class LoginDTO {
    private String username;
    private String password;
    private String role;
}