package com.playnomm.wallet.dto.blockchain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@Alias("LmcNftsDTO")
public class LmcNftsDTO {
	@Schema(example = "1022672", description = "NFT 순번", type = "integer")
	private Integer nftitmSn;
	@Schema(example = "#1", description = "NFT명", type = "string")
	private String  nftitmNm;
	@Schema(example = "https://d3j8b1jkcxmuqq.cloudfront.net/temp/collections/2023_FW_COLLECTION_001/NFT_ITEM_THUMB/F911995A-C264-4900-AD12-BD1805362847.jpg", description = "NFT 썸네일 경로", type = "string")
	private String  nftitmCntntsThumbFilePath;
	@Schema(example = "JinKei", description = "창작자명", type = "string")
	private String  crtrNm;
	@Schema(example = "1", description = "사용자 NFT 랜덤박스 일련번호", type = "integer")
	private Integer userNftRandboxSn;
	@Schema(example = "1", description = "랜덤박스 일련번호", type = "integer")
	private Integer randboxSn;
	@Schema(example = "1", description = "노출 순서", type = "integer")
	private Integer expsrOrdr;
}
