package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : PinNumberPatchRequestDTO
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
public class PinNumberPatchRequestDTO {

    @NotNull(message = "필수 파라메터가 없습니다.(userSn)")
    @Schema(example = "20220990", description = "사용자 일련번호", required = true)
    private int userSn;

    @NotEmpty(message = "필수 파라메터가 없습니다.(pinNumber)")
    @Schema(example = "219387", description = "변경 할 핀 번호")
    private String pinNumber;

}
