package com.playnomm.wallet.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : hzn
 * @date : 2022/12/30
 * @description :
 */
public class RequestContextUtil {

	public static String getLocaleString (HttpServletRequest request) {
		String langCode = RequestContextUtils.getLocale (request).getLanguage ().toUpperCase ();
		if (!langCode.equalsIgnoreCase  ("KO") && !langCode.equalsIgnoreCase ("EN")) return "KO";
		return langCode;
	}

	public static HttpServletRequest getHttpServletRequest () {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes ()).getRequest ();
	}
}
