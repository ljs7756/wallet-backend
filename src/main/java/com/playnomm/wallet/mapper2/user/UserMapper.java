package com.playnomm.wallet.mapper2.user;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper2.user
 * fileName : UserMapper
 * author :  ljs7756
 * date : 2023-02-07
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-02-07                ljs7756             최초 생성
 */
@Mapper
public interface UserMapper {
    String getPrivateKey2(Map<String, Object> map);
    String getMngrPrivateKey2(Map<String, Object> map);
    int insertUserDividePrivateKey(List<Map<String, Object>> mapList);
    int insertPrivateKey(Map<String, Object> map);
}

