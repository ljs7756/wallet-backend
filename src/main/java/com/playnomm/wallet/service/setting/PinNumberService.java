package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.PinNumberPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.PinNumberCheckResponseDTO;
import com.playnomm.wallet.dto.setting.response.PinNumberEnableStateResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.PinNumberMapper;
import com.playnomm.wallet.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : PinNumberService
 * author :  evilstorm
 * date : 2023/01/10
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/10              evilstorm             최초 생성
 */
@Service
public class PinNumberService {

    private PinNumberMapper mapper;
    public PinNumberService(PinNumberMapper mapper){
        this.mapper = mapper;
    }

    public ResultDTO getPinNumberEnableState(int userSn) {
        PinNumberEnableStateResponseDTO result = mapper.getPinNumberEnableState(userSn);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO checkPinNumber(PinNumberPatchRequestDTO params) {
        String encPin = CommonUtil.getSHA256(params.getPinNumber());
        params.setPinNumber(encPin);

        HashMap<String, Object> queryResult = mapper.findUserWithPinNumber(params);

        PinNumberCheckResponseDTO result = new PinNumberCheckResponseDTO();
        result.setPinCrtfcAt("Y");

        if(queryResult == null || queryResult.isEmpty()) {
            result.setPinCrtfcAt("N");
            return new ResultDTO(StatusCode.ACCESS, result);
        }

        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO modifyPinNumber(PinNumberPatchRequestDTO params) {

        String encPin = CommonUtil.getSHA256(params.getPinNumber());
        params.setPinNumber(encPin);

        int result = mapper.modifyPinNumber(params);

        if(result < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO postPinNumber(PinNumberPatchRequestDTO params) {

        String encPin = CommonUtil.getSHA256(params.getPinNumber());
        params.setPinNumber(encPin);

        int result = mapper.postPinNumber(params);

        if(result < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO(StatusCode.ACCESS, null);
    }
}
