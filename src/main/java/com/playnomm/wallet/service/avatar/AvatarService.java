package com.playnomm.wallet.service.avatar;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.auth.request.RefreshTokenRequestDTO;
import com.playnomm.wallet.dto.auth.response.UserAccessTokenResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.auth.AuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.avatar
 * fileName : AvatarService
 * author :  ljs7756
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                ljs7756             최초 생성
 */
@Service
public class AvatarService {

    @Autowired
    AuthMapper authMapper;
    public ResultDTO getAuthorization(Integer userSn){

        UserAccessTokenResponseDTO userAccessTokenResponseDTO = authMapper.getUserAccessToken(userSn);

        if(userAccessTokenResponseDTO.getCnt() < 1) return new ResultDTO(StatusCode.USER_NOT_EXISTS);

        return new ResultDTO(StatusCode.ACCESS);
    }
}
