package com.playnomm.wallet.dto.blockchain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : hzn
 * @date : 2023/01/25
 * @description :
 */
@Setter
@Getter
public class WithdrawalInfoDTO {
	@Schema(example = "Y", description = "LMC 자산 소유 여부", type = "string")
	private String whetherOwnedLmcAssets;
	@Schema(example = "Y", description = "ERC 자산 소유 여부", type = "string")
	private String whetherOwnedErcAssets;
	@Schema(example = "Y", description = "전체 서비스 탈퇴 여부", type = "string")
	private String whetherWithdrawAll;
}
