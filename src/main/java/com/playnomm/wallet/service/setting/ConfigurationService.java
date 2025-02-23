package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.ChangeLanguageDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSAuthDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSymbolDTO;
import com.playnomm.wallet.dto.setting.response.ConfigurationDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.ConfigurationMapper;
import org.springframework.stereotype.Service;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : ConfiguratonService
 * author :  evilstorm
 * date : 2022/12/27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/27              evilstorm             최초 생성
 */
@Service
public class ConfigurationService {

    private ConfigurationMapper mapper;
    public ConfigurationService(ConfigurationMapper mapper) {
        this.mapper = mapper;
    }

    public ResultDTO getConfiguration(int userSn) {
        ConfigurationDTO result = mapper.getConfiguration(userSn);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO patchSymbol(int userSn, ChangeSymbolDTO params) {
        params.setUserSn(userSn);
        int patchResult = mapper.patchSymbol(params);

        if(patchResult < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return getConfiguration(userSn);
    }

    public ResultDTO patchLanguage(int userSn, ChangeLanguageDTO params) {
        params.setUserSn(userSn);
        int patchResult = mapper.patchLanguage(params);

        if(patchResult < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return getConfiguration(userSn);
    }
    public ResultDTO patchSAuth(int userSn, ChangeSAuthDTO params) {
        params.setUserSn(userSn);
        int patchResult = mapper.patchSAuth(params);

        if(patchResult < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO(StatusCode.ACCESS);
    }

}
