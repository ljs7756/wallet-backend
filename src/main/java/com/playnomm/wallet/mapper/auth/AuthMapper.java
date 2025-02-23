package com.playnomm.wallet.mapper.auth;

import com.playnomm.wallet.dto.auth.request.InsertUserRequestDTO;
import com.playnomm.wallet.dto.auth.request.LogOutRequestDTO;
import com.playnomm.wallet.dto.auth.response.EtherAddressInfoResponseDTO;
import com.playnomm.wallet.dto.auth.response.InsertUserResponseDTO;
import com.playnomm.wallet.dto.auth.response.UserAccessTokenResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.auth
 * fileName : AuthMapper
 * author :  ljs7756
 * date : 2022-12-27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-27                ljs7756             최초 생성
 */
@Mapper
public interface AuthMapper {

    int insertUser(InsertUserRequestDTO insertUserRequestDTO);
    int insertUserWallet(List<Map<String, Object>> mapList);
    int insertUserToken(List<Map<String, Object>> mapList);
    InsertUserResponseDTO selectInsertUser(Integer userCmmnSn);
    int loginUser(Map<String, Object> params);
    int updateUserInfo(Map<String, Object> params);
    UserAccessTokenResponseDTO getUserAccessToken(Integer userSn);
    int logout (LogOutRequestDTO logOutRequestDTO);
    UserAccessTokenResponseDTO getPreJoinUser(InsertUserRequestDTO insertUserRequestDTO);
    int updateJoinStatus(InsertUserRequestDTO insertUserRequestDTO);
    EtherAddressInfoResponseDTO getEtherAddressInfo(Integer userSn);
    int insertLoginLog(Map<String, Object> params);
    int checkDuplicate(Map<String, Object> params);
    int checkLogOut(String loginAccesTkn);
    String getPrivateKey1(Map<String, Object> map);
    Map<String, Object> getMngrPrivateKey1(Map<String, Object> map);
    List<Map<String, Object>> migrationPrivateKey();
    int updateMigrationPrivateKey(Map<String, Object> map);
}
