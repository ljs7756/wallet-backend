package com.playnomm.wallet.dto.setting.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : ConfigurationRequestDTO
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigurationRequestDTO {
    @Schema(example = "20220990", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "KO", allowableValues = {"KO", "EN"}, description = "언어 코드")
    private String langCode;

    @Schema(example = "KRW", allowableValues = {"KRW", "USD"}, description = "통화")
    private String currency;
}
