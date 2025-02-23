package com.playnomm.wallet.dto.token.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * packageName :  com.playnomm.wallet.dto.token.response
 * fileName : TokenAdditionResponseDTO
 * author :  ljs7756
 * date : 2022-12-16
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-16                ljs7756             최초 생성
 */
@Getter
@Setter
public class TokenAdditionResponseDTO {
    @Schema(example = "1", description = "토큰 일련번호")
    private Integer blcTknSn;

    @Schema(example = "1", description = "사용자 토큰 일련번호")
    private Integer userCxwaletTknSn;

    @Schema(example = "N", description = "프라이빗 토큰 여부")
    private String prvateTknAt;

    @Schema(example = "Y", description = "소유 여부")
    private String ownAt;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "컨트랙트 주소")
    private String smrtCntrctAdres;

    @Schema(example = "LeisureMeta", description = "토큰 명")
    private String tknNm;

    @Schema(example = "LMC", description = "토큰 기호")
    private String cxSymbolCode;

    @Schema(example = "18", description = "소수점 길이")
    private Integer dcmlpointLt;

//    @Schema(example = "1", defaultValue = "1", description = "노출 순서")
//    private Integer expsrOrdr;

    @Schema(example = "test/image.jpg", description = "아이콘 이미지")
    private String iconFileUrl;

    @Schema(example = "Y", description = "대표 LM 구분")
    private String defaultLm;

}
