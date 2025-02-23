package com.playnomm.wallet.dto.auth.response;

import lombok.Getter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.response
 * fileName : KycResponseDTO
 * author :  ljs7756
 * date : 2023-02-01
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-01                ljs7756             최초 생성
 */
@Getter
public class KycResponseDTO {

    private String kycAt;
    private String kycCode;
    private String kycMessage;

    public KycResponseDTO(String kycAt, String kycCode, String kycMessage){
        this.kycAt = kycAt;
        this.kycCode = kycCode;
        this.kycMessage = kycMessage;
    }
}
