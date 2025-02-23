package com.playnomm.wallet.dto.auth.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * packageName :  com.playnomm.wallet.dto.auth.response
 * fileName : PrivateKeyResponseDTO
 * author :  ljs7756
 * date : 2023-01-18
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-18                ljs7756             최초 생성
 */
@Getter
@Setter
public class PrivateKeyResponseDTO {
    @Schema(example = "0x981acbcb1df32989d37d466837486e896e0194a5", description = "private key")
    private String privateKey;
}
