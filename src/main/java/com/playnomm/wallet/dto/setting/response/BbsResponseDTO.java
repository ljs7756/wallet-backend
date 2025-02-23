package com.playnomm.wallet.dto.setting.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : BbsResponseDTO
 * author :  ljs7756
 * date : 2022-12-20
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-20                ljs7756             최초 생성
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BbsResponseDTO {

    @Schema(example = "1", description = "일련번호")
    private Integer nttSn;

    @Schema(example = "지갑서비스 오픈", description = "제목")
    private String nttSj;

    @Schema(example = "지갑서비스 오픈합니다..", description = "내용")
    private String nttCn;

    @Schema(example = "2022-12-21T06:44:24.000+00:00", description = "게시일")
    private Timestamp ntceBeginDt;

    @Schema(example = "2022-12-21T06:44:24.000+00:00", description = "종료일")
    private Timestamp ntceEndDt;

    @Schema(example = "2022-12-21T06:44:24.000+00:00", description = "업데이트 일")
    private Timestamp sysUpdtDt;

    @Schema(example = "2014", description = "카테고리 일련번호")
    private int bbsNttClSn;
}
