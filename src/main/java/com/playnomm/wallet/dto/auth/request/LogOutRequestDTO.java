package com.playnomm.wallet.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.request
 * fileName : LogOutRequestDTO
 * author :  ljs7756
 * date : 2023-01-25
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-25                ljs7756             최초 생성
 */
@Getter
@Setter
public class LogOutRequestDTO {
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2NzIyODc5MDl9.rUOrRW6ZZobURqAM4Q-UWThGKwYtN-WhKW3Hww0MD78", description = "Access Token")
    private String loginAccesTkn;
}
