package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author : hzn
 * @date : 2023/01/11
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErcTxDTO {
	@Schema(example = "0x8c6ad18953a7e430747098295098d4cce03612211d6bb7c84e2d7dd0df628ba1", description = "트랜잭션 해시", type = "string")
	private String     transactionHash;
	@Schema(example = "0x7de7e12a03e34403ad85ab618d81c0c7940d112d6d900bb5fb6583847eb3bf9c", description = "블록 해시", type = "string")
	private String     blockHash;
	@Schema(example = "8285933", description = "블록 번호", type = "number")
	private BigInteger blockNumber;
	@Schema(example = "21000", description = "사용된 가스", type = "number")
	private BigInteger gasUsed;
	@Schema(example = "0x2b51cb53b54e842cb19b0efb8a2f9b4c4137370b", description = "송신자 지갑 주소", type = "string")
	private String     from;
	@Schema(example = "0x7bf54762cf3d4e6da15b03ce52f7e7f94acf5b74", description = "수신자 지갑 주소", type = "string")
	private String     to;

	public void setTransactionHash (String transactionHash) {
		if (transactionHash != null && !transactionHash.startsWith ("0x")) {
			transactionHash = "0x" + transactionHash;
		}
		this.transactionHash = transactionHash;
	}
}
