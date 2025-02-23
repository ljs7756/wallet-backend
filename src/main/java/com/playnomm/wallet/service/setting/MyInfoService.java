package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.CertificationEmailPwdRequestDTO;
import com.playnomm.wallet.dto.setting.request.CertificationRequestDTO;
import com.playnomm.wallet.dto.setting.request.ModifyPasswordRequestDTO;
import com.playnomm.wallet.dto.setting.request.VerificationCodeRequestDTO;
import com.playnomm.wallet.dto.setting.response.MyInfoResponseDTO;
import com.playnomm.wallet.dto.setting.response.WithdrawResponseDTO;
import com.playnomm.wallet.enums.AuthUserInfoType;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.PlayNommAuthException;
import com.playnomm.wallet.mapper.setting.MyInfoMapper;
import com.playnomm.wallet.service.PlayNommAuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : MyInfoService
 * author :  evilstorm
 * date : 2022/12/29
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/29              evilstorm             최초 생성
 */
@Service
public class MyInfoService {

    private MyInfoMapper mapper;
    private PlayNommAuthService playNommAuthService;

    public MyInfoService(PlayNommAuthService playNommAuthService, MyInfoMapper mapper) {
        this.playNommAuthService = playNommAuthService;
        this.mapper = mapper;
    }

    public ResultDTO getMyInfo(HttpServletRequest request, int userCmmnSn, int userSn) {

        MyInfoResponseDTO result = new MyInfoResponseDTO();
        Map<String, Object> commonInfos = null;
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, userCmmnSn);
            commonInfos = playNommAuthService.requestCommonUserInfo(headers, AuthUserInfoType.INFO);
        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        } catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }

        Map<String, Object> data =  (Map)commonInfos.get("data");

        result.setUserCmmnSn(userCmmnSn);
        result.setUserSn(userSn);
        result.setName(data.get("name").toString());
        result.setEmail(data.get("email").toString());
        result.setNickname(data.get("nickname").toString());
        result.setKycAt(data.get("kycAt").toString());
        result.setBirthday(data.get("birthday").toString());
        result.setSnsCnncAt(data.get("snsCnncAt").toString());
        result.setUserSbscrbDt(data.get("userSbscrbDt").toString());

        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO getMyInfoBalance(HttpServletRequest request, int userCmmnSn, int userSn) {

        MyInfoResponseDTO result = new MyInfoResponseDTO();
        Map<String, Object> commonInfos = null;
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, userCmmnSn);
            commonInfos = playNommAuthService.requestCommonUserInfo(headers, AuthUserInfoType.BALANCE);
        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        } catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }

        Map<String, Object> data =  (Map)commonInfos.get("data");
        System.out.println(data.toString());

        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO certificationWithPwd(HttpServletRequest request, CertificationEmailPwdRequestDTO params) {

        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, params.getUserCmmnSn());

            playNommAuthService.certificationWithPwd(headers, params.getUserId(), params.getPassword());

        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO sendVerificationCodeWithEmail(HttpServletRequest request, CertificationRequestDTO params) {
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, params.getUserCmmnSn());
            playNommAuthService.sendVerificationCodeViaEmail(headers, params.getUserId());
        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO sendVerificationCodeWithPwd(HttpServletRequest request, CertificationEmailPwdRequestDTO params) {
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, params.getUserCmmnSn());
            playNommAuthService.certificationWithPwd(headers, params.getUserId(), params.getPassword());
        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO modifyPassword(HttpServletRequest request, ModifyPasswordRequestDTO params) {

        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, params.getUserCmmnSn());

            playNommAuthService.modifyPassword(headers, params.getPassword(), params.getNewPassword());

        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO certificationCode(HttpServletRequest request, VerificationCodeRequestDTO params) {
        try {

            HttpHeaders headers = playNommAuthService.createAuthHeader(request, params.getUserCmmnSn());
            playNommAuthService.certificationCode(headers, params.getUserId(), params.getVerifyCode());

        }catch (PlayNommAuthException authException) {
            return new ResultDTO(authException.getCode(), authException.getMessage());
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO withdrawMember(HttpServletRequest request, int userCmmnSn) {
        WithdrawResponseDTO result = new WithdrawResponseDTO();
        try {
            HttpHeaders headers = playNommAuthService.createAuthHeader(request, userCmmnSn);

            playNommAuthService.withdrawMember(headers);

        }catch (PlayNommAuthException authException) {
            result.setWithdrawAt("N");
            return new ResultDTO(200, authException.getMessage(), result);
        }catch (Exception e) {
            return new ResultDTO(StatusCode.BAD_REQUEST);
        }

        result.setWithdrawAt("Y");

        return new ResultDTO(StatusCode.ACCESS, result);
    }
}
