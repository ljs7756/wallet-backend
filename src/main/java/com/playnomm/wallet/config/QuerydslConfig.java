package com.playnomm.wallet.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Configuration
public class QuerydslConfig {
	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public EntityManager entityManager () {
		return entityManager;
	}

	@Bean
	public JPAQueryFactory jpaQueryFactory () {
		return new JPAQueryFactory (entityManager);
	}
}
