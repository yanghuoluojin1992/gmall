package com.atguigu.gmall.usermanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com.atguigu.gmall.usermanage.mapper")
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
public class GmallUsermanageApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmallUsermanageApplication.class, args);
    }

}
