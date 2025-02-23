package com.playnomm.wallet.dto.setting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : myInfoResponseDTO
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
public class MyInfoResponseDTO {
    @Schema(example = "133", description = "사용자 공통 일련번호")
    private Integer userCmmnSn;
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "test@playnomm.com", description = "이메일")
    private String email;
    @Schema(example = "Y", description = "SNS 연결 여부")
    private String snsCnncAt;

    @Schema(example = "홍길동", description = "사용자 이름")
    private String name;
    @Schema(example = "홍길동", description = "사용자 닉네임")
    private String nickname;

    @Schema(example = "1982-09-25", description = "생년월일")
    private String birthday;

    @Schema(example = "2022-12-21", description = "가입일")
    private String userSbscrbDt;

    @Schema(example = "Y", type = "string", description = "KYC 인증여부")
    private String kycAt;

}
