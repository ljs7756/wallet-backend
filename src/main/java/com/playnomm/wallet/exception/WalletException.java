package com.playnomm.wallet.exception;

import com.playnomm.wallet.enums.StatusCode;
import lombok.Getter;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Getter
public class WalletException extends RuntimeException {
	private int    code;
	private String message;

	public WalletException () {
		this.code = StatusCode.BASIC_ERROR.getCode ();
		this.message = StatusCode.BASIC_ERROR.getMessage ();
	}

	public WalletException (StatusCode statusCode) {
		this.code = statusCode.getCode ();
		this.message = statusCode.getMessage ();
	}

	public WalletException (int code) {
		this (code, null);
	}

	public WalletException (int code, String message) {
		this.code = code;
		this.message = message;
	}
}
