package com.playnomm.wallet.dto.home.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.home.response
 * fileName : HomeResponseDTO
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Getter
@Setter
public class HomeResponseDTO {
    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "1", description = "사용자 공통회원 일련번호")
    private Integer userCmmnSn;

    @Schema(example = "1000", description = "블록체인 네트워크 ID")
    private String blcNtwrkId;

    @Schema(example = "LMC", description = "블록체인 네트워크 명")
    private String blcNtwrkNm;

    @Schema(example = "1", description = "사용자 지갑 일련번호")
    private Integer userCxwaletSn;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "지갑 주소")
    private String cxWaletAdres;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "지갑 명칭")
    private String cxwaletNm;

    @Schema(example = "test.jpg", description = "아바타 이미지 경로")
    private String avataProflUrl;

    @Schema(example = "Y", description = "kyc 인증 여부")
    private String kycCrtfcAt;

    @Schema(example = "N", description = "생체 인증 여부")
    private String loginSauthUseAt;


}
