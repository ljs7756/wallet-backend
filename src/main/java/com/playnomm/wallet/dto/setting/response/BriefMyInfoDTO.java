package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : BriefMyInfoDTO
 * author :  evilstorm
 * date : 2022/12/23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/23              evilstorm             최초 생성
 */
@Getter
@Setter
public class BriefMyInfoDTO {

    @Schema(example = "6548", type = "integer", description = "사용자 일련번호")
    private int userSn;
    @Schema(example = "20230074", type = "integer", description = "사용자 공통 일련번호")
    private int userCmmnSn;
    @Schema(example = "Y", type = "string", description = "KYC 인증 여부")
    private String kycAt;
    @Schema(example = "플레이놈", type = "string", description = "닉네임")
    private String nickname;
    @Schema(example = "Y", type = "string", description = "사용자 아바타 사용 여부")
    private String userAvataUseAt;
    @Schema(example = "djqoijsad", type = "string", description = "사용자 아바타 일련번호")
    private String userCmmnAvataSn;

}
