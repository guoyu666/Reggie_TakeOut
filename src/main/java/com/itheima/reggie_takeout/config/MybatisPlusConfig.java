package com.itheima.reggie_takeout.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mybatis-plus的分页插件
 */
@Configuration
public class MybatisPlusConfig{

    /**
     * 添加分页插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusinterceptor = new MybatisPlusInterceptor();
        mybatisPlusinterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return mybatisPlusinterceptor;
    }
}
