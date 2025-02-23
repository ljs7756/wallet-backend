package com.playnomm.wallet.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.util
 * fileName : PlaynommUtil
 * author :  ljs7756
 * date : 2022-12-27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-27                ljs7756             최초 생성
 */
@Component
public class PlaynommUtil {
    public static final String ALGORITHM = "HmacSHA256";

    public static String secretKey;

    public static String credentialId;

    @Value("${jwt.secret-key}")
    public void setSecretKey(String value) {
        secretKey = value;
    }

    @Value("${auth.credential.id}")
    public void setCredentialId(String value) {
        credentialId = value;
    }

    public static Map<String, Object> sendApi(HttpHeaders httpHeaders, Map<String, Object> body, String url, String method) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> requestMessage = new HttpEntity<>(body, httpHeaders);
        HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.valueOf(method), requestMessage, String.class);

        // 결과값 Json Parsing
        Map<String, Object> result;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,true);
        result = objectMapper.readValue(response.getBody(), Map.class);

        return result;
    }

    public static String getPnAuthorization(long userCmmnSn){


        long expire = new Date().getTime() + (5 * 60 * 1000);

        String p1 = calculateHMac(credentialId, secretKey);
        String p2 = calculateHMac(p1 + expire, secretKey);
        String p3 = calculateHMac(p2 + userCmmnSn, secretKey);
        String signature = encryptAES256(p3, credentialId, secretKey);

        //System.out.println("Credential="+credentialId+"/"+expire+"/"+userCmmnSn+",Signature="+signature);
        String result = "Credential="+credentialId+"/"+expire+"/"+userCmmnSn+",Signature="+signature;

        return result;
    }
    public static String calculateHMac(String data, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance(ALGORITHM);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes("UTF-8"), ALGORITHM);
            sha256_HMAC.init(secret_key);
            return byteArrayToHex(sha256_HMAC.doFinal(data.getBytes("UTF-8")));

        } catch (Exception e) {
            return "";
        }
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }


    public static String alg = "AES/CBC/PKCS5Padding";
    public static String encryptAES256(String text, String clientId, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(clientId.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return "";
        }

    }

    public static String decryptAES256(String cipherText, String clientId, String secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(alg);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivParamSpec = new IvParameterSpec(clientId.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static HttpHeaders  createCommonAuthHttpHeader(String token, int userCmmnSn) {
        String pnAuthorization = getPnAuthorization(Long.valueOf(userCmmnSn));

        //api 전송 시작
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("PN-Authorization", pnAuthorization);
        httpHeaders.set("Authorization","Bearer "+ token);

        return httpHeaders;
    }

}
