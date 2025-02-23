package com.playnomm.wallet.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.ApiType;
import com.playnomm.wallet.enums.EthereumApiType;
import com.playnomm.wallet.enums.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : hzn
 * @date : 2022/12/19
 * @description :
 */
@Slf4j
public class HttpUtil {
	private static Pattern      pattern      = Pattern.compile ("(\\{\\w+})");
	private static ObjectMapper objectMapper = new ObjectMapper ();

	public static ResultDTO send (String domain, ApiType apiType, Object param) {
		return send (domain, apiType, param, null);
	}

	public static ResultDTO send (String domain, ApiType apiType, Object param, Map<String, Object> headers) {
		boolean isGet = "GET".equals (apiType.getMethod ());
		HttpURLConnection conn = null;
		try {
			conn = getUrlConnection (domain, apiType, param, headers);
			if (!isGet) {
				try (OutputStream os = conn.getOutputStream ()) {
					byte[] input = objectMapper.writeValueAsString (param).getBytes (StandardCharsets.UTF_8);
					os.write (input, 0, input.length);
				}
			}

			StringBuilder sb = new StringBuilder ();
			int HttpResult = conn.getResponseCode ();
			String line = null;
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				try (BufferedReader br = new BufferedReader (new InputStreamReader (conn.getInputStream (), StandardCharsets.UTF_8))) {

					while ((line = br.readLine ()) != null) {
						sb.append (line);
					}
				}
				return new ResultDTO<> (StatusCode.ACCESS, sb.toString ());
			} else {
				try (BufferedReader br = new BufferedReader (new InputStreamReader (conn.getErrorStream (), StandardCharsets.UTF_8))) {
					while ((line = br.readLine ()) != null) {
						sb.append (line);
					}
				}
//				for (StatusCode sc : StatusCode.values ()) {
//					if (sc.getCode () == HttpResult) return new ResultDTO<> (sc);
//				}
				return new ResultDTO<> (HttpResult, sb.toString ());
			}
		} catch (Exception e) {
			String message = ObjectUtils.isEmpty (e.getMessage ()) ? "Unknown Error" : e.getMessage ();
			log.error ("[" + HttpUtil.class.getName () + "] - {}", message);
			return new ResultDTO<> (StatusCode.BASIC_ERROR, message);
		} finally {
			conn.disconnect ();
		}
	}

	private static HttpURLConnection getUrlConnection (String domain, ApiType apiType, Object param, Map<String, Object> headers) throws IOException {
		String uri = apiType.getUri ();
		String method = apiType.getMethod ();
		StringBuilder sb = new StringBuilder (domain);

		if ("GET".equals (method)) {
			if (apiType instanceof EthereumApiType) {
				setPathVariables (uri, sb, (Map) param);
			} else {
				sb.append (uri);
				setQueryString (sb, (Map) param);
			}
		} else {
			sb.append (uri);
		}

		URL reqUrl = new URL (sb.toString ());
		HttpURLConnection connection = (HttpURLConnection) reqUrl.openConnection ();
		connection.setRequestMethod (method);
		connection.setRequestProperty ("Content-Type", apiType.getContentType ());
		connection.setRequestProperty ("Accept", apiType.getAccept ());
		if (headers != null) {
			for (String key : headers.keySet ()) {
				connection.setRequestProperty (key, String.valueOf (headers.get (key)));
			}
		}
		if ("POST".equals (method)) {
			connection.setDoOutput (true);
		}
		return connection;
	}

	private static void setQueryString (StringBuilder sb, Map<String, Object> param) throws JsonProcessingException {
		boolean isFirst = true;
		for (String key : param.keySet ()) {
			Object object = param.get (key);

			if (ObjectUtils.isEmpty (object)) continue;
			if ("pathVariable".equals (key)) {
				sb.append ("/").append (object);
				continue;
			}

			if (isFirst) {
				sb.append ("?");
				isFirst = false;
			} else {
				sb.append ("&");
			}
			String value = null;
			if (object instanceof Number) {
				value = CommonUtil.padding (String.valueOf (object), (int) param.get ("dcmlpointLt")).replace (".", "");
			} else {
				value = object.toString ();
			}
			sb.append (key).append ("=").append (value);
		}
	}

	private static void setPathVariables (String uri, StringBuilder sb, Map<String, Object> param) {
		if (!ObjectUtils.isEmpty (uri)) {
			Matcher matcher = pattern.matcher (uri);
			AtomicReference<String> path = new AtomicReference<> ();
			path.set (uri);
			matcher.results ().forEach (mr -> {
				String holder = mr.group ();
				String value = holder.replace ("{", "").replace ("}", "");
				path.set (path.get ().replace (holder, String.valueOf (param.get (value))));
			});
			sb.append (path.get ());
		}
	}
}
