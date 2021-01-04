package com.klasdq.demo;

import com.klasdq.demo.persistence.mybatis.conf.MybatisConfiguration;
import com.klasdq.demo.web.conf.SwaggerConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({MybatisConfiguration.class, SwaggerConfiguration.class})
@MapperScan(basePackages = "com.klasdq.demo.persistence.dao")
@ComponentScan(basePackages = {"com.klasdq.demo.web.controller","com.klasdq.demo.service.impl"})
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
