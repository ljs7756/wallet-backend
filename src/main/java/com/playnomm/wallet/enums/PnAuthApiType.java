package com.playnomm.wallet.enums;

import lombok.Getter;

/**
 * @author : hzn
 * @date : 2023/01/25
 * @description :
 */
@Getter
public enum PnAuthApiType implements ApiType {
	MEMBERSHIP_INFO ("membershipInfo", "/pn-api/v1/me", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8");

	private String apiName;
	private String uri;
	private String method;
	private String contentType;
	private String accept;

	PnAuthApiType (String apiName, String uri, String method, String contentType, String accept) {
		this.apiName = apiName;
		this.uri = uri;
		this.method = method;
		this.contentType = contentType;
		this.accept = accept;
	}

	@Override
	public void setUri (String uri) {
		this.uri = uri;
	}
}
