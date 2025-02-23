package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.ChangeLanguageDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSAuthDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSymbolDTO;
import com.playnomm.wallet.dto.setting.response.ConfigurationDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * packageName :  com.playnomm.wallet.mapper.setting
 * fileName : CongigurationService
 * author :  evilstorm
 * date : 2022/12/27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/27              evilstorm             최초 생성
 */
@Mapper
public interface ConfigurationMapper {
    ConfigurationDTO getConfiguration(int userSn);
    int patchSymbol(ChangeSymbolDTO params);
    int patchLanguage(ChangeLanguageDTO params);
    int patchSAuth(ChangeSAuthDTO params);

}
