package com.playnomm.wallet.config.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//@Component
//@WebFilter(filterName = "CORSFilter")
//@Order(1)
//public class CORSFilter extends OncePerRequestFilter {
//	@Value("${wallet.cors.allowed.domain}")
//	private List<String> corsAllowedDomains;
//
//	@Override
//	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//		corsAllowedDomains.stream ().forEach (domain -> response.addHeader ("Access-Control-Allow-Origin", domain));
//		response.setHeader ("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS");
//		response.setHeader ("Access-Control-Max-Age", "3600");
//
//		if (request.getMethod ().equals ("OPTIONS")) {
//			response.setHeader ("Access-Control-Allow-Headers", "*");
//			response.flushBuffer ();
//		} else {
//			response.setHeader ("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-PaymentType, Content-Disposition, Accept");
//			filterChain.doFilter (request, response);
//		}
//	}
//}


