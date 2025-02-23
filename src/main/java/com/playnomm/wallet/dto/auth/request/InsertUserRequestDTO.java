package com.playnomm.wallet.dto.auth.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * packageName :  com.playnomm.wallet.dto.auth.request
 * fileName : InsertUserRequestDTO
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InsertUserRequestDTO {
    @Schema(example = "1", description = "공통 사용자 일련번호")
    private Integer userCmmnSn;

//    @Schema(example = "둘리", description = "닉네임")
//    private String userNcnm;
//
//    @Schema(example = "0xdac17f958d2ee523a2206206994597c13d831ec7", description = "LM 지갑 주소")
//    private String cxwaletAdres;
    
}
