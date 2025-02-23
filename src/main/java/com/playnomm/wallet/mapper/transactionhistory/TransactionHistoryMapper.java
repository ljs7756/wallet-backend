package com.playnomm.wallet.mapper.transactionhistory;

import com.playnomm.wallet.dto.transaction.response.NftTransactionHistoryResponseDTO;
import com.playnomm.wallet.dto.transaction.response.TransactionHistoryResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.mapper.transactionhistory
 * fileName : TransactionHistoryMapper
 * author :  ljs7756
 * date : 2023-01-06
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-06                ljs7756             최초 생성
 */
@Mapper
public interface TransactionHistoryMapper {
    List<TransactionHistoryResponseDTO> selectTokenTransactionHistory(Map<String, Object> params);

    TransactionHistoryResponseDTO selectTokenTransactionHistoryDetail(Integer cxTradeSn);

    List<NftTransactionHistoryResponseDTO> selectNftTransactionHistory(Map<String, Object> params);

    NftTransactionHistoryResponseDTO selectNftTransactionHistoryDetail(Integer cxTradeSn);
}
