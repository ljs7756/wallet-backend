package com.playnomm.wallet.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : hzn
 * @date : 2022/11/11
 * @description :
 */
@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "Bearer oAuth Token";

	@Bean
	public GroupedOpenApi groupedOpenApiV1 () {
		return GroupedOpenApi.builder().group ("v1-definition").pathsToMatch ("/api/v1/**").build();
	}

	@Bean
	public OpenAPI openAPI () {
		return new OpenAPI ()
				.addServersItem(new Server().url("/"))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
				.components(
						new Components()
								.addSecuritySchemes(SECURITY_SCHEME_NAME,
										new SecurityScheme()
												.name(SECURITY_SCHEME_NAME)
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT")
								)
				)
				.info (new Info ().title ("Wallet API").description ("Wallet API 명세").version ("v1.0"));
	}

}
