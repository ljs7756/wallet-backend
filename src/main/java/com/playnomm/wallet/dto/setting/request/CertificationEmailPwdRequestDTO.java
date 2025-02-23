package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : CertificationEmailPwdReqeustDTO
 * author :  evilstorm
 * date : 2023/01/06
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/06              evilstorm             최초 생성
 */
@Getter
@Setter
public class CertificationEmailPwdRequestDTO {

    @NotNull(message = "필수 파라메터가 없습니다.(userCmmnSn)")
    @Schema(example = "20230074", description = "사용자 공통 일련번호", required = true)
    private int userCmmnSn = -1;

    @NotEmpty(message = "필수 파라메터가 없습니다.(userId)")
    @Email(message = "필수 파라메터가 없습니다.(userId - not email type)")
    @Schema(example = "kimmc@playnomm.com", description = "사용자 아이디(이메일)", required = true)
    private String userId;

    @NotEmpty(message = "필수 파라메터가 없습니다.(password)")
    @Schema(example = "qei@39sdk!", description = "사용자 비밀번호", required = true)
    private String password;
}
