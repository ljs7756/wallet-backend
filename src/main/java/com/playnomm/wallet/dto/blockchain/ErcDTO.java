package com.playnomm.wallet.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playnomm.wallet.validation.ValidationGroups;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;

/**
 * @author : hzn
 * @date : 2022/12/13
 * @description : ETH, BNB
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErcDTO extends BlockchainDTO {
	private BigInteger gasLimit = ETH_GAS_LIMIT;
	@NotNull(groups = {
			ValidationGroups.ErcTransferGroup.class, ValidationGroups.ErcSwapGroup.class, ValidationGroups.ErcNftSwapGroup.class
	})
	@Min(value = 1, groups = {
			ValidationGroups.ErcTransferGroup.class, ValidationGroups.ErcSwapGroup.class, ValidationGroups.ErcNftSwapGroup.class
	})
	private Integer    priority;
	@NotBlank(groups = {ValidationGroups.ErcNftSwapGroup.class})
	private String     contractAddress;
	private String     privateKey;
	private String     blcNtwrkBassTknAt;
	@NotNull(groups = {ValidationGroups.ErcNftSwapGroup.class})
	private BigInteger tokenId;
}
