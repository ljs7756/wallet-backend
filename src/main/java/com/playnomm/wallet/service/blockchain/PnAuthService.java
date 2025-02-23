package com.playnomm.wallet.service.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.config.security.JwtProvider;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.PnAuthApiType;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import com.playnomm.wallet.util.HttpUtil;
import com.playnomm.wallet.util.PlaynommUtil;
import com.playnomm.wallet.util.RequestContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hzn
 * @date : 2023/01/25
 * @description :
 */
@Service
@RequiredArgsConstructor
public class PnAuthService {
	private final JwtProvider jwtProvider;
	@Value ("${playnomm.auth.api.url}")
	private String pnAuthApiUrl;
	private final ObjectMapper objectMapper = new ObjectMapper ();

	/**
	 * 서비스 가입 목록 (통합 인증 API 호출)
	 *
	 * @param userCmmnSn
	 * @return
	 */
	public List<Map<String, Object>> listOfServices (Integer userCmmnSn) {
		List<Map<String, Object>> services;

		if (userCmmnSn == null) return new ArrayList<> ();

		try {
			String pnAuthorization = PlaynommUtil.getPnAuthorization (userCmmnSn);
			String jwtToken = jwtProvider.extractToken (RequestContextUtil.getHttpServletRequest ());

			Map<String, Object> headers = new HashMap<> ();
			headers.put ("PN-Authorization", pnAuthorization);
			headers.put ("Authorization", "Bearer " + jwtToken);

			Map<String, Object> param = new HashMap<> ();
			param.put ("fields", "service");

			ResultDTO resultDTO = HttpUtil.send (pnAuthApiUrl, PnAuthApiType.MEMBERSHIP_INFO, param, headers);
			if (resultDTO.getCode () != 200) throw new WalletException (resultDTO.getCode (), resultDTO.getMessage ());
			services = (List) ((Map) objectMapper.readValue ((String) resultDTO.getData (), Map.class).get ("data")).get ("service");

		} catch (Exception e) {
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return services;
	}
}
