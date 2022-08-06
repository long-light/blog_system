package com.longzx.blog.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtils {

    /**
     * jwt 有三部分组成：A.B.C
     *
     * A：Header，{“type”:“JWT”,“alg”:“HS256”} 固定
     *
     * B：playload，存放信息，比如，用户id，过期时间等等，可以被解密，不能存放敏感信息
     *
     * C: 签证，A和B加上秘钥 加密而成，只要秘钥不丢失，可以认为是安全的。

     */

    //JWT中的密钥，用于加密C部分
    private static final String jwtToken = "123456Longzx!@#$$";

    public static String createToken(Long userId){
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId",userId);
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, jwtToken) // 签发算法，秘钥为jwtToken -->A部分
                .setClaims(claims) // body数据，要唯一，自行设置  -->B部分
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 24L * 60 * 60 * 60 * 1000));// 设置token的有效时间为一天
        return jwtBuilder.compact(); //-->C部分，进行加密，生成token
    }

    public static Map<String, Object> checkToken(String token){
        try {
            Jwt parse = Jwts.parser().setSigningKey(jwtToken).parse(token);
            return (Map<String, Object>) parse.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

}


