package com.playnomm.wallet.dto.blockchain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.web3j.crypto.ECKeyPair;

/**
 * @author : hzn
 * @date : 2022/12/15
 * @description :
 */
@Getter
@Setter
@AllArgsConstructor
public class KeyPairDTO {
	private String    privateKey;
	private String    publicKey;
	private String    address;
	private ECKeyPair ecKeyPair;
}
