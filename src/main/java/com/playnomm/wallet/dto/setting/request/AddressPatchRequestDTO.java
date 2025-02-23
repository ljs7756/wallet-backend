package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : AddressRequestDTO
 * author :  evilstorm
 * date : 2022/12/26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/26              evilstorm             최초 생성
 */
@Getter
@Setter
public class AddressPatchRequestDTO {

    @Hidden
    private int userCxwaletAdbkSn;
    @Schema(example = "지갑 1번", type = "string", description = "주소록 명칭")
    private String adbkNm;
    @Schema(example = "0x32984723", type = "string", description = "주소록 암호화폐지갑 주소")
    private String adbkCxwaletAdres;
    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;
    @Schema(example = "LM|ETH|BNB", allowableValues = {"LM", "ETH", "BNB"}, description = "실볼 코드 ")
    private String cxSymbolCode;
    @Schema(example = "F", type = "string", allowableValues = {"F", "N"}, description = "이용목적 코드 (F: FT, N: NFT)")
    private String usePurpsCode;
    @Schema(example = "1", type = "intger", description = "노출 순서")
    private int expsrOrdr;

}
