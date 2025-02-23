package com.playnomm.wallet.dto.nft.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.nft.request
 * fileName : SaveNftOrderRequestDTO
 * author :  ljs7756
 * date : 2022-12-26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-26                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveNftOrderRequestDTO {
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "1", description = "사용자 NFT 일련번호")
    private Integer nftitmSn;

    @Schema(example = "1", description = "랜덤박스 일련번호")
    private Integer randboxSn;

    @Schema(example = "1", description = "사용자 nft 매핑 일련번호")
    private Integer userNftRandboxSn;

    @Schema(example = "1", defaultValue = "1", description = "노출 순서")
    private Integer expsrOrdr;
}
