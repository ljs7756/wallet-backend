package com.playnomm.wallet.service;

import com.playnomm.wallet.config.security.JwtProvider;
import com.playnomm.wallet.enums.AuthUserInfoType;
import com.playnomm.wallet.exception.PlayNommAuthException;
import com.playnomm.wallet.util.PlaynommUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service
 * fileName : PlayNommAuthService
 * author :  evilstorm
 * date : 2023/01/04
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/04              evilstorm             최초 생성
 */
@Service
public class PlayNommAuthService {
    @Value("${playnomm.auth.api.url}")
    String authApiUrl;

    @Autowired
    JwtProvider jwtProvider;

    public HttpHeaders createAuthHeader(HttpServletRequest request, int userCmmnSn) {
        return PlaynommUtil.createCommonAuthHttpHeader(jwtProvider.extractToken(request), userCmmnSn);
    }

    /**
     * 간략한 사용자 정보를 받아온다.
     * @param header createAuthHeader 호출 후 생성된 해더 사용.
     * @return
     * @throws Exception
     */
    public Map<String, Object> requestCommonUserInfo(HttpHeaders header, AuthUserInfoType type) throws Exception {

        String url = authApiUrl+"/pn-api/v1/me?" + type.getType();

        Map<String, Object> result;

        try{
            result = PlaynommUtil.sendApi(header, null, url, "GET");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());

            return result;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 이메일 + 비밀번호 조합으로 사용자 인증
     *
     * @param header createAuthHeader 호출 후 생성된 해더 사용.
     * @param userId 사용자 아이디(이메일)
     * @param pwd 비밀번호
     * @return
     * @throws Exception
     */
    public Map<String, Object> certificationWithPwd(HttpHeaders header, String userId, String pwd) throws Exception{

        String url = authApiUrl+"/pn-api/v1/email";

        Map<String, Object> result;

        HashMap<String, Object> params = new HashMap<>(){{
            put("mode", "check");
            put("userId", userId);
            put("currentPassword", pwd);
        }};

        try{
            result = PlaynommUtil.sendApi(header, params, url, "POST");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> sendVerificationCodeViaEmail(HttpHeaders header, String userId) throws Exception {

        String url = authApiUrl+"/pn-api/v1/email";

        Map<String, Object> result;

        HashMap<String, Object> params = new HashMap<>(){{
            put("mode", "findPinNumber");
            put("userId", userId);
            put("statusCode", "60");
        }};

        try{
            result = PlaynommUtil.sendApi(header, params, url, "POST");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> certificationCode(HttpHeaders header, String userId, String verifyCode) throws Exception {

        String url = authApiUrl+"/pn-api/v1/email";

        Map<String, Object> result;

        HashMap<String, Object> params = new HashMap<>(){{
            put("mode", "verify");
            put("userId", userId);
            put("verifyCode", verifyCode);
            put("statusCode", "60");
        }};

        try{
            result = PlaynommUtil.sendApi(header, params, url, "POST");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> modifyPassword(HttpHeaders header, String password, String newPassword) throws Exception {


        String url = authApiUrl+"/pn-api/v1/me";

        Map<String, Object> result;

        HashMap<String, Object> params = new HashMap<>(){{
            put("mode", "modify");
            put("currentPassword", password);
            put("modifyPassword", newPassword);
        }};

        try{
            result = PlaynommUtil.sendApi(header, params, url, "PUT");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> withdrawMember(HttpHeaders header) throws Exception {
        String url = authApiUrl+"/pn-api/v1/me";
        Map<String, Object> result;
        try{
            result = PlaynommUtil.sendApi(header, null, url, "DELETE");
            if(!result.get("resultCode").equals(0)) throw new PlayNommAuthException(Integer.parseInt(result.get("resultCode").toString()), result.get("message").toString());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
