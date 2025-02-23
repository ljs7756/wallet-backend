package com.playnomm.wallet.dto.token.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.token.request
 * fileName : SaveDirectTokenRequestDTO
 * author :  ljs7756
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveDirectTokenRequestDTO {
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "1000", allowableValues = {"1000","1001","1002"}, description = "메인넷")
    private String blcNtwrkId;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "컨트랙트 주소")
    private String smrtCntrctAdres;

    @Schema(example = "LeisureMeta", description = "토큰 명")
    private String tknNm;

    @Schema(example = "LMC", description = "토큰 기호")
    private String cxSymbolCode;

    @Schema(example = "18", description = "소수점 길이")
    private Integer dcmlpointLt;
}
