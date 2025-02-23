package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : WithdrawResponseDTO
 * author :  evilstorm
 * date : 2023/01/27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/27              evilstorm             최초 생성
 */
@Getter
@Setter
public class WithdrawResponseDTO {
    @Schema(example = "Y", description = "탈퇴 처리 여부")
    private String withdrawAt;
}
