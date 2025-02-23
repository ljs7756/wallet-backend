package com.playnomm.wallet.config.security;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.auth.response.AuthResponseDTO;
import com.playnomm.wallet.dto.auth.response.KycResponseDTO;
import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.UserJwtDifferentException;
import com.playnomm.wallet.mapper.auth.AuthMapper;
import com.playnomm.wallet.mapper.home.HomeMapper;
import com.playnomm.wallet.service.home.HomeService;
import com.playnomm.wallet.util.PlaynommUtil;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.config.security
 * fileName : JwtProvider
 * author :  ljs77
 * date : 2022-11-28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-28                ljs77             최초 생성
 */
@Component
public class JwtProvider {

    @Autowired
    AuthMapper authMapper;

    @Autowired
    CustomUserDetailService customUserDetailService;

    HomeResponseDTO homeResponseDTO;
    @Autowired
    HomeMapper homeMapper;

    @Value("${playnomm.auth.api.url}")
    String authApiUrl;

    // secret key
    @Value("${jwt.secret-key}")
    private String secretKey;

    // access token 유효시간
    private long accessTokenValidTime =  2 * 60 * 60 * 1000L; //2시간  5 * 60 * 1000L; //5분

    // refresh token 유효시간
    private long refreshTokenValidTime = 30 * 24 * 60 * 60 * 1000L; //30일


    @PostConstruct
    private void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 토큰에서 Header 추출
     */
    private JwsHeader getHeaderFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getHeader();
    }

    /**
     * 토큰에서 Claim 추출
     */
    private Claims getClaimsFormToken(String token) {
        return Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();
    }

    /**
     * 토큰에서 인증 subject 추출
     */
    private String getSubject(String token) {
        return getClaimsFormToken(token).getSubject();
    }

    /**
     * 토큰에서 인증 정보 추출
     */
    public Authentication getAuthentication(String token) {

        UserDetails userDetails = customUserDetailService.loadUserByUsername(Integer.parseInt(this.getSubject(token)),token);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * 토큰 발급
     */
    public ResultDTO generateJwtToken(Integer userSn, String loginSttusCode, String loginUuid) {

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setGrantType("Bearer");

        Claims claims = Jwts.claims().setSubject(String.valueOf(userSn));
        claims.put("userSn", userSn);
        Date now = new Date();
        Date accessExpireDate = new Date(now.getTime() + accessTokenValidTime);
        Date refreshExpireDate = new Date(now.getTime() + refreshTokenValidTime);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessExpireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(refreshExpireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

            //kyc인증여부 api 호출
            try{
                homeResponseDTO = homeMapper.selectUserInfo(userSn);

                if(homeResponseDTO == null) return new ResultDTO(StatusCode.NOT_FOUND);

                Map<String, Object> kycParams = getKycAt(homeResponseDTO.getUserCmmnSn(), accessToken);

                if(kycParams.get("kycAt").equals("N")){
                    KycResponseDTO kycResponseDTO = new KycResponseDTO(String.valueOf(kycParams.get("kycAt")), String.valueOf(kycParams.get("kycCode")),String.valueOf(kycParams.get("kycMessage")));
                    return new ResultDTO(StatusCode.KYC_UNAUTHORIZED, kycResponseDTO);
                }

            }catch (Exception e){
                return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
            }

        //refreshToken은 Db에 저장한다
        Map<String, Object> paramas = new HashMap<>();
        paramas.put("userSn",userSn);
        paramas.put("accessToken",accessToken);
        paramas.put("refreshToken",refreshToken);
        int rs = authMapper.loginUser(paramas);
        if(rs < 1){
            return new ResultDTO(404,"User Does not exist.");
        }else{
            // 로그 insert
            Map<String, Object> logParamas= new HashMap<>();
            logParamas.put("userSn",userSn);
            logParamas.put("loginSttusCode",loginSttusCode);
            logParamas.put("accessToken",accessToken);
            logParamas.put("refreshToken",refreshToken);
            logParamas.put("loginDt",now);
            logParamas.put("loginEndDt",accessExpireDate);
            logParamas.put("loginUuid",loginUuid);


           int rsLog =  authMapper.insertLoginLog(logParamas);
        }

        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        authResponseDTO.setUserSn(userSn);


        return new ResultDTO(StatusCode.ACCESS,authResponseDTO);
    }

    public String generateAccessToken (Integer userSn){

        Claims claims = Jwts.claims().setSubject(String.valueOf(userSn));
        claims.put("userSn", userSn);
        Date now = new Date();
        Date accessExpireDate = new Date(now.getTime() + accessTokenValidTime);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessExpireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        return accessToken;
    }

    /**
     * 토큰 검증
     */
    public int validateToken(String token) {
        int statusCode = 200;
        try {
            Claims claims = getClaimsFormToken(token);
            if(!claims.getExpiration().before(new Date())){ //만료 전
                statusCode = StatusCode.ACCESS.getCode();
            }
        } catch (ExpiredJwtException e ) {
            return StatusCode.TOKEN_EXPIRED.getCode();
        } catch (JwtException | IllegalArgumentException e) {
            return StatusCode.BAD_REQUEST.getCode();
        }
        return statusCode;
    }

    /**
     * 토큰 검증
     * JwtFilter 사용
     */
    public Boolean validateTokenFilter(String token) {

        try {
            Claims claims = getClaimsFormToken(token);
            if(!claims.getExpiration().before(new Date())){ //만료 전
               return true;
            }
        } catch (ExpiredJwtException e ) {
            throw new ExpiredJwtException(getHeaderFormToken(token),getClaimsFormToken(token),"Token Expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Token Bad Request");
        }
        return false;
    }

    /**
     * Request Header에서 토큰 추출
     */
    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Map<String, Object> getKycAt(Integer userCmmnSn, String accessToken) throws Exception{

        String pnAuthorization = PlaynommUtil.getPnAuthorization(Long.valueOf(userCmmnSn));

        //api 전송 시작
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("PN-Authorization", pnAuthorization);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        Map<String, Object> body = new HashMap<>();

        String url = authApiUrl + "/pn-api/v1/me?fields=info";

        Map<String, Object> result = PlaynommUtil.sendApi(httpHeaders, body, url, "GET");

        if(!result.get("resultCode").equals(0)) throw new Exception("Api Error: "+result);

        String kycAt = String.valueOf(((HashMap)result.get("data")).get("kycAt"));

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("kycAt", kycAt);

        if(kycAt.equals("N")){
            resultMap.put("kycCode", String.valueOf(((HashMap)result.get("data")).get("kycCode")));
            resultMap.put("kycMessage", String.valueOf(((HashMap)result.get("data")).get("kycMessage")));
        }



        return resultMap;
    }


}
