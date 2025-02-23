package com.playnomm.wallet.service.token;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.token.request.SaveAdditionRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveDirectTokenRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveOrderRequestDTO;
import com.playnomm.wallet.dto.token.response.TokenAdditionResponseDTO;
import com.playnomm.wallet.dto.token.response.TokenInfoResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.token.TokenMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.token
 * fileName : TokenService
 * author :  ljs7756
 * date : 2022-12-23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-23                ljs7756             최초 생성
 */
@Service
public class TokenService {

    @Value("${root.path}")
    String rootPath;

    @Autowired
    TokenMapper tokenMapper;

    public ResultDTO getTokenList(Map<String, Object> params)
    {
        List<TokenInfoResponseDTO> tokenInfoResponseDTOList = tokenMapper.selectUserToken(params);

        return new ResultDTO(StatusCode.ACCESS,tokenInfoResponseDTOList);
    }

    public ResultDTO getTokenAllList(Map<String, Object> params)
    {

        List<TokenAdditionResponseDTO> tokenAdditionResponseDTOList = tokenMapper.selectAllToken(params);

        return  new ResultDTO(StatusCode.ACCESS,tokenAdditionResponseDTOList);
    }

    @Transactional
    public ResultDTO saveTokenList(List<SaveAdditionRequestDTO> saveAdditionRequestDTOList)
    {
        int rs = tokenMapper.saveAdditionTokenList(saveAdditionRequestDTOList);
        if(rs < 1) return  new ResultDTO(400,"Sql result 0");

        return new ResultDTO(StatusCode.ACCESS);
    }

    @Transactional
    public ResultDTO saveDirectToken(SaveDirectTokenRequestDTO saveDirectTokenRequestDTO)
    {
        int checkSmartContract = tokenMapper.checkSmartContract(saveDirectTokenRequestDTO);
        if (checkSmartContract > 0) return  new ResultDTO(StatusCode.DUPLICATE_DATA);

        int rs = tokenMapper.saveDirectToken(saveDirectTokenRequestDTO);
        if(rs < 1) return  new ResultDTO(400,"Sql result 0");

        return new ResultDTO(StatusCode.ACCESS);
    }
    @Transactional
    public ResultDTO saveTokensOrder(List<SaveOrderRequestDTO> saveOrderRequestDTOList)
    {
        int rs= tokenMapper.saveTokenOrder(saveOrderRequestDTOList);
        if(rs < 1) return  new ResultDTO(400,"Sql result 0");

        return new ResultDTO(StatusCode.ACCESS);
    }



}
