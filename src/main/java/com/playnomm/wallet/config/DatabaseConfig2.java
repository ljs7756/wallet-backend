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

import javax.sql.DataSource;

/**
 * @author : hzn
 * @date : 2022/12/16
 * @description :
 */
@Configuration
@MapperScan(value="com.playnomm.wallet.mapper2", sqlSessionFactoryRef="sqlSessionFactory2")
@RequiredArgsConstructor
public class DatabaseConfig2 {
	private final ApplicationContext applicationContext;

	@Bean(name = "dataSource2")
	@ConfigurationProperties(prefix = "spring.datasource.hikari2")
	public DataSource dataSource () {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "sqlSessionFactory2")
	public SqlSessionFactory sqlSessionFactory2 (@Qualifier("dataSource2") DataSource dataSource2) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean ();
		bean.setDataSource (dataSource2);
		bean.setMapperLocations (applicationContext.getResources ("classpath:mybatis/mapper2/**/**.xml"));
		bean.setConfigLocation (applicationContext.getResource ("classpath:mybatis/mybatis-config.xml"));
		bean.setTypeAliasesPackage ("com.playnomm.wallet");
		return bean.getObject ();
	}

	@Bean(name = "sqlSessionTemplate2")
	public SqlSessionTemplate sqlSessionTemplate2 (@Qualifier("sqlSessionFactory2") SqlSessionFactory sqlSessionFactory2) {
		return new SqlSessionTemplate (sqlSessionFactory2);
	}
}
