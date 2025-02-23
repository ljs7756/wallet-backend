package com.playnomm.wallet.dto.setting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : TermsResponseDTO
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
public class TermsResponseDTO {

    @Schema(example = "10M001", description = "약관 ID")
    private String stplatId;
    @Schema(example = "이용약관", description = "약관 명")
    private String stplatNm;
    @Schema(example = "통신사 이용약관에 대한 동의..", description = "약관 내용")
    private String stplatCn;
    @Schema(example = "이용약관", description = "약관 상세 내용")
    private String stplatDetailCn;
    @Schema(example = "Y", description = "약관 필수 여부")
    private String stplatEssntlAt;


}
