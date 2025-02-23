package com.playnomm.wallet.config.interceptor;

import com.playnomm.wallet.config.security.CustomUserDetails;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * @author : hzn
 * @date : 2023/02/23
 * @description :
 */
public class AuthorityInterceptor implements HandlerInterceptor {
	private final List<String> authParameters = List.of ("userSn", "userCmmnSn");

	public boolean preHandle (HttpServletRequest request, HttpServletResponse response, Object handler) {
		CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext ().getAuthentication ().getPrincipal ();

		Enumeration<String> parameterNames = request.getParameterNames ();
		while (parameterNames.hasMoreElements ()) {
			String parameterName = parameterNames.nextElement ();
			if (authParameters.contains (parameterName)) {
				String value = request.getParameter (parameterName);
				checkAuthority (parameterName, value, customUserDetails);
			}
		}

		if (handler instanceof HandlerMethod) {
			String uri = request.getRequestURI ();
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			String[] methodPathArray = handlerMethod.getMethodAnnotation (RequestMapping.class).path ();
			String[] controllerPathArray = handlerMethod.getMethod ().getDeclaringClass ().getAnnotation (RequestMapping.class).value ();
			if (ObjectUtils.isEmpty (controllerPathArray)) controllerPathArray = handlerMethod.getMethod ().getDeclaringClass ().getAnnotation (RequestMapping.class).path ();

			for (String path : methodPathArray) {
				path = controllerPathArray[0] + path;
				int pos = path.indexOf ("{");
				if (pos == -1) {
					pos = path.length ();
				}
				List<String> requestMappingIncludedUriItem = Arrays.asList (uri.substring (uri.indexOf (path.substring (0, pos))).split ("/"));
				List<String> pathItem = Arrays.asList (path.replaceAll ("\\{(\\w+)}", "$1").split ("/"));
				for (String authParameter : authParameters) {
					if (pathItem.contains (authParameter)) {
						MethodParameter[] methodParameters = handlerMethod.getMethodParameters ();
						for (MethodParameter methodParameter : methodParameters) {
							if (methodParameter.getParameter ().getName ().equals (authParameter)) {
								String value = requestMappingIncludedUriItem.get (pathItem.indexOf (authParameter));
								checkAuthority (authParameter, value, customUserDetails);
							}
						}
					}
				}
			}
		}
		return true;
	}

	private void checkAuthority (String parameterName, String value, CustomUserDetails customUserDetails) {
		if (!ObjectUtils.isEmpty (parameterName) && !ObjectUtils.isEmpty (value) && customUserDetails != null)
			if ((authParameters.get (0).equals (parameterName) && !Integer.valueOf (value).equals (customUserDetails.getUserSn ()))
				|| (authParameters.get (1).equals (parameterName) && !Integer.valueOf (value).equals (customUserDetails.getUserCmmnSn ())))
				throw new WalletException (StatusCode.FORBIDDEN);
	}
}
