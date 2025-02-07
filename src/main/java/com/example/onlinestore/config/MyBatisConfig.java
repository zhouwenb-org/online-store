package com.example.onlinestore.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import javax.sql.DataSource;

@Configuration
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/*.xml"));
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        // 开启驼峰命名转换
        configuration.setMapUnderscoreToCamelCase(true);
        // 配置 Java 8 日期时间类型的处理
        configuration.getTypeHandlerRegistry().setDefaultEnumTypeHandler(org.apache.ibatis.type.EnumTypeHandler.class);
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }
} 