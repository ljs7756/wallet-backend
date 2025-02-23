package com.playnomm.wallet.dto.setting.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CategoryPostRequestDTO {
    @Schema(example = "01CXWL-NTC", description = "게시판 ID")
    private String bbsId;

    @Schema(example = "KO", description = "언어")
    private String langCode;

    @Schema(example = "CXWL", description = "시스템 아이디")
    private String sysId;

    @Schema(example = "일반|자산", description = "카테고리 이름")
    private String nttClNm;

    @Schema(example = "1", description = "노출 순서")
    private String expsrOrdr;


        @Schema(example = "Y", description = "사용 여부")
    private String useAt;

}
