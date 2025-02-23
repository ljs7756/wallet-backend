package com.playnomm.wallet.enums;

import lombok.Getter;

/**
 * @author : hzn
 * @date : 2022/12/14
 * @description :
 */
@Getter
public enum StatusCode {
	ACCESS(200, "OK."),

	BAD_REQUEST(400, "Bad Request."),
	TOKEN_UNAUTHORIZED (403, "Token unauthorized."),
	TOKEN_EXPIRED(401, "Token expired."),
	FORBIDDEN(413, "Not permitted user."),

	KYC_UNAUTHORIZED(470, "KYC unauthorized."),

	TOKEN_EXPIRED_ALL(471, "Token expired all."),

	NOT_FOUND(404, "Not Found."),
	USER_NOT_EXISTS(404, "User Does not exists."),
	DUPLICATE_LOGIN(423, "Duplicated login."),
	DUPLICATE_DATA(424, "Duplicated data."),
	USER_LOGOUT(409, "logout."),

	INTERNAL_SERVER_ERROR(500, "Internal Server Error."),

	REQUIRED_PARAMETERS_ERROR(600, "Check required parameters."),
	INVALID_NETWORK_ID(601, "Invalid network identification."),

	NO_CONTRACT_INFORMATION(8000, "No contract address information."),
	UNSUPPORTED_SWAP(8001, "Unsupported swap."),
	BALANCE_NOT_FOUND(8002, "Balance not found."),
	ACCOUNT_NOT_FOUND(8003, "Account not found."),
	NOT_ENOUGH_BALANCE(8003, "Not enough balance in your wallet."),
	LMC_SERVER_ERROR(8999, "LeisureMeta Chain service unavailable."),

	NO_NETWORK_INFO (9000, "No network basic information."),
	NO_TOKEN_INFO (9001, "No token basic information."),
	NO_WALLET_INFO (9002, "No wallet information."),
	NO_WALLET_TOKEN_INFO (9002, "No wallet token information."),
	NO_REWARD_ORDER_INFO (9003, "No reward order information."),
	NO_RECEIVER_INFO (9004, "No receiver information."),
	NO_NFT_INFO (9005, "No NFT information."),
	NO_TOKEN_NFT_INFO (9006, "No token NFT information."),
	NO_TOKEN_FT_INFO (9007, "No token FT information."),
	NO_USER_INFO (9008, "No user information."),
	NO_NFTS_DATA (9009, "No NFTs data."),
	NO_NFT_DATA (9010, "No NFT data."),
	NO_EXR_BASS (9011, "No exchange rate information."),
	NO_COMMON_CODE_INFO (9012, "No common code information."),
	NO_GATEWAY_INFO (9013, "No gateway information."),
	NO_GAS_TRACKER_INFO (9014, "No gas tracker information."),
	NO_PLAYNOMM_RECEIVER_INFO (9015, "No playnomm service receiver information."),
	CHECK_RECEIVER_ADDRESS (9016, "Check the receiver address."),
	BASIC_ERROR(9999, "An unknown error occurred. Please try again after a while.")
	;

	private int code;
	private String message;

	StatusCode (int code, String message) {
		this.code = code;
		this.message = message;
	}
}
