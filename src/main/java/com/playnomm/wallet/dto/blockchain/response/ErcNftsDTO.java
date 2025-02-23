package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@JsonIgnoreProperties({"type"})
public class ErcNftsDTO {
	@Schema(example = "0x495f947276749ce646f68ac8c248420045cb7b5e", description = "계약 주소", type = "string")
	private String contract;
	@Schema(example = "30525461628701321596829457633536467399064712353917245261906823441357413024244", description = "토큰 ID", type = "string")
	private String tokenId;
	@Schema(example = "ERC721", description = "NFT 유형", type = "string")
	private String type;
	@Schema(example = "Blue", description = "NFT 이름", type = "string")
	private String name;
	@Schema(example = "https://arweave.net/7kYf67WUdFjKnfFWrg98ba0ZgDIqCbe9s_txuL-OJtE", description = "이미지", type = "string")
	private String image;
}
