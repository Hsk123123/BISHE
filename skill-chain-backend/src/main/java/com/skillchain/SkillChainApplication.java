package com.skillchain;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.skillchain.mapper")
public class SkillChainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkillChainApplication.class, args);
    }
}