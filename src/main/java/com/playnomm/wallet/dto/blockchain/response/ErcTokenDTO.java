package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErcTokenDTO {
	@Schema(example = "LM", description = "토큰 심볼", type = "string")
	private String     symbol;
	@Schema(example = "5000000000", description = "토큰 전체 수량", type = "number")
	private BigInteger totalSupply;
	@Schema(example = "18", description = "소수점", type = "number")
	private BigInteger decimals;
	@Schema(example = "LeisureMeta", description = "토큰명", type = "string")
	private String     name;
}
