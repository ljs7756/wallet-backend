package com.playnomm.wallet.enums;

import lombok.Getter;

/**
 * packageName :  com.playnomm.wallet.enums
 * fileName : TransactionType
 * author :  ljs7756
 * date : 2023-01-06
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-06                ljs7756             최초 생성
 */
@Getter
public enum TransactionType {
    LMC_TRANSFER("TLLF,TLLT"),
    LMC_SWAP("TLEF,TELT"),
    LMC_REWARD("RP01,RP02,RP03,RP05,RP06"),
    ETH_TRANSFER("TEEF,TEET"),
    ETH_SWAP("TELF,TLET"),
    BNB_TRANSFER("TBBF,TBBT"),
    NFT_TRADE("PP12,PP13,PP15,PP22,PP23,PP25"),
    NFT_TRANSFER("NLLF,NLLT"),
    NFT_MINTING("MP01,MP02")
    ;

    private String tradeTypeCode;

    TransactionType(String tradeTypeCode){
        this.tradeTypeCode = tradeTypeCode;
    }
}

