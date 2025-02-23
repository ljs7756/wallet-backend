package com.playnomm.wallet.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * packageName :  com.playnomm.wallet.dto.response
 * fileName : AuthResponseDTO
 * author :  ljs77
 * date : 2022-11-29
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-29                ljs77             최초 생성
 */
@Getter
@Setter
public class AuthResponseDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private Integer userSn;
}
