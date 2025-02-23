package com.playnomm.wallet.dto.transaction.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * packageName :  com.playnomm.wallet.dto.transaction.response
 * fileName : TransactionHistoryResponseDTO
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Getter
@Setter
public class TransactionHistoryResponseDTO {
    @Schema(example = "1", description = "거래내역 일련번호")
    private Integer cxTradeSn;

    @Schema(example = "1", description = "사용자 일련번호")
    private Integer userSn;

    @Schema(example = "CXWL", description = "시스템 아이디")
    private String sysId;

    @Schema(example = "1000", description = "BLC 네트워크 아이디")
    private String blcNtwrkId;

    @Schema(example = "LM", description = "심볼 코드")
    private String cxSymbolCode;
    
    @Schema(example = "T", description = "거래 유형 코드")
    private String tradeTyCode;

    @Schema(example = "NPNT", description = "거래 유형 상세 코드")
    private String tradeTyDetailCode;

    @Schema(example = "1", description = "송신자 사용자 일련번호")
    private Integer trsmtrUserSn;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "송신자 지갑 주소")
    private String trsmtrCxwaletAdres;

    @Schema(example = "1", description = "수신자 사용자 일련번호")
    private Integer rcverUserSn;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "수신자 지갑 주소")
    private String rcverCxwaletAdres;

    @Schema(example = "123.567", description = "금액")
    private BigDecimal cxTradeQty;

    @Schema(example = "123.567", description = "수수료")
    private BigDecimal cxFeeQty;

    @Schema(example = "123.567", description = "잔액")
    private BigDecimal cxBlceQty;
    
    @Schema(example = "2022-12-19 07:55:32", description = "거래 발생 일시 일시")
    private Timestamp tradeOccrrncDt;

    @Schema(example = "TRANSFER", allowableValues = {"transfer", "swap", "reward"}, description = "거래 타입")
    private String transactionType;

    @Schema(example = "SENT", allowableValues = {"sent", "received"}, description = "입출금 구분")
    private String transactionDetail;

    @Schema(example = "0x80dc4b76fadf3be523ff49727580e49dd996994f", description = "TX HASH")
    private String txhash;

    @Schema(example = "ED00", description = "블록체인 전송 진행 코드")
    private String blcProcessSttusCode;

    @Schema(example = "0000", description = "블록체인 전송 결과 코드")
    private String blcTrnsmisResultCode;


    
}
