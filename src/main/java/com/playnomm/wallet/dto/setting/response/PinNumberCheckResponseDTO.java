package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : PinNumberCheckResponseDTO
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
public class PinNumberCheckResponseDTO {
    @Schema(example = "Y", description = "인증 여부")
    private String pinCrtfcAt;

}
