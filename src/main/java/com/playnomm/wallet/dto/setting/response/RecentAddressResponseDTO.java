package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : RecentAddressResponseDTO
 * author :  ljs7756
 * date : 2023-01-16
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-16                ljs7756             최초 생성
 */
@Getter
@Setter
public class RecentAddressResponseDTO {

    @Schema(example = "1", description = "공통 사용자 일련번호")
    private Integer userCmmnSn;

    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "CXWL", description = "시스템 아이디")
    private String sysId;

    @Schema(example = "1000", description = "BLC 네트워크 아이디")
    private String blcNtwrkId;

    @Schema(example = "LM", description = "심볼 코드")
    private String cxSymbolCode;

    @Schema(example = "NPNT", description = "거래 유형 상세 코드")
    private String tradeTyDetailCode;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "송신자 지갑 주소")
    private String trsmtrCxwaletAdres;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "수신자 지갑 주소")
    private String rcverCxwaletAdres;

    @Schema(example = "2022-12-19 07:55:32", description = "거래 발생 일시 일시")
    private Timestamp tradeOccrrncDt;

    @Schema(example = "홍길동 지갑", description = "지갑 명칭")
    private String adbkNm;


}
