package com.playnomm.wallet.service.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.config.security.JwtProvider;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.auth.request.InsertUserRequestDTO;
import com.playnomm.wallet.dto.auth.request.LogOutRequestDTO;
import com.playnomm.wallet.dto.auth.request.RefreshTokenRequestDTO;
import com.playnomm.wallet.dto.auth.response.*;
import com.playnomm.wallet.dto.blockchain.KeyPairDTO;
import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import com.playnomm.wallet.mapper.auth.AuthMapper;
import com.playnomm.wallet.mapper.home.HomeMapper;
import com.playnomm.wallet.mapper2.user.UserMapper;
import com.playnomm.wallet.service.AwsService;
import com.playnomm.wallet.service.ManageKeyService;
import com.playnomm.wallet.service.blockchain.LeisureMetaService;
import com.playnomm.wallet.util.PlaynommUtil;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.net.SocketException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * packageName :  com.playnomm.wallet.service.sample.auth
 * fileName : AuthService
 * author :  ljs77
 * date : 2022-11-29
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-29                ljs77             최초 생성
 */
@Service
@Slf4j
public class AuthService {

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthMapper authMapper;

    @Value("${playnomm.auth.api.url}")
    String authApiUrl;

    @Autowired
    LeisureMetaService leisureMetaService;

    @Autowired
    AwsService awsService;

    HomeResponseDTO homeResponseDTO;

    @Autowired
    HomeMapper homeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ManageKeyService manageKeyService;

    public ResultDTO updateAccessToken(RefreshTokenRequestDTO refreshTokenRequestDTO){

        ResultDTO resultDTO=null;

        int atSatus = jwtProvider.validateToken(refreshTokenRequestDTO.getAccessToken());
        int rtStatus = jwtProvider.validateToken(refreshTokenRequestDTO.getRefreshToken());

        if(atSatus== 401 && rtStatus == 401){
            resultDTO = new ResultDTO(StatusCode.TOKEN_EXPIRED_ALL);
        }else if(atSatus== 401 && rtStatus == 200){
            //accessToken은 만료되고 refleshToken은 만료 전인 경우 재발급을 진행한다.
            resultDTO = jwtProvider.generateJwtToken(refreshTokenRequestDTO.getUserSn(),"RF", refreshTokenRequestDTO.getLoginUuid());
        }else {
            resultDTO = new ResultDTO(StatusCode.BAD_REQUEST);
        }

        return resultDTO;
    }

