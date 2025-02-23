package com.playnomm.wallet.config.wrapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.config.security.CustomUserDetails;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author : hzn
 * @date : 2023/02/23
 * @description :
 */
public class AuthorityRequestWrapper extends HttpServletRequestWrapper {
	private final List<String> authParameters = List.of ("userSn", "userCmmnSn");
	private final ObjectMapper objectMapper = new ObjectMapper ();
	private ServletInputStream body;

	public AuthorityRequestWrapper (HttpServletRequest request) {
		super (request);
		try {
			body = getInputStream (request);
		} catch (WalletException e) {
			throw e;
		} catch (Exception e) {
			String message = ObjectUtils.isEmpty (e.getMessage ()) ? StatusCode.BASIC_ERROR.getMessage () : e.getMessage ();
			throw new WalletException (StatusCode.INTERNAL_SERVER_ERROR.getCode (), message);
		}
	}

	public ServletInputStream getInputStream () {
		return body;
	}

	private ServletInputStream getInputStream (HttpServletRequest request) throws IOException {
		// RequestBody 권한 체크
		BufferedReader br = new BufferedReader (new InputStreamReader (request.getInputStream (), StandardCharsets.UTF_8));
		String line;
		StringBuilder sb = new StringBuilder ();
		while ((line = br.readLine ()) != null) {
			sb.append (line);
		}
		String body = sb.toString ().replaceAll ("<", "&lt;").replaceAll (">", "&gt;");
		br.close ();

		Map<String, Object> bodyMap = objectMapper.readValue (body, Map.class);
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext ().getAuthentication ().getPrincipal ();
		for (String authParameter : authParameters) {
			if (bodyMap.containsKey (authParameter)) {
				checkAuthority (authParameter, bodyMap.get (authParameter), customUserDetails);
			}
		}

		ByteArrayInputStream bais = new ByteArrayInputStream (body.getBytes (StandardCharsets.UTF_8));
		ServletInputStream sis = new ServletInputStream () {
			@Override
			public int read () throws IOException {
				return bais.read ();
			}

			@Override
			public void setReadListener (ReadListener readListener) {
			}

			@Override
			public boolean isReady () {
				return false;
			}

			@Override
			public boolean isFinished () {
				return false;
			}
		};
		return sis;
	}

	private void checkAuthority (String parameterName, Object value, CustomUserDetails customUserDetails) {
		if (!ObjectUtils.isEmpty (parameterName) && !ObjectUtils.isEmpty (value) && customUserDetails != null)
			if ((authParameters.get (0).equals (parameterName) && !customUserDetails.getUserSn ().equals (value))
					|| (authParameters.get (1).equals (parameterName) && !customUserDetails.getUserCmmnSn ().equals (value)))
				throw new WalletException (StatusCode.FORBIDDEN);
	}
}
