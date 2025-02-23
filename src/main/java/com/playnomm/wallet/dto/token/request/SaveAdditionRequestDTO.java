package com.playnomm.wallet.dto.token.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.token.request
 * fileName : SaveAdditionRequestDTO
 * author :  ljs7756
 * date : 2022-12-16
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-16                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SaveAdditionRequestDTO {
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "1000", allowableValues = {"1000","1001","1002"}, description = "메인넷")
    private String blcNtwrkId;

    @Schema(example = "1", description = "토큰 일련번호")
    private Integer blcTknSn;

    @Schema(example = "1", description = "사용자 토큰 일련번호")
    private Integer userCxwaletTknSn;

}
