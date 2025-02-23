package com.playnomm.wallet.enums;

import lombok.Getter;

@Getter
public enum EthereumApiType implements ApiType {
	BALANCE ("balance", "", "POST", "application/json;charset=UTF-8", "application/json;charset=UTF-8"),
	TRANSFER ("transfer", "", "POST", "application/json;charset=UTF-8", "application/json;charset=UTF-8"),

	// NFT
	NFTS ("nfts", "/networks/{chainId}/accounts/{walletAddress}/assets/nfts", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"),
	NFT ("nft", "/networks/{chainId}/nfts/{tokenAddress}/tokens/{tokenId}", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"),

	// Gas Tracker
	ETH_GAS_TRACKER ("ethGasTracker", "/api?module=gastracker&action=gasoracle&apikey={apiKey}", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"),
	ETH_GAS_TRACKER_GASESTIMATE ("ethGasTrackerGasestimate", "/api?module=gastracker&action=gasestimate&gasprice={gasPrice}&apikey={apiKey}", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"),
	BNB_GAS_TRACKER ("bnbGasTracker", "/api?module=gastracker&action=gasoracle&apikey={apiKey}", "GET", "application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8")
	;

	private String apiName;
	private String uri;
	private String method;
	private String contentType;
	private String accept;

	EthereumApiType (String apiName, String uri, String method, String contentType, String accept) {
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
