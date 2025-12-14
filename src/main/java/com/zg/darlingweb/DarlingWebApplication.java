package com.zg.darlingweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.zg.darlingweb.mapper")
public class DarlingWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DarlingWebApplication.class, args);
    }

}
