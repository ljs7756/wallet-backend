package com.playnomm.wallet.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.request
 * fileName : RefreshTokenRequestDTO
 * author :  ljs7756
 * date : 2022-12-22
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-22                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefreshTokenRequestDTO {

    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzIyODc5MDl9.rUOrRW6ZZobURqAM4Q-UWThGKwYtN-WhKW3Hww0MD78", description = "Access Token")
    private String accessToken;

    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzIyODc5MDl9.rUOrRW6ZZobURqAM4Q-UWThGKwYtN-WhKW3Hww0MD78", description = "Refresh Token")
    private String refreshToken;

    @Schema(example = "bcdc8ebd-ba95-48a8-b61b-16bc55b2fb544", description = "Login UUID")
    private String loginUuid;
}
