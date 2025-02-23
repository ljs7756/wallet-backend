package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : MainNetResponseDTO
 * author :  evilstorm
 * date : 2023/01/10
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/10              evilstorm             최초 생성
 */
@Getter
@Setter
public class MainNetResponseDTO {
    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;
    @Schema(example = "LM|ETH|BNB", allowableValues = {"LM", "ETH", "BNB"}, description = "실볼 코드 ")
    private String cxSymbolCode;
    @Schema(example = "Leisure Meta Chain Network", description = "네트워크 이름")
    private String blcNtwrkNm;
    @Schema(example = "Leisure Meta Chain Network", description = "네트워크 설명")
    private String blcNtwrkDc;
    @Schema(example = "5", description = "네트워크 Chain ID")
    private String blcChainId;


}