    @Transactional
    public ResultDTO InsertUser(InsertUserRequestDTO insertUserRequestDTO) {

        //insert 실행
        InsertUserResponseDTO insertUserResponseDTO;
        ResultDTO resultDTO;
        HttpHeaders httpHeaders = new HttpHeaders();

        try {
            //가입상태가 P인 경우는 api 전송만 안된 것으로 판단
            UserAccessTokenResponseDTO userAccessTokenResponseDTO = authMapper.getPreJoinUser(insertUserRequestDTO);

            if (userAccessTokenResponseDTO.getCnt() > 0) {

                insertUserResponseDTO = authMapper.selectInsertUser(insertUserRequestDTO.getUserCmmnSn());

                return new ResultDTO<>(StatusCode.ACCESS, insertUserResponseDTO);

            } else {
                int insertResult = authMapper.insertUser(insertUserRequestDTO);

                //insert 후
                if (insertResult > 0) {
                    insertUserResponseDTO = authMapper.selectInsertUser(insertUserRequestDTO.getUserCmmnSn());

                    //회원 정보 조회
                    String pnAuthorization = PlaynommUtil.getPnAuthorization(Long.valueOf(insertUserRequestDTO.getUserCmmnSn()));

                    //access,refresh token 생성
                    resultDTO = new ResultDTO<>(StatusCode.ACCESS, insertUserResponseDTO);


                    //api 전송 시작
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.set("PN-Authorization", pnAuthorization);
                    httpHeaders.set("Authorization", "Bearer " + jwtProvider.generateAccessToken(insertUserResponseDTO.getUserSn()));

                    String url = authApiUrl + "/pn-api/v1/me?fields=info";

                    Map<String, Object> result = PlaynommUtil.sendApi(httpHeaders, new HashMap<>(), url, "GET");
                    if (!result.get("resultCode").equals(0)) throw new SocketException("Api Error: " + result);

                    String nickName = String.valueOf(((HashMap) result.get("data")).get("nickname"));
                    String lmCxwaletAdres = String.valueOf(((HashMap) result.get("data")).get("cxwaletAdres"));

                    //닉네임 업데이트
                    Map<String, Object> updateParams = new HashMap<>();
                    updateParams.put("nickName", nickName);
                    updateParams.put("userSn", insertUserResponseDTO.getUserSn());
                    int updateResult = authMapper.updateUserInfo(updateParams);
                    if (updateResult < 0) throw new Exception("Insert Error.");

                    //총 세 개의 지갑 주소 저장(LMC, ETH, BNB)
                    List<Map<String, Object>> walletMapList = new ArrayList<>();

                    //지갑 생성
                    KeyPairDTO keyPairDTO = leisureMetaService.getRawKeyPair();
                    String cxwaletAdres = keyPairDTO.getAddress();
                    String userPrivky = keyPairDTO.getPrivateKey(); //개인키(aws 암호 함수 사용)
                    String userPblky = awsService.awsKmsEncrypt (keyPairDTO.getPublicKey());  //공개키(aws 암호 함수 사용)

                    String[] arrPrivateKey = manageKeyService.dividePrivateKey(userPrivky); //  개인키를 DB를 나누어 보관

                    //LM 메인넷 생성
                    Map<String, Object> lmParams = new HashMap<>();
                    lmParams.put("userSn", insertUserResponseDTO.getUserSn());
                    lmParams.put("cxwaletAdres", lmCxwaletAdres);  //통합회원에서 이미 생성된 지갑주소 insert
                    lmParams.put("blcNtwrkId", "1000");
                    lmParams.put("bassEntryNtwrkAt", "Y");
                    lmParams.put("userPrivky", null);
                    lmParams.put("userPrivky2", null);
                    lmParams.put("userPblky", null);
                    walletMapList.add(lmParams);

                    //이더리움 메인넷 생성
                    Map<String, Object> ethParams = new HashMap<>();
                    ethParams.put("userSn", insertUserResponseDTO.getUserSn());
                    ethParams.put("cxwaletAdres", cxwaletAdres);
                    ethParams.put("blcNtwrkId", "1001");
                    ethParams.put("bassEntryNtwrkAt", "N");
                    ethParams.put("userPrivky", arrPrivateKey[0]);
                    ethParams.put("userPrivky2", arrPrivateKey[1]);
                    ethParams.put("userPblky", userPblky);
                    walletMapList.add(ethParams);

                    //바이낸스 메인넷 생성
                    Map<String, Object> bnbParams = new HashMap<>();
                    bnbParams.put("userSn", insertUserResponseDTO.getUserSn());
                    bnbParams.put("cxwaletAdres", cxwaletAdres);
                    bnbParams.put("blcNtwrkId", "1002");
                    bnbParams.put("bassEntryNtwrkAt", "N");
                    bnbParams.put("userPrivky", arrPrivateKey[0]);
                    bnbParams.put("userPrivky2", arrPrivateKey[1]);
                    bnbParams.put("userPblky", userPblky);
                    walletMapList.add(bnbParams);

                    int walletResult = authMapper.insertUserWallet(walletMapList);
                    if (walletResult > 0){
                        int divideResult = userMapper.insertUserDividePrivateKey(walletMapList);
                        if(divideResult < 1) throw new Exception("Insert divided private key error.");
                    }else{
                        throw new Exception("Insert Error.");
                    }

                    // 기본 토큰 생성 (총 5개)
                    List<Map<String, Object>> tokenMapList = new ArrayList<>();

                    // LMC 메인넷
                    Map<String, Object> tokenParams = new HashMap<>();
                    tokenParams.put("userSn", insertUserResponseDTO.getUserSn());
                    tokenParams.put("blcNtwrkId", "1000");
                    tokenParams.put("cxSymbolCode", "LM");
                    tokenMapList.add(tokenParams);

                    //ETH 메인넷
                    Map<String, Object> tokenParams_eth1 = new HashMap<>();
                    tokenParams_eth1.put("userSn", insertUserResponseDTO.getUserSn());
                    tokenParams_eth1.put("blcNtwrkId", "1001");
                    tokenParams_eth1.put("cxSymbolCode", "ETH");
                    tokenMapList.add(tokenParams_eth1);

                    Map<String, Object> tokenParams_eth2 = new HashMap<>();
                    tokenParams_eth2.put("userSn", insertUserResponseDTO.getUserSn());
                    tokenParams_eth2.put("blcNtwrkId", "1001");
                    tokenParams_eth2.put("cxSymbolCode", "LM");
                    tokenMapList.add(tokenParams_eth2);

                    //BNB 메인넷
                    Map<String, Object> tokenParams_bnb1 = new HashMap<>();
                    tokenParams_bnb1.put("userSn", insertUserResponseDTO.getUserSn());
                    tokenParams_bnb1.put("blcNtwrkId", "1002");
                    tokenParams_bnb1.put("cxSymbolCode", "BNB");
                    tokenMapList.add(tokenParams_bnb1);

                    Map<String, Object> tokenParams_bnb2 = new HashMap<>();
                    tokenParams_bnb2.put("userSn", insertUserResponseDTO.getUserSn());
                    tokenParams_bnb2.put("blcNtwrkId", "1002");
                    tokenParams_bnb2.put("cxSymbolCode", "LM");
                    tokenMapList.add(tokenParams_bnb2);

                    int rs = authMapper.insertUserToken(tokenMapList);
                    if (rs < 1) return new ResultDTO(404, "Insert Error.");

                } else {
                    return new ResultDTO(400, "Account already exists.");
                }
            }
        }catch (SocketException se){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 트랜잭션 롤백
            return new ResultDTO(422, se.getMessage());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 트랜잭션 롤백
            e.getMessage();
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return resultDTO;
    }

    public Map<String, Object> sendUserSn(HttpHeaders httpHeaders, LocalDateTime userSbscrbDt, InsertUserRequestDTO insertUserRequestDTO){

        Map<String, Object> putApiResult = new HashMap<>();
        try{

            Map<String, Object> putApiBody = new HashMap<>();
            putApiBody.put("mode", "init");
            putApiBody.put("userSbscrbDt", userSbscrbDt);

            String putApiUrl = authApiUrl + "/pn-api/v1/me";

            putApiResult = PlaynommUtil.sendApi(httpHeaders, putApiBody, putApiUrl, "PUT");
            if (!putApiResult.get("resultCode").equals(0)) throw new Exception("Api Error :" + putApiResult);

        }catch (Exception e){
            putApiResult.put("code", 422);
            putApiResult.put("msg", e.getMessage());
            return putApiResult;
        }
        int changeStatus = authMapper.updateJoinStatus(insertUserRequestDTO);
        if (changeStatus == 0){
            putApiResult.put("code", 404);
            putApiResult.put("msg", "User not found.");
        }else{
            putApiResult.put("code", 200);
            putApiResult.put("msg", "");
        }

        return putApiResult;
    }

    public ResultDTO logout(LogOutRequestDTO logOutRequestDTO){

        int rs = authMapper.logout(logOutRequestDTO);
        if(rs < 1) log.info("Logout result count 0.");

        return new ResultDTO(StatusCode.ACCESS);
    }

    public ResultDTO getPrivateKey(Integer userSn){

        String uuid = null;
        KeyPairDTO keyPairDTO= null;
        PrivateKeyResponseDTO privateKeyResponseDTO = new PrivateKeyResponseDTO();

        try {
            //uuid 조회
            EtherAddressInfoResponseDTO etherAddressInfoResponseDTO = authMapper.getEtherAddressInfo(userSn);
            uuid = etherAddressInfoResponseDTO.getUserUuid();
            if (uuid == null) return new ResultDTO<>(StatusCode.USER_NOT_EXISTS);
            
            //분리된 private key 병합
            String joinedKey = manageKeyService.joinPrivateKey(userSn,etherAddressInfoResponseDTO.getCxwaletAdres(), "1001");
            etherAddressInfoResponseDTO.setUserPrivky(joinedKey);

            //이더리움 지갑 정보 입력
            keyPairDTO = new KeyPairDTO(etherAddressInfoResponseDTO.getUserPrivky(),
                    etherAddressInfoResponseDTO.getUserPblky(), etherAddressInfoResponseDTO.getCxwaletAdres(), null);

            privateKeyResponseDTO.setPrivateKey(etherAddressInfoResponseDTO.getUserPrivky());

            ResultDTO addPublicKeyDTO = leisureMetaService.addPublicKeySummaries(uuid,keyPairDTO);
            if(addPublicKeyDTO.getCode()!=200) return new ResultDTO<>(StatusCode.BASIC_ERROR);

            //Ether 지갑 매핑(update Account) - 결과값에 상관없이 진행
            ResultDTO updateAccountResultDTO = leisureMetaService.updateAccount(uuid,keyPairDTO);

        }catch (WalletException we){
            we.getMessage();
           return new ResultDTO<>(StatusCode.BASIC_ERROR);
        }catch(Exception e){
            e.getMessage();
           return new ResultDTO<>(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO<>(StatusCode.ACCESS,privateKeyResponseDTO);
    }

    public ResultDTO checkDuplicate(Integer userSn, String loginUuid){

        Map<String, Object> params = new HashMap<>();
        params.put("userSn", userSn);
        params.put("loginUuid", loginUuid);

        Map<String, Object> responseData = new HashMap<>();

        int rs = authMapper.checkDuplicate(params);
        if(rs > 0){
            responseData.put("duplicate",true);
            return  new ResultDTO(StatusCode.ACCESS,responseData);
        }

        responseData.put("duplicate",false);
        return new ResultDTO(StatusCode.ACCESS,responseData);
    }

    public ResultDTO checKyc(Integer userSn){

        //kyc인증여부 api 호출
        KycResponseDTO kycResponseDTO;
        try{

            String accessToken = jwtProvider.generateAccessToken(userSn);

            homeResponseDTO = homeMapper.selectUserInfo(userSn);

            if(homeResponseDTO == null) return new ResultDTO(StatusCode.NOT_FOUND);

            Map<String, Object> kycParams = jwtProvider.getKycAt(homeResponseDTO.getUserCmmnSn(), accessToken);

            kycResponseDTO = new KycResponseDTO(String.valueOf(kycParams.get("kycAt")),String.valueOf(kycParams.get("kycCode")),String.valueOf(kycParams.get("kycMessage")));


        }catch (Exception e){
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return new ResultDTO(StatusCode.ACCESS,kycResponseDTO);
    }

}

