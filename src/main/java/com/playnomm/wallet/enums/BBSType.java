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
public enum BBSType {
    EVENT("40CXWL-EVT"),
    NOTICE("02CXWL-NTC"),
    FAQ("01CXWL-FAQ"),
    ASK("21CXWL-1N1");

    private final String type;

    BBSType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
}
