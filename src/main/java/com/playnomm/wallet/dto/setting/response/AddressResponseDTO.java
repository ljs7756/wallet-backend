package com.playnomm.wallet.dto.setting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : AddressResponseDTO
 * author :  ljs7756
 * date : 2022-12-21
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-21                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressResponseDTO {
    @Schema(example = "1", description = "사용자 주소 일련번호")
    private Integer userCxwaletAdbkSn;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "지갑주소")
    private String adbkCxwaletAdres;

    @Schema(example = "홍길동 지갑", description = "주소명")
    private String adbkNm;

    @Schema(example = "F", allowableValues = {"F","N"}, description = "이용 목적 코드")
    private String usePurpsCode;

    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;

    @Schema(example = "LM|ETH|BNB", allowableValues = {"LM", "ETH", "BNB"}, description = "실볼 코드 ")
    private String cxSymbolCode;

    @Schema(example = "N", description = "본인 주소록 여부")
    private String selfAdbkAt;

}
