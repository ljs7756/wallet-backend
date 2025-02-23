package com.playnomm.wallet.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playnomm.wallet.dto.common.CommonDTO;
import com.playnomm.wallet.validation.ValidationGroups;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import org.web3j.protocol.Web3j;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * @author : hzn
 * @date : 2022/12/19
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BlockchainDTO extends CommonDTO {
	public static final BigInteger ETH_GAS_LIMIT     = BigInteger.valueOf (21000L); // ETH 메인 토큰 수수료
	public static final BigInteger ETH_ERC_GAS_LIMIT = BigInteger.valueOf (65000L); // ETH 메인 토큰 외 수수료

	@NotBlank(groups = {ValidationGroups.defaultGroup.class})
	private String              networkId;
	@NotNull(groups = {ValidationGroups.ErcTransferGroup.class})
	private Integer             userCxwaletTknSn;
	private String              symbol;
	@NotNull(groups = {
			ValidationGroups.ErcTransferGroup.class,
			ValidationGroups.LmcTransferGroup.class,
			ValidationGroups.LmcNftTransferGroup.class,
			ValidationGroups.LmcSwapGroup.class,
			ValidationGroups.ErcSwapGroup.class,
			ValidationGroups.LmcNftSwapGroup.class,
			ValidationGroups.ErcNftSwapGroup.class
	})
	@Min(value = 1, groups = {
			ValidationGroups.ErcTransferGroup.class,
			ValidationGroups.LmcTransferGroup.class,
			ValidationGroups.LmcNftTransferGroup.class,
			ValidationGroups.LmcSwapGroup.class,
			ValidationGroups.ErcSwapGroup.class,
			ValidationGroups.LmcNftSwapGroup.class,
			ValidationGroups.ErcNftSwapGroup.class
	})
	private Integer             userCmmnSn;
	@NotBlank(groups = {ValidationGroups.ErcTransferGroup.class, ValidationGroups.LmcTransferGroup.class, ValidationGroups.LmcNftTransferGroup.class})
	private String              toAddress;
	@NotNull(groups = {
			ValidationGroups.ErcTransferGroup.class, ValidationGroups.LmcTransferGroup.class, ValidationGroups.LmcSwapGroup.class, ValidationGroups.ErcSwapGroup.class
	})
	private BigDecimal          amount      = BigDecimal.ZERO;
	private String              toNetworkId;
	private String              toSymbol;
	private Web3j               web3j;
	private Integer             chainId;
	private Integer             dcmlpointLt;
	private String              fromAddress;
	private String              rpcUrl;
	private Integer             userSn;
	private String              txHash;
	private Map<String, Object> txHashLogData;
	private String              creatDt;
	private Integer             blcTknSn;
	private String              responseBody;
	private BigDecimal          totalAmount = BigDecimal.ZERO;
	private BigDecimal          receiverTotalAmount = BigDecimal.ZERO;
	private String              gtwySeCode;
	private BigDecimal          cxFeeQty    = BigDecimal.ZERO;
	private String              gtwyAdres;

	public String getFromAddress () {
		String value = this.fromAddress;
		if (ObjectUtils.isEmpty (value)) return value;
		if (!this.fromAddress.startsWith ("0x")) {
			value = "0x".concat (this.fromAddress);
		}
		return value;
	}

	public String getFromAddress (boolean isLm) {
		String value = this.fromAddress;
		if (ObjectUtils.isEmpty (value)) return value;
		if (isLm) {
			value = this.fromAddress.replace ("0x", "");
		} else {
			if (!this.fromAddress.startsWith ("0x")) {
				value = "0x".concat (this.fromAddress);
			}
		}
		return value;
	}

	public String getToAddress () {
		String value = this.toAddress;
		if (ObjectUtils.isEmpty (value)) return value;
		if (!this.toAddress.startsWith ("0x")) {
			value = "0x".concat (this.toAddress);
		}
		return value;
	}

	public String getToAddress (boolean isLm) {
		String value = this.toAddress;
		if (ObjectUtils.isEmpty (value)) return value;
		if (isLm) {
			value = this.toAddress.replace ("0x", "");
		} else {
			if (!this.toAddress.startsWith ("0x")) {
				value = "0x".concat (this.toAddress);
			}
		}
		return value;
	}
}
