package com.playnomm.wallet.mapper.home;

import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.home
 * fileName : HomeMapper
 * author :  ljs7756
 * date : 2022-12-14
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-14                ljs7756             최초 생성
 */
@Mapper
public interface HomeMapper {
    HomeResponseDTO selectUserInfo(Integer userSn);

    int modifyBaseNetwork(Map<String, Object> map);
}
