package com.playnomm.wallet.mapper.token;

import com.playnomm.wallet.dto.token.request.SaveAdditionRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveDirectTokenRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveOrderRequestDTO;
import com.playnomm.wallet.dto.token.response.TokenAdditionResponseDTO;
import com.playnomm.wallet.dto.token.response.TokenInfoResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.token
 * fileName : TokenMapper
 * author :  ljs7756
 * date : 2023-01-03
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-03                ljs7756             최초 생성
 */
@Mapper
public interface TokenMapper {
    List<TokenInfoResponseDTO> selectUserToken(Map<String, Object> map);

    List<TokenAdditionResponseDTO> selectAllToken(Map<String, Object> map);

    int saveAdditionTokenList(List<SaveAdditionRequestDTO> saveAdditionRequestDTOList);

    int saveDirectToken(SaveDirectTokenRequestDTO saveDirectTokenRequestDTO);

    int saveTokenOrder(List<SaveOrderRequestDTO> saveOrderRequestDTOList);

    int checkSmartContract(SaveDirectTokenRequestDTO saveDirectTokenRequestDTO);

}
