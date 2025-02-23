package com.playnomm.wallet.config.security;



import com.playnomm.wallet.dto.auth.response.UserAccessTokenResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.UserJwtDifferentException;
import com.playnomm.wallet.mapper.auth.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.config.security
 * fileName : CustomUserDetailService
 * author :  ljs77
 * date : 2022-11-28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-28                ljs77             최초 생성
 */
@Service
public class CustomUserDetailService {

    @Autowired
    AuthMapper authMapper;

    public UserDetails loadUserByUsername(Integer userSn, String jwt){
        
        // access token 확인
        UserAccessTokenResponseDTO userAccessTokenResponseDTO = authMapper.getUserAccessToken(Integer.valueOf(userSn));

            if(userAccessTokenResponseDTO.getCnt() == 1){
                //로그아웃 체크
                int checkLogOut = authMapper.checkLogOut(jwt);
                if(checkLogOut > 0) throw new UserJwtDifferentException(StatusCode.USER_LOGOUT);

                // 중복로그인 체크
                if(!jwt.equals(userAccessTokenResponseDTO.getLoginAccesTkn())) throw new UserJwtDifferentException(StatusCode.DUPLICATE_LOGIN);
            }else{
                throw new UsernameNotFoundException("User not found");
            }


        return new CustomUserDetails(userSn, userAccessTokenResponseDTO.getUserCmmnSn());
    }
}
