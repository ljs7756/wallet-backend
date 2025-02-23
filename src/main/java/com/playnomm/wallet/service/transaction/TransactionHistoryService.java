package com.playnomm.wallet.service.transaction;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.transaction.response.NftTransactionHistoryResponseDTO;
import com.playnomm.wallet.dto.transaction.response.TransactionHistoryResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.transactionhistory.TransactionHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service.transaction
 * fileName : TransactionHistoryService
 * author :  ljs7756
 * date : 2022-12-26
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-26                ljs7756             최초 생성
 */
@Service
public class TransactionHistoryService {

    @Autowired
    TransactionHistoryMapper transactionHistoryMapper;

    public ResultDTO getTokenTransactionHistoryList(Map<String, Object> params)
    {

        List<TransactionHistoryResponseDTO> transactionHistoryResponseDTOS = transactionHistoryMapper.selectTokenTransactionHistory(params);

        return new ResultDTO(StatusCode.ACCESS,transactionHistoryResponseDTOS);
    }

    public ResultDTO selectTokenTransactionHistoryDetail(Integer cxTradeSn)
    {

        TransactionHistoryResponseDTO transactionHistoryResponseDTO = transactionHistoryMapper.selectTokenTransactionHistoryDetail(cxTradeSn);

        return new ResultDTO(StatusCode.ACCESS,transactionHistoryResponseDTO);
    }


    public ResultDTO getNftTransactionHistoryList(Map<String, Object> params)
    {

        List<NftTransactionHistoryResponseDTO> nftTransactionHistoryResponseDTOList = transactionHistoryMapper.selectNftTransactionHistory(params);

        return new ResultDTO(StatusCode.ACCESS,nftTransactionHistoryResponseDTOList);
    }

    public ResultDTO getNftTransactionHistoryListDetail(Integer cxTradeSn)
    {

        NftTransactionHistoryResponseDTO nftTransactionHistoryResponseDTO = transactionHistoryMapper.selectNftTransactionHistoryDetail(cxTradeSn);

        return new ResultDTO(StatusCode.ACCESS,nftTransactionHistoryResponseDTO);
    }
}

