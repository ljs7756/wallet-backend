package com.playnomm.wallet.dto.token.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.token.request
 * fileName : SaveOrderRequestDTO
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
public class SaveOrderRequestDTO {

    @Schema(example = "1", description = "사용자 토큰 일련번호")
    private Integer userCxwaletTknSn;

    @Schema(example = "1", defaultValue = "1", description = "노출 순서")
    private Integer expsrOrdr;
}
