package com.playnomm.wallet.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.response
 * fileName : EtherAddressInfoResponseDTO
 * author :  ljs7756
 * date : 2023-01-20
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-20                ljs7756             최초 생성
 */
@Getter
@Setter
public class EtherAddressInfoResponseDTO {
    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "uuid")
    private String userUuid;

    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "wallet address")
    private String cxwaletAdres;

    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "private key")
    private String userPrivky;

    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "public key")
    private String userPblky;
}
