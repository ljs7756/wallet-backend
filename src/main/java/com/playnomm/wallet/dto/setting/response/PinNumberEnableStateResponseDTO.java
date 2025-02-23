package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : PinNumberEnableStateResponseDTO
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
public class PinNumberEnableStateResponseDTO {
    @Schema(example = "Y", description = "핀번호 설정 여부")
    private String userTradePinAt;
}
