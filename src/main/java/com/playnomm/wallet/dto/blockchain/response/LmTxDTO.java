package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : hzn
 * @date : 2023/01/11
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LmTxDTO {
	@Schema(example = "0x8c6ad18953a7e430747098295098d4cce03612211d6bb7c84e2d7dd0df628ba1", description = "트랜잭션 해시", type = "string")
	private String transactionHash;
}
