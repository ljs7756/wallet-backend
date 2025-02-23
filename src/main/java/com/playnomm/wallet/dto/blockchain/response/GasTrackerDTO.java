package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("GasTrackerDTO")
public class GasTrackerDTO {
	private BigInteger lastBlck;
	private BigInteger safeGasPrice;
	private BigInteger propseGasPrice;
	private BigInteger fastGasPrice;
	private BigDecimal sugestGasPrice;
}
