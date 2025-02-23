package com.playnomm.wallet.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.response
 * fileName : UserAccessTokenResponseDTO
 * author :  ljs7756
 * date : 2023-01-13
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13                ljs7756             최초 생성
 */
@Getter
@Setter
public class UserAccessTokenResponseDTO {
    private int cnt;
    private String loginAccesTkn;

    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "1", description = "공통 사용자 일련번호")
    private  Integer userCmmnSn;
}
