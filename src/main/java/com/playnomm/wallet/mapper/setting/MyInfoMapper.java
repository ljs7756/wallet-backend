package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.response.MyInfoResponseDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * packageName :  com.playnomm.wallet.mapper.setting
 * fileName : MyInfoMapper
 * author :  evilstorm
 * date : 2022/12/29
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/29              evilstorm             최초 생성
 */
@Mapper
public interface MyInfoMapper {
    MyInfoResponseDTO getMyInfo(int userSn);


}
