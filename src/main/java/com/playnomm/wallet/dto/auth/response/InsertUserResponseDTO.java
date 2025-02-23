package com.playnomm.wallet.dto.auth.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * packageName :  com.playnomm.wallet.dto.auth.response
 * fileName : InsertUserResponseDTO
 * author :  ljs7756
 * date : 2022-12-23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-23                ljs7756             최초 생성
 */
@Getter
@Setter
public class InsertUserResponseDTO {
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "2022-12-22 10:09:15", description = "가입 일시")
    private LocalDateTime userSbscrbDt;

    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "private key")
    private String privateKey;

}
