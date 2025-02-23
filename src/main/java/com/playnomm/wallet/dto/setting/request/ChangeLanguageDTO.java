package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : ChangeLanguageDTO
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
public class ChangeLanguageDTO {

    @Hidden
    private int userSn;

    @NotEmpty(message = "필수 파라메터가 없습니다.(langCode)")
    @Schema(example = "KO|EN", description = "변경 할 언어", allowableValues = {"KO", "EN"})
    private String langCode;

}
