package com.playnomm.wallet.util;

import com.playnomm.wallet.config.security.CustomUserDetails;
import com.playnomm.wallet.exception.InvalidUserException;
import com.playnomm.wallet.exception.UserJwtDifferentException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;

/**
 * packageName :  com.playnomm.wallet.util
 * fileName : AuthenticationUtil
 * author :  ljs7756
 * date : 2023-02-21
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-21                ljs7756             최초 생성
 */

public class AuthenticationUtil {

    public static void authUserSn(Integer userSn) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails)principal;
        Integer authUserSn = customUserDetails.getUserSn();
        if(!userSn.equals(authUserSn)) throw new InvalidUserException("Not authenticated user.");

    }

    public static void authUserCmmnSn(Integer userCmmnSn) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CustomUserDetails customUserDetails = (CustomUserDetails)principal;
        Integer authUserCmmnSn = customUserDetails.getUserCmmnSn();
        if(!userCmmnSn.equals(authUserCmmnSn)) throw new InvalidUserException("Not authenticated user.");

    }
}
