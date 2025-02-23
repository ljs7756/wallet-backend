package com.playnomm.wallet.exception;

import com.playnomm.wallet.enums.StatusCode;
import lombok.Getter;

/**
 * packageName :  com.playnomm.wallet.exception
 * fileName : UserJwtDifferentException
 * author :  ljs7756
 * date : 2023-01-13
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-13                ljs7756             최초 생성
 */
@Getter
public class UserJwtDifferentException extends RuntimeException{
    private int code;
    private String message;

    public UserJwtDifferentException() {super();}

    public UserJwtDifferentException(String message) {
        super(message);
    }
    public UserJwtDifferentException(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
}
