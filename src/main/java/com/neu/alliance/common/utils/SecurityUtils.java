package com.neu.alliance.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Slf4j

public class SecurityUtils {
    public static String encrypt(String password) throws Exception {
        if(!StringUtils.hasLength(password)){
            log.info("密码为空");
            throw new Exception("密码不能为空");
        }
        // 生成盐
        String salt = UUID.randomUUID().toString().replace("-", "");
        //将密码与随机生成的盐值拼接后,进行md5加密
        String securityPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
        return salt + securityPassword;
    }

    public static boolean verify(String password, String sqlPassword){
        if(!StringUtils.hasLength(password) || !StringUtils.hasLength(sqlPassword)){
            return false;
        }

        if(sqlPassword.length() != 64){
            return false;
        }

        String salt = sqlPassword.substring(0, 32);

        String securityPassword = DigestUtils.md5DigestAsHex((password + salt).getBytes(StandardCharsets.UTF_8));
        return sqlPassword.equals(salt + securityPassword);
    }
}
