package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
@Alias("LmcNftDTO")
@JsonIgnoreProperties({"randboxOpnAt", "randboxSn", "tknNftSn", "tknDfnId", "tknId"})
public class LmcNftDTO {
	@Schema(example = "1022672", description = "NFT 순번", type = "integer")
	private Integer       nftitmSn;
	@Schema(example = "#1", description = "NFT명", type = "string")
	private String        nftitmNm;
	@Schema(example = "LGDY", description = "NFT 레어리티 코드", type = "string")
	private String        nftitmRareCode;
	@Schema(example = "Legendary", description = "NFT 레어리티 코드명", type = "string")
	private String        nftitmRareCodeNm;
	@Schema(example = "12", description = "NFT 레어리티 점수", type = "string")
	private String        nftitmRareWghtval;
	@Schema(example = "JinKei", description = "창작자명", type = "string")
	private String        crtrNm;
	@Schema(example = "PR", description = "NFT 상태 코드", type = "string")
	private String        nftSttusCode;
	@Schema(example = "Prepare", description = "NFT 상태 코드명", type = "string")
	private String        nftSttusCodeNm;
	@Schema(example = "20220830", description = "소유자", type = "integer")
	private Integer       ownerUserSn;
	@Schema(example = "V", description = "NFT 미디어 타입 코드", type = "string")
	private String        nftitmMediaTyCode;
	@Schema(example = "Video", description = "NFT 미디어 타입 코드명", type = "string")
	private String        nftitmMediaTyCodeNm;
	@Schema(example = "https://d3j8b1jkcxmuqq.cloudfront.net/temp/collections/TEST-COLLECTION-03/NFT_ITEM/DA9D1E61-5A81-4358-B8E7-EE071AE99009.png", description = "NFT 컨텐츠 URL", type = "string")
	private String        nftitmCntntsUrl;
	@Schema(example = "https://d3j8b1jkcxmuqq.cloudfront.net/temp/collections/TEST-COLLECTION-03/NFT_ITEM_META/DA9D1E61-5A81-4358-B8E7-EE071AE99009.json", description = "NFT 메타파일 URL", type = "string")
	private String        nftitmMetaFileUrl;
	@Schema(example = "2022-09-15 05:38:52", description = "NFT 등록일시", type = "string")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime nftitmRegistDt;
	@Schema(example = "43.72", description = "NFT 가격", type = "number")
	private BigDecimal    nftitmQty;
	@Schema(example = "50.144627610823702284", description = "NFT USD 가격", type = "number")
	private BigDecimal    nftitmQtyUsd;
	@Schema(example = "12350.144627610823702", description = "NFT KRW 가격", type = "number")
	private BigDecimal    nftitmQtyKrw;
	@Schema(example = "Superchief 1", description = "시즌 회차명", type = "string")
	private String        seasonTmeNm;
	@Schema(example = "Limited Edition", description = "시즌명", type = "string")
	private String        seasonNm;
	@Schema(example = "Superchief Gallery is the world&#39;s 1st physical IRL NFT gallery, established in 2021 in NYC. The 1st Superchief Edition is a limited collection of the prominent NFT artists. With playNomm&rsquo;s unique reward model, the NFT carefully selected and created for this collection makes the NFT even more fascinating.", description = "시즌설명", type = "string")
	private String        seasonDc;
	@Schema(example = "The artwork titled &quot;Light in the Shadows&quot; portrays the internal battle of the main character as she grapples with living under the shadows of tradition and finding her own voice. The piece showcases her struggle to navigate the expectations of her culture and ultimately find her own path. The lighting in the artwork creates dappled shadows on the woman&#39;s face, highlighting her inner turmoil and the journey towards self-discovery.", description = "작품설명", type = "string")
	private String        colctDc;
	@Schema(example = "60eb0562e1ecb53216ced1baa596852da62676fd1d01b89cc0af3853a04222f7", description = "NFT 트랜잭션 해시", type = "string")
	private String        nftSignTxhash;
	@Schema(example = "1", description = "거래 SN", type = "integer")
	private Integer       cxTradeSn;
	@Schema(example = "2022-09-28 00:00:00", description = "받은 시각", type = "string")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime tradeOccrrncDt;
	@Schema(example = "17af7bcbecc9963df6b4f2704f784b4a485df262", description = "보낸 사람", type = "string")
	private String        trsmtrCxwaletAdres;
	@Schema(example = "60eb0562e1ecb53216ced1baa596852da62676fd1d01b89cc0af3853a04222f7", description = "거래 트랜잭션 해시", type = "string")
	private String        signTxhash;
	@Hidden
	private String        randboxOpnAt;
	@Hidden
	private Integer       randboxSn;
	@Hidden
	private Integer       tknNftSn;
	@Hidden
	private String        tknDfnId;
	@Hidden
	private String        tknId;
}
