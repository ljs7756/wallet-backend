package com.playnomm.wallet.dto.setting.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : AddressRequestDTO
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
public class AddressRequestDTO {
    @Hidden
    @Schema(example = "12376", description = "사용자 암호화폐지갑 주소록 일련번호")
    private Integer userCxwaletAdbkSn;

    @NotNull(message = "필수 파라메터가 없습니다.(userSn)")
    @Schema(example = "20220990", description = "사용자 일련번호")
    private Integer userSn;

    @NotEmpty(message = "필수 파라메터가 없습니다.(usePurpsCode)")
    @Schema(example = "F", allowableValues = {"F","N"}, description = "이용 목적 코드 F:FT, N: NFT")
    private String usePurpsCode;

    @Schema(example = "홍길동 지갑", description = "주소명")
    private String adbkNm;

    @NotEmpty(message = "필수 파라메터가 없습니다.(adbkCxwaletAdres)")
    @Schema(example = "0xaskd2q3eoasd", description = "지갑 주소")
    private String adbkCxwaletAdres;

    @NotEmpty(message = "필수 파라메터가 없습니다.(blcNtwrkId)")
    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;

    @NotEmpty(message = "필수 파라메터가 없습니다.(cxSymbolCode)")
    @Schema(example = "LM|ETH|BNB", allowableValues = {"LM", "ETH", "BNB"}, description = "실볼 코드 ")
    private String cxSymbolCode;

    @Hidden
    @Schema(example = "1", description = "노출 순서")
    private String expsrOrdr;

    @Hidden
    @Schema(example = "N", description = "본인 주소록 여부 ")
    private String selfAdbkAt;

}
