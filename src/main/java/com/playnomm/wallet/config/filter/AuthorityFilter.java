package com.playnomm.wallet.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.config.wrapper.AuthorityRequestWrapper;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : hzn
 * @date : 2023/02/23
 * @description :
 */
public class AuthorityFilter extends OncePerRequestFilter {
	private final ObjectMapper objectMapper = new ObjectMapper ();

	@Override
	protected void doFilterInternal (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
		try {
			if ("application/json".equals (request.getContentType ())) filterChain.doFilter (new AuthorityRequestWrapper (request), response);
			else filterChain.doFilter (request, response);
		} catch (WalletException e) {
			exceptionResponse (response, e);
		} catch (Exception e) {
			String message = ObjectUtils.isEmpty (e.getMessage ()) ? StatusCode.INTERNAL_SERVER_ERROR.getMessage () : e.getMessage ();
			exceptionResponse (response, new WalletException (StatusCode.INTERNAL_SERVER_ERROR.getCode (), message));

		}
	}

	private void exceptionResponse (HttpServletResponse response, WalletException e) throws IOException {
		ResultDTO resultDTO = new ResultDTO (e.getCode (), e.getMessage ());
		String responseBody = objectMapper.writeValueAsString (resultDTO);
		response.setStatus (200);
		response.setContentType ("application/json");
		response.setContentLength (responseBody.length ());
		response.getWriter ().println (responseBody);
	}
}
