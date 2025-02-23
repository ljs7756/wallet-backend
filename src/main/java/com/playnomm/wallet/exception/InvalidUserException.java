package com.playnomm.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * packageName :  com.playnomm.wallet.exception
 * fileName : InvalidUserException
 * author :  ljs7756
 * date : 2023-02-21
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-21                ljs7756             최초 생성
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidUserException extends RuntimeException{
    public InvalidUserException(String message){
        super(message);
    }
}
