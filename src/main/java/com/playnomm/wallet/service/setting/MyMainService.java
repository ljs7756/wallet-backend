package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.WalletPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.BriefMyInfoDTO;
import com.playnomm.wallet.dto.setting.response.WalletInfoResponseDTO;
import com.playnomm.wallet.enums.AuthUserInfoType;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.PlayNommAuthException;
import com.playnomm.wallet.mapper.setting.MyMainMapper;
import com.playnomm.wallet.service.PlayNommAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : MyMainService
 * author :  evilstorm
 * date : 2022/12/23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/23              evilstorm             최초 생성
 */
@Service
public class MyMainService {

    private PlayNommAuthService playNommAuthService;

    private MyMainMapper myMainMapper;

    public MyMainService(PlayNommAuthService playNommAuthService, MyMainMapper mapper) {
        this.playNommAuthService = playNommAuthService;
        this.myMainMapper = mapper;

    }

    public ResultDTO getBriefMyInfo(HttpServletRequest request, int userCmmnSn, int userSn) {
        BriefMyInfoDTO result = myMainMapper.getBriefMyInfo(userSn);
        Map<String, Object> commonInfos;
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader (request, userCmmnSn);
            commonInfos = playNommAuthService.requestCommonUserInfo(headers, AuthUserInfoType.INFO);
        } catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        } catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }

        Map<String, Object> data =  (Map)commonInfos.get("data");
        result.setKycAt(data.get("kycAt").toString());
        result.setNickname(data.get("nickname").toString());
        result.setUserCmmnSn(Integer.parseInt(data.get("userCmmnSn").toString()));

        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO getMyWallet(int userSn) {
        List<WalletInfoResponseDTO> result = myMainMapper.getMyWallet(userSn);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO getMyRepresentWallet(int userSn) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("userSn", userSn);
        params.put("bassEntryNtwrkAt", "Y");

        WalletInfoResponseDTO result = myMainMapper.getWallet(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO patchWallet(int userCxwaletSn, WalletPatchRequestDTO data) {
        data.setUserCxwaletSn(userCxwaletSn);
        int patchResult = myMainMapper.patchWallet(data);

        HashMap<String, Object> params = new HashMap<>();
        params.put("userCxwaletSn", userCxwaletSn);
        WalletInfoResponseDTO result = myMainMapper.getWallet(params);

        return new ResultDTO(StatusCode.ACCESS, result);
    }
}
