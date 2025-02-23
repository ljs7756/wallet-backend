package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : ModifyPasswordRequestDTO
 * author :  ljs7756
 * date : 2022-12-21
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-21                ljs7756             최초 생성
 */
@Getter
@Setter
public class ModifyPasswordRequestDTO {
    @NotNull(message = "필수 파라메터가 없습니다.(userCmmnSn)")
    @Schema(example = "20230074", description = "사용자 공통 일련번호", required = true)
    private int userCmmnSn = -1;

    @NotNull(message = "필수 파라메터가 없습니다.(password)")
    @Schema(description = "새로운 패스워드")
    private String password;

    @NotNull(message = "필수 파라메터가 없습니다.(newPassword)")
    @Schema(description = "새로운 패스워드")
    private String newPassword;
}
