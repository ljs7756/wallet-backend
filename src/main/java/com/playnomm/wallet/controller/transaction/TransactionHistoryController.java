package com.playnomm.wallet.controller.transaction;

import com.playnomm.wallet.dto.transaction.response.NftTransactionHistoryResponseDTO;
import com.playnomm.wallet.dto.transaction.response.TransactionHistoryResponseDTO;
import com.playnomm.wallet.enums.TransactionType;
import com.playnomm.wallet.service.transaction.TransactionHistoryService;
import com.playnomm.wallet.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.transaction
 * fileName : TransactionHistoryController
 * author :  ljs7756
 * date : 2022-12-16
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-16                ljs7756             최초 생성
 */
@Tag(name = "거래내역", description = "")
@RestController
@RequestMapping("/api/v1/transaction/history")
public class TransactionHistoryController {

    @Autowired
    TransactionHistoryService transactionHistoryService;

    @Operation(summary = "사용자 토큰 거래내역 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TransactionHistoryResponseDTO.class)))
    @GetMapping("/token/{userSn}/{blcNtwrkId}/{userCxwaletTknSn}/{tradeTyCode}/{startDt}/{endDt}")
    public ResponseEntity<Object> getTokenTransactionHistoryList (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn,
                                                             @Parameter(description = "메인넷", in=ParameterIn.PATH,schema = @Schema(allowableValues = {"1000","1001","1002"})) @PathVariable String blcNtwrkId,
                                                             @Parameter(description = "사용자 토큰 일련 번호", in=ParameterIn.PATH ) @PathVariable Integer userCxwaletTknSn,
                                                             @Parameter(description = "거래 코드", in= ParameterIn.PATH,schema = @Schema(allowableValues = {"TRANSFER","SWAP","REWARD"})) @PathVariable String tradeTyCode,
                                                             @Parameter(description = "검색 기간 시작 ", in= ParameterIn.PATH, example = "20221219") @PathVariable String startDt,
                                                             @Parameter(description = "검색 기간 끝", in= ParameterIn.PATH, example = "20221219") @PathVariable String endDt) {

        //로그인한 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        String netWork = "";
        switch (blcNtwrkId){
            case "1000": netWork = "LMC";
                break;
            case "1001":  netWork = "ETH";
                break;
            case "1002": netWork = "BNB";
                break;
            default: netWork = "LMC";
                break;
        }


        String[] arrDetailCode = TransactionType.valueOf(netWork+"_"+tradeTyCode).getTradeTypeCode().split(",");

        Map<String, Object> map = new HashMap<>();
        map.put("userSn", userSn);
        map.put("blcNtwrkId", blcNtwrkId);
        map.put("userCxwaletTknSn", userCxwaletTknSn);
        map.put("tradeTyDetailCode", arrDetailCode);
        map.put("startDt", startDt);
        map.put("endDt", endDt);

        return ResponseEntity.ok().body (transactionHistoryService.getTokenTransactionHistoryList(map));
    }

    @Operation(summary = "토큰 거래 상세 내역 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TransactionHistoryResponseDTO.class)))
    @GetMapping("/token/{cxTradeSn}")
    public ResponseEntity<Object> selectTokenTransactionHistoryDetail (@Parameter(description = "거래내역 일련번호", in= ParameterIn.PATH) @PathVariable Integer cxTradeSn) {

        return ResponseEntity.ok().body (transactionHistoryService.selectTokenTransactionHistoryDetail(cxTradeSn));
    }

    @Operation(summary = "사용자 NFT 거래내역 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = NftTransactionHistoryResponseDTO.class)))
    @GetMapping("/nft/{userSn}/{nftitmSn}/{tradeTyCode}/{startDt}/{endDt}")
    public ResponseEntity<Object> getNftTransactionHistoryList (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn,
                                                                @Parameter(description = "NFT 아이템 일련번호", in= ParameterIn.PATH) @PathVariable Integer nftitmSn,
                                                                @Parameter(description = "거래 유형 코드", in= ParameterIn.PATH,schema = @Schema(allowableValues = {"TRADE","TRANSFER","MINTING"})) @PathVariable String tradeTyCode,
                                                                @Parameter(description = "검색 기간 시작 ", in= ParameterIn.PATH, example = "20221219") @PathVariable String startDt,
                                                                @Parameter(description = "검색 기간 끝", in= ParameterIn.PATH, example = "20221219") @PathVariable String endDt) {

        //로그인한 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);


        String[] arrDetailCode = TransactionType.valueOf("NFT_"+tradeTyCode).getTradeTypeCode().split(",");

        Map<String, Object> map = new HashMap<>();
        map.put("userSn", userSn);
        map.put("nftitmSn", nftitmSn);
        map.put("tradeTyDetailCode", arrDetailCode);
        map.put("tradeNm", tradeTyCode);
        map.put("startDt", startDt);
        map.put("endDt", endDt);

        return ResponseEntity.ok().body (transactionHistoryService.getNftTransactionHistoryList(map));
    }

    @Operation(summary = "NFT 거래 상세 내역 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = NftTransactionHistoryResponseDTO.class)))
    @GetMapping("/nft/{cxTradeSn}")
    public ResponseEntity<Object> getNftTransactionHistoryListDetail (@Parameter(description = "거래내역 일련번호", in= ParameterIn.PATH) @PathVariable Integer cxTradeSn) {

        return ResponseEntity.ok().body (transactionHistoryService.getNftTransactionHistoryListDetail(cxTradeSn));
    }
}
