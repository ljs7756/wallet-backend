package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.WalletPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.BriefMyInfoDTO;
import com.playnomm.wallet.dto.setting.response.WalletInfoResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.util.HashMap;
import java.util.List;

/**
 * packageName :  com.playnomm.wallet.mapper.setting
 * fileName : MyMainMapper
 * author :  evilstorm
 * date : 2022/12/23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/23              evilstorm             최초 생성
 */
@Mapper
@Alias("MyMainMapper")
public interface MyMainMapper {
    BriefMyInfoDTO getBriefMyInfo(int userSn);
    List<WalletInfoResponseDTO> getMyWallet(int userSn);
    WalletInfoResponseDTO getWallet(HashMap<String, Object> params);
    int patchWallet(WalletPatchRequestDTO data);
}
