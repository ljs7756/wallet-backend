package com.playnomm.wallet.enums;

/**
 * @author : hzn
 * @date : 2022/12/20
 * @description :
 */
public interface ApiType {
	String getApiName ();
	String getUri ();
	void setUri (String uri);
	String getMethod ();
	String getContentType ();
	String getAccept ();
}
