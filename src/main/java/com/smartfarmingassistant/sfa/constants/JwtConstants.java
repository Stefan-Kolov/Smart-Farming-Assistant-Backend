package com.smartfarmingassistant.sfa.constants;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Getter
public class JwtConstants {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;
}