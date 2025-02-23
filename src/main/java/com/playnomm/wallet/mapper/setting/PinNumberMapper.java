package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.PinNumberPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.PinNumberEnableStateResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

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
public interface PinNumberMapper {
    PinNumberEnableStateResponseDTO getPinNumberEnableState(int userSn);
    HashMap findUserWithPinNumber(PinNumberPatchRequestDTO params);
    int postPinNumber(PinNumberPatchRequestDTO params);
    int modifyPinNumber(PinNumberPatchRequestDTO params);


}
