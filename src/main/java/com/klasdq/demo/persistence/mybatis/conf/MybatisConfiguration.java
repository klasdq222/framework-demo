package com.klasdq.demo.persistence.mybatis.conf;

import com.klasdq.demo.persistence.mybatis.intercepts.BatchSaveInterceptor;
import com.klasdq.demo.persistence.mybatis.intercepts.PageInterceptor;
import com.klasdq.demo.persistence.mybatis.intercepts.SetBatchParameterInterceptor;
import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MybatisConfiguration {
    @Bean
    Interceptor[] interceptors() {
        return new Interceptor[]{
                new PageInterceptor(),
                new BatchSaveInterceptor(),
                new SetBatchParameterInterceptor()
        };
    }
}
