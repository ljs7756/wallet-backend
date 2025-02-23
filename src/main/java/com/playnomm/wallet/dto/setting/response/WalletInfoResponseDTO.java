package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : WalletInfoResponseDTO
 * author :  evilstorm
 * date : 2022/12/27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/27              evilstorm             최초 생성
 */
@Getter
@Setter
public class WalletInfoResponseDTO {

    @Schema(example = "1", description = "사용자 암호화폐지갑 일련번호")
    private String userCxwaletSn;
    @Schema(example = "지갑 2번", description = "암호화폐지갑 명칭")
    private String cxwaletNm;
    @Schema(example = "N", description = "기본 진입 네트워크 여부")
    private String bassEntryNtwrkAt;
    @Schema(example = "0xdlkjasdljkwd", description = "암호화폐지갑 주소")
    private String cxwaletAdres;
    @Schema(example = "1000", description = "BLC 네트웍 아이디 (LMC: 1000, ETH:1001, BNB:1002")
    private String blcNtwrkId;

}
