package com.playnomm.wallet.enums;

/**
 * packageName :  com.playnomm.wallet.enums
 * fileName : BBSType
 * author :  evilstorm
 * date : 2022/12/26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/26              evilstorm             최초 생성
 */
public enum AuthUserInfoType {
    INFO("fields=info"),
    SERVICE("fields=service"),
    BALANCE("fields=balance");

    private final String type;

    AuthUserInfoType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
