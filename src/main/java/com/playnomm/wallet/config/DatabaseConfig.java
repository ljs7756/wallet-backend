package com.playnomm.wallet.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author : hzn
 * @date : 2022/12/16
 * @description :
 */
@Configuration
@MapperScan(value="com.playnomm.wallet.mapper", sqlSessionFactoryRef="sqlSessionFactory")
@RequiredArgsConstructor
public class DatabaseConfig {
	private final ApplicationContext applicationContext;

//	@Primary
//	@Bean
//
//	public HikariConfig hikariConfig () {
//		return new HikariConfig ();
//	}

	@Primary
	@Bean(name = "dataSource")
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public DataSource dataSource () {
		return DataSourceBuilder.create().build();
	}

	@Primary
	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory (@Qualifier("dataSource") DataSource dataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean ();
		bean.setDataSource (dataSource);
		bean.setMapperLocations (applicationContext.getResources ("classpath:mybatis/mapper/**/**.xml"));
		bean.setConfigLocation (applicationContext.getResource ("classpath:mybatis/mybatis-config.xml"));
		bean.setTypeAliasesPackage ("com.playnomm.wallet");
		return bean.getObject ();
	}

	@Primary
	@Bean(name = "sqlSessionTemplate")
	public SqlSessionTemplate sqlSessionTemplate (@Autowired SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate (sqlSessionFactory);
	}
}
