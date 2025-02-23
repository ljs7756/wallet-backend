package com.playnomm.wallet.dto.setting.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : AddAddressRequestDTO
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
public class AddAddressRequestDTO {
    @Schema(example = "20220990", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "LMC", allowableValues = {"LMC", "ETH", "BNB"}, description = "네트워크명")
    private String blcNetwork;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "지갑주소")
    private String adbkCxwaletAdres;

    @Schema(example = "F", allowableValues = {"F","N"}, description = "이용 목적 코드")
    private String usePurpsCode;

    @Schema(example = "홍길동 지갑", description = "주소명")
    private String adbkNm;

}
