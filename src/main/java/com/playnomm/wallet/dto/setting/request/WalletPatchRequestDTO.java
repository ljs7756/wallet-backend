package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : WalletPatchReqestDTO
 * author :  evilstorm
 * date : 2022/12/28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/28              evilstorm             최초 생성
 */
@Getter
@Setter
public class WalletPatchRequestDTO {

    @Hidden
    @Schema(example = "1", description = "사용자 암호화폐지갑 일련번호")
    private int userCxwaletSn;
    @Schema(example = "지갑 2번", description = "암호화폐지갑 명칭")
    private String cxwaletNm;
    @Hidden
    @Schema(example = "0xdlkjasdljkwd", description = "암호화폐지갑 주소")
    private String cxwaletAdres;
    @Hidden
    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;

}
