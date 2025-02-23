package com.playnomm.wallet.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.UserJwtDifferentException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * packageName :  com.playnomm.wallet.config.security
 * fileName : ExceptionHandlerFilter
 * author :  ljs77
 * date : 2022-11-30
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-30                ljs77             최초 생성
 */
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            //토큰의 유효기간 만료
            setErrorResponse(response, StatusCode.TOKEN_EXPIRED.getCode(), StatusCode.TOKEN_EXPIRED.getMessage());
        }catch (JwtException e){
            //유효하지 않은 토큰
            setErrorResponse(response, StatusCode.BAD_REQUEST.getCode(), StatusCode.BAD_REQUEST.getMessage());
        }catch (UserJwtDifferentException e){
            //중복로그인 & 로그아웃
            setErrorResponse(response, e.getCode(), e.getMessage());
        }
    }
    private void setErrorResponse(HttpServletResponse response, int errorCode, String message){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(200);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ResultDTO resultDTO = new ResultDTO ();
        resultDTO.setCode (errorCode);
        resultDTO.setMessage (message);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(resultDTO));
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
