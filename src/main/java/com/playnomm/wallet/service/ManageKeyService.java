package com.playnomm.wallet.service;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.auth.AuthMapper;
import com.playnomm.wallet.mapper2.user.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service
 * fileName : ManageKeyService
 * author :  ljs7756
 * date : 2023-02-10
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-10                ljs7756             최초 생성
 */
@Service
@Slf4j
public class ManageKeyService {

    @Autowired
    AwsService awsService;

    @Autowired
    AuthMapper authMapper;

    @Autowired
    UserMapper userMapper;

    public String[] dividePrivateKey (String privateKey){
        int key_len = privateKey.length();

        String[] arrPrivateKey = {awsService.awsKmsEncrypt (privateKey.substring(0,key_len/2)), awsService.awsKmsEncrypt (privateKey.substring(key_len/2))};

        log.info("private key: {}"+ privateKey);
        log.info("divided private key 1: " + privateKey.substring(0,key_len/2));
        log.info("divided private key 2: " + privateKey.substring(key_len/2));
        log.info("encoded private key 1: " + arrPrivateKey[0]);
        log.info("encoded private key 2: " + arrPrivateKey[1]);

        return arrPrivateKey;
    }

    public String joinPrivateKey(Integer userSn, String smrtCntrctAdres, String blcNtwrkId ){

        Map<String, Object> params = new HashMap<>();
        params.put("userSn", userSn);
        params.put("smrtCntrctAdres", smrtCntrctAdres);
        params.put("blcNtwrkId", blcNtwrkId);

        String returnPrivateKey="";
        try{
            // 보관된 key를 디코딩 후 결합하여 다시 인코딩.
            String privateKey1 = awsService.awsKmsDecrypt(authMapper.getPrivateKey1(params));
            String privateKey2 = awsService.awsKmsDecrypt(userMapper.getPrivateKey2(params));

            returnPrivateKey = awsService.awsKmsEncrypt(privateKey1+privateKey2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return returnPrivateKey;
    }

    public String joinMngrPrivateKey(String blcMngrId){

        Map<String, Object> params = new HashMap<>();
        params.put("blcMngrId", blcMngrId);

        String returnPrivateKey="";
        try{
            // 보관된 key를 디코딩 후 결합하여 다시 인코딩.
            Map<String, Object> resultMap = authMapper.getMngrPrivateKey1(params);
            if(resultMap == null) throw new Exception();
            
            //db2에 관리자 private key 조회를 위한 파라미터 추가
            params.put("blcMngrBassSn", Integer.parseInt(resultMap.get("BLC_MNGR_BASS_SN").toString()));

            String privateKey1 = awsService.awsKmsDecrypt(resultMap.get("BLC_MNGR_PRIVKY").toString());
            String privateKey2 = awsService.awsKmsDecrypt(userMapper.getMngrPrivateKey2(params));

            returnPrivateKey = awsService.awsKmsEncrypt(privateKey1+privateKey2);

        }catch (Exception e){
            e.printStackTrace();
        }
        return returnPrivateKey;
    }


    public ResultDTO migrationPrivateKey(){

        List<Map<String, Object>> arrList;
        try{
            arrList = authMapper.migrationPrivateKey();
            for ( Map<String, Object> data : arrList ) {

                String prkOrigin = awsService.awsKmsDecrypt(data.get("USER_PRIVKY").toString());
                String[] arrPrk = dividePrivateKey(prkOrigin);

                Map<String, Object> params = new HashMap<>();
                params.put("userSn", data.get("USER_SN"));
                params.put("blcNtwrkId", data.get("BLC_NTWRK_ID"));
                params.put("cxwaletAdres", data.get("CXWALET_ADRES"));
                params.put("userPrivky", arrPrk[0]);
                params.put("userPrivky2", arrPrk[1]);

                int rs = authMapper.updateMigrationPrivateKey(params);
               // if(rs > 0) userMapper.insertPrivateKey(params);

            }

        }catch (Exception e){
       //     TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // 트랜잭션 롤백
            e.printStackTrace();
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return new ResultDTO(StatusCode.ACCESS);
    }

    public ResultDTO decodeKey(String Key){

        String prkOrigin = awsService.awsKmsDecrypt(Key);

        return new ResultDTO(StatusCode.ACCESS,prkOrigin);
    }

    public ResultDTO incodeKey(String Key){

        String prkOrigin = awsService.awsKmsEncrypt(Key);

        return new ResultDTO(StatusCode.ACCESS,prkOrigin);
    }
}
