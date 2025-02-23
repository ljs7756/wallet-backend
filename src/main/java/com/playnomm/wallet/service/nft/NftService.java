package com.playnomm.wallet.service.nft;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.nft.request.SaveNftOrderRequestDTO;
import com.playnomm.wallet.dto.nft.response.NftInfoResponseDTO;
import com.playnomm.wallet.dto.token.request.SaveOrderRequestDTO;
import com.playnomm.wallet.dto.token.response.TokenInfoResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.nft.NftMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.nft
 * fileName : NftService
 * author :  ljs7756
 * date : 2022-12-27
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-27                ljs7756             최초 생성
 */
@Service
public class NftService {

    @Autowired
    NftMapper nftMapper;

    public ResultDTO getNftList(Integer userSn)
    {
        List<NftInfoResponseDTO> nftInfoResponseDTOList = nftMapper.selectUserNft(userSn);

        return new ResultDTO(StatusCode.ACCESS,nftInfoResponseDTOList);
    }

    public ResultDTO saveNftOrder(List<SaveNftOrderRequestDTO> saveNftOrderRequestDTOList)
    {

        int rs = nftMapper.saveNftOrder(saveNftOrderRequestDTOList);
        if(rs < 1) return  new ResultDTO(400,"Sql result 0");

        return new ResultDTO(StatusCode.ACCESS);
    }
}
