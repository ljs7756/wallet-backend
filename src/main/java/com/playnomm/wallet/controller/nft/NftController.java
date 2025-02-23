package com.playnomm.wallet.controller.nft;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.nft.request.SaveNftOrderRequestDTO;
import com.playnomm.wallet.dto.nft.response.NftInfoResponseDTO;
import com.playnomm.wallet.dto.token.request.SaveOrderRequestDTO;
import com.playnomm.wallet.dto.token.response.TokenInfoResponseDTO;
import com.playnomm.wallet.dto.transaction.response.TransactionHistoryResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.nft.NftService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * packageName :  com.playnomm.wallet.controller.nft
 * fileName : NftController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "NFT", description = "")
@RestController
@RequestMapping("/api/v1/nft")
public class NftController {

    @Autowired
    NftService nftService;

//    @Operation(summary = "사용자 NFT 조회", description = "")
//    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = NftInfoResponseDTO.class)))
//    @GetMapping("/{userSn}")
//    public ResponseEntity<Object> getNftList (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn) {
//
//        return ResponseEntity.ok().body (nftService.getNftList(userSn));
//    }

    @Operation(summary = "NFT 순서 저장", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PutMapping("/order")
    public ResponseEntity<Object> saveNftOrder (@RequestBody List<SaveNftOrderRequestDTO> saveNftOrderRequestDTOList) {

        //로그인한 userSn인지 확인
        AuthenticationUtil.authUserSn(saveNftOrderRequestDTOList.get(0).getUserSn());

        return ResponseEntity.ok().body (nftService.saveNftOrder(saveNftOrderRequestDTOList));
    }
}
