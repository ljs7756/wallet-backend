package com.playnomm.wallet.dto.setting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponseDTO {
    @Schema(example = "1", description = "일련번호")
    private int bbsNttClSn;
    @Schema(example = "01CXWL-NTC", description = "게시판 ID")
    private String bbsId;
    @Schema(example = "일반|자산", description = "카테고리 이름")
    private String nttClNm;
    @Schema(example = "3", description = "노출 순서")
    private int expsrOrdr;

}
