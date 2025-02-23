package com.playnomm.wallet.config.filter;

import com.playnomm.wallet.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
@WebFilter(filterName = "LogFilter")
@Order(2)
public class LogFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		long start = new Date ().getTime ();
		filterChain.doFilter (request, response);
		Parser uaParser = new Parser ();
		Client c = uaParser.parse (request.getHeader ("user-agent"));
		String userLog = CommonUtil.getClientIP (request) + " " + c.os.family + "(v" + c.os.major + "." + c.os.minor + ") " + c.userAgent.family + "(v" + c.userAgent.major + "." + c.userAgent.minor + ")";
		long endTime = new Date ().getTime () - start;
		if (log.isInfoEnabled ()) log.info (request.getMethod ().replaceAll ("[\r\n]", "") + " " + request.getRequestURI ().replaceAll ("[\r\n]", "") + " " + endTime + "(ms) " + userLog.replaceAll ("[\r\n]", ""));

	}
}
