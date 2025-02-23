package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BalanceDTO {
	@Schema(example = "1500.12", description = "토큰 수량", type = "number", defaultValue = "0")
	private BigDecimal balance  = BigDecimal.ZERO;
	@Schema(example = "445.44999321430174", description = "USD 가격", type = "number", defaultValue = "0")
	private BigDecimal usdPrice = BigDecimal.ZERO;
	@Schema(example = "0.296942906710331000", description = "USD 환율", type = "number", defaultValue = "0")
	private BigDecimal usdExr   = BigDecimal.ZERO;
	@Schema(example = "445.44999321430174", description = "KRW 가격", type = "number", defaultValue = "0")
	private BigDecimal krwPrice = BigDecimal.ZERO;
	@Schema(example = "0.296942906710331000", description = "KRW 환율", type = "number", defaultValue = "0")
	private BigDecimal krwExr   = BigDecimal.ZERO;
	@Schema(example = "-0.139911870000000000", description = "KRW 시세 변동률 (1H)", type = "number", defaultValue = "0")
	private BigDecimal krwPercentChange   = BigDecimal.ZERO;
	@Schema(example = "-0.139911870000000000", description = "USD 시세 변동률 (1H)", type = "number", defaultValue = "0")
	private BigDecimal usdPercentChange   = BigDecimal.ZERO;
	@Schema(example = "0x17af7bcbecc9963df6b4f2704f784b4a485df262", description = "지갑 주소", type = "string")
	private String     cxwaletAdres;
}
