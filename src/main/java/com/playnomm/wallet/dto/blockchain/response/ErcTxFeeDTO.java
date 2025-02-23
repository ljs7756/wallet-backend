package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author : hzn
 * @date : 2023/01/17
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErcTxFeeDTO {
	@Schema(example = "0.000031504626174", description = "예상 수수료", type = "number")
	private BigDecimal fee;
	@Schema(example = "0.000031504626174", description = "예상 수수료 (USD)", type = "number")
	private BigDecimal usdFee;
	@Schema(example = "0.000031504626174", description = "최대 예상 수수료", type = "number")
	private BigDecimal maxFee;
	@Schema(example = "0.000031504626174", description = "최대 예상 수수료 (USD)", type = "number")
	private BigDecimal usdMaxFee;
//	@Schema(example = "9227", description = "예상 시간 (초)", type = "integer")
//	private BigInteger estimate;
}
