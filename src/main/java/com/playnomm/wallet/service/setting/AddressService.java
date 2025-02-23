package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.AddressPatchRequestDTO;
import com.playnomm.wallet.dto.setting.request.AddressRequestDTO;
import com.playnomm.wallet.dto.setting.response.AddressResponseDTO;
import com.playnomm.wallet.dto.setting.response.MainNetResponseDTO;
import com.playnomm.wallet.dto.setting.response.RecentAddressResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.AddressMapper;
import com.playnomm.wallet.util.CommonUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.setting
 * fileName : AddressService
 * author :  evilstorm
 * date : 2022/12/26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/26              evilstorm             최초 생성
 */
@Service
public class AddressService {

    AddressMapper addressMapper;
    public AddressService(AddressMapper mapper) {
        this.addressMapper = mapper;
    }

    public ResultDTO getAddressList(int userSn) {

        List<AddressResponseDTO> result = addressMapper.getAddressList(
                new HashMap<String, Object>() {{
                    put("userSn", userSn);
                    put("selfAdbkAt", "N");
                }}
        );

        return new ResultDTO(StatusCode.ACCESS, result);
    }


    public ResultDTO getMainNetList() {

        List<MainNetResponseDTO> result = addressMapper.getMainNetList();

        return new ResultDTO(StatusCode.ACCESS, result);
    }


    public ResultDTO searchAddressBySymbol(int userSn, String symbol) {
        List<AddressResponseDTO> result = addressMapper.getAddressList(
                new HashMap<String, Object>() {{
                    put("userSn", userSn);
                    put("cxSymbolCode", symbol);
                    put("selfAdbkAt", "N");
                }}
        );
        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO postAddress(AddressRequestDTO params) {

        //address 0x 자르기
        params.setAdbkCxwaletAdres(CommonUtil.modifyWalletAddress(params.getAdbkCxwaletAdres()));

        //address 중복여부 확인
        int duplicateCheck = addressMapper.checkAddress(params);

        if(duplicateCheck > 0) {
            //중복값 있는 경우 Update
            int updateResult = addressMapper.updateAddressInfo(params);
            if(updateResult < 1) return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);

        }else {
            params.setSelfAdbkAt("N");
            int postResult = addressMapper.postAddress(params);

            if(postResult < 1) {
                return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
            }
        }

        AddressResponseDTO result = addressMapper.postAfterSearch(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO deleteAddress(int sn) {
        int result = addressMapper.deleteBySn(sn);

        return new ResultDTO(StatusCode.ACCESS, null);
    }
    public ResultDTO patchAddress(int sn, AddressPatchRequestDTO body) {
        body.setUserCxwaletAdbkSn(sn);

        //address 0x 자르기
        body.setAdbkCxwaletAdres(CommonUtil.modifyWalletAddress(body.getAdbkCxwaletAdres()));

        int patchResult = addressMapper.patchBySn(body);

        AddressResponseDTO result = addressMapper.findOneBySn(sn);
        return new ResultDTO(StatusCode.ACCESS, result);

    }

    public ResultDTO getRecentAddressForToken(Map<String, Object> params) {

        List<RecentAddressResponseDTO> recentAddressResponseDTOList = addressMapper.getRecentAddressForToken(params);

        return new ResultDTO(StatusCode.ACCESS, recentAddressResponseDTOList);
    }

    public ResultDTO getRecentAddressForNft(Integer userSn) {

        List<RecentAddressResponseDTO> recentAddressResponseDTOList = addressMapper.getRecentAddressForNft(userSn);

        return new ResultDTO(StatusCode.ACCESS, recentAddressResponseDTOList);
    }

}
