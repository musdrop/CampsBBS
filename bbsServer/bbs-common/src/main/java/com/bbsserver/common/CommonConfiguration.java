package com.bbsserver.common;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.apache.catalina.filters.CorsFilter;

@Configuration
@ComponentScan({"com.bbsserver.common.web","com.bbsserver.common.service"})
@MapperScan({"com.bbsserver.common.mapper"})
public class CommonConfiguration implements WebMvcConfigurer {

    @Value("${file.image.path}")
    private String imagePath;

    @Bean
    public CorsFilter corsFilter(){
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //放行哪些原始域
        config.addAllowedOriginPattern("*");
        //是否发送 Cookie
        config.setAllowCredentials(true);
        //放行哪些请求
        config.addAllowedMethod("*");
        //放行哪些请求头
        config.addAllowedHeader("*");
        //暴露哪些头部信息
        config.addExposedHeader("*");
        //2.添加路径映射
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        //3.返回新的CorsFilter
        return new CorsFilter(source);
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //添加分页插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        //设置请求的页面大于最大页的操作，true调回首页，false继续请求，默认是false
        paginationInnerInterceptor.setOverflow(false);
        //单页分页的条数限制
        paginationInnerInterceptor.setMaxLimit(20L);
        //设置数据库类型
        paginationInnerInterceptor.setDbType(DbType.ORACLE);

        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}
