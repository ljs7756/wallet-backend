package com.playnomm.wallet.dto.nft.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.nft.response
 * fileName : NftInfoResponseDTO
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Getter
@Setter
public class NftInfoResponseDTO {

    @Schema(example = "1", description = "nft 일련번호")
    private Integer nftitmSn;

    @Schema(example = "1", description = "컬렉션 일련번호")
    private Integer colctSn;

    @Schema(example = "1Season1 BPS", description = "시즌 명")
    private String  seasonNm;

    @Schema(example = "BPS_S.Younghoon(U)", description = "컬렉션 명")
    private String  colctNm;

    @Schema(example = "#1019", description = "nft 명")
    private String  nftitmNm;

    @Schema(example = "BPS – Block, People, Soul is the first NFT Project...", description = "컬렉션 설명")
    private String  colctDc;

    @Schema(example = "BPS – Block, People, Soul is the first NFT Project...", description = "컬렉션 설명-한국어")
    private String  colctDcKo;

    @Schema(example = "JinKei", description = "창작자 명")
    private String  crtrNm;

    @Schema(example = "1", description = "nft 일련번호")
    private Integer randboxSn;

    @Schema(example = "1", description = "사용자 nft 매핑 일련번호")
    private Integer userNftRandboxSn;

    @Schema(example = "1", description = "노출 순서")
    private Integer expsrOrdr;

    @Schema(example = "test/thumbnail/image.jpg", description = "NFT 썸네일")
    private String colctThumbFileUrl;

    @Schema(example = "test/image.jpg", description = "NFT URL")
    private String nftitmCntntsUrl;
}
