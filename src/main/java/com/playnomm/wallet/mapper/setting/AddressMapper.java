package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.AddressPatchRequestDTO;
import com.playnomm.wallet.dto.setting.request.AddressRequestDTO;
import com.playnomm.wallet.dto.setting.response.AddressResponseDTO;
import com.playnomm.wallet.dto.setting.response.MainNetResponseDTO;
import com.playnomm.wallet.dto.setting.response.RecentAddressResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.setting
 * fileName : AddressService
 * author :  evilstorm
 * date : 2022/12/26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/26              evilstorm             최초 생성
 */
@Mapper
public interface AddressMapper {

    List<AddressResponseDTO> getAddressList(HashMap params);
    List<MainNetResponseDTO> getMainNetList();
    AddressResponseDTO findOneBySn(int userCxwaletAdbkSn);
    AddressResponseDTO postAfterSearch(AddressRequestDTO params);
    int deleteBySn(int userCxwaletAdbkSn);
    int patchBySn(AddressPatchRequestDTO AddressPatchRequestDTO);
    int postAddress(AddressRequestDTO params);
    int updateAddressInfo(AddressRequestDTO params);
    int checkAddress(AddressRequestDTO params);
    List<RecentAddressResponseDTO> getRecentAddressForToken(Map<String, Object> params);
    List<RecentAddressResponseDTO> getRecentAddressForNft(Integer userSn);

}
