package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.response.TermsResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.TermsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : TermsService
 * author :  evilstorm
 * date : 2022/12/28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/28              evilstorm             최초 생성
 */
@Service
public class TermsService {

    private TermsMapper termsMapper;

    @Value("${SYS_ID}")
    private String sysId;

    public TermsService(TermsMapper mapper) {
        this.termsMapper = mapper;
    }

    public ResultDTO getTermsList(String langCode, int userSn) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("sysId", sysId);
        params.put("userSn", userSn);
        params.put("langCode", langCode);

        List<TermsResponseDTO> result = termsMapper.getTermsList(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO getTermsDetail(String langCode, String stplatId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("sysId", sysId);
        params.put("stplatId", stplatId);
        params.put("langCode", langCode);

        TermsResponseDTO result =  termsMapper.getTermsDetail(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }

}
