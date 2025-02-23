package com.playnomm.wallet.dto.setting.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : AgreeTermsRequestDTO
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
public class AgreeTermsRequestDTO {

    @Schema(example = "20220990", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "10M001", description = "약관 ID")
    private String stplatId;

    @Schema(example = "Y", description = "약관 동의 여부")
    private String stplatAgreAt;
}
