package com.playnomm.wallet.mapper.nft;

import com.playnomm.wallet.dto.nft.request.SaveNftOrderRequestDTO;
import com.playnomm.wallet.dto.nft.response.NftInfoResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.nft
 * fileName : NftMapper
 * author :  ljs7756
 * date : 2023-01-10
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-10                ljs7756             최초 생성
 */
@Mapper
public interface NftMapper {
    List<NftInfoResponseDTO> selectUserNft(Integer userSn);

    int saveNftOrder(List<SaveNftOrderRequestDTO> saveNftOrderRequestDTOList);

}
