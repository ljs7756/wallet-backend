package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

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
public class ChangeSymbolDTO {
    @Hidden
    private int userSn;


//CHECKME 값 체크는 되는데 잘못딘 값을 입력했을 때 405에러가 발생한다.
//    @Pattern(regexp="^(KRW|USD)$", message = "유효한 값이 아닙니다.(KRW, USD)")
    @NotEmpty(message = "필수 파라메터가 없습니다.(symbolCode)")
    @Schema(example = "KRW|USD", description = "변경 할 통화", allowableValues = {"KRW", "USD"})
    private String symbolCode;

}
