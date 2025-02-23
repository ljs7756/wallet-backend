package com.playnomm.wallet.dto.blockchain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playnomm.wallet.validation.ValidationGroups;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : hzn
 * @date : 2022/12/15
 * @description :
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Alias("LmDTO")
public class LmDTO extends BlockchainDTO {
	private String  movable = "free";
	@NotNull(groups = {ValidationGroups.LmcNftTransferGroup.class, ValidationGroups.LmcNftSwapGroup.class})
	private Integer nftitmSn;
	@NotBlank(groups = {ValidationGroups.LmcTransferGroup.class, ValidationGroups.LmcNftTransferGroup.class, ValidationGroups.LmcSwapGroup.class, ValidationGroups.LmcNftSwapGroup.class})
	private String  privateKey;
	private String  signUserUuid;
	private String  guardian;
}
