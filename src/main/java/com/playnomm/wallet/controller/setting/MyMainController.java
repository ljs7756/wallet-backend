package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.setting.request.WalletPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.BriefMyInfoDTO;
import com.playnomm.wallet.dto.setting.response.WalletInfoResponseDTO;
import com.playnomm.wallet.service.setting.MyMainService;
import com.playnomm.wallet.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : MyMainController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "MY Home", description = "")
@RestController
@RequestMapping("/api/v1/my")
public class MyMainController {

    private MyMainService myMainSerivce;

    public MyMainController(MyMainService serivce) {
        this.myMainSerivce = serivce;
    }

    @Operation(summary = "마이 기본 정보", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BriefMyInfoDTO.class)))
    @GetMapping("/{userCmmnSn}/{userSn}")
    public ResponseEntity<Object> getMyBriefInfo(
            HttpServletRequest request,
            @Parameter(description = "사용자 공통 일련번호", in= ParameterIn.PATH) @PathVariable int userCmmnSn,
            @Parameter(description = "사용자 일련번호", in= ParameterIn.PATH) @PathVariable int userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (myMainSerivce.getBriefMyInfo(request, userCmmnSn, userSn));
    }

    @Hidden
    @Operation(summary = "내 지갑 리스트", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BriefMyInfoDTO.class)))
    @GetMapping("/wallet/{userSn}")
    public ResponseEntity<Object> getMyWallet(@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (myMainSerivce.getMyWallet(userSn));
    }

    @Operation(summary = "내 대표 지갑", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = WalletInfoResponseDTO.class)))
    @GetMapping("/wallet/represent/{userSn}")
    public ResponseEntity<Object> getMyRepresentWallet(@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (myMainSerivce.getMyRepresentWallet(userSn));
    }

    @Operation(summary = "지갑 별명 변경", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = WalletInfoResponseDTO.class)))
    @PatchMapping("/wallet/{userCxwaletSn}")
    public ResponseEntity<Object> patchWallet(@Parameter(description = "사용자 암호화폐지갑 일련번호", in= ParameterIn.PATH) @PathVariable int userCxwaletSn,
                                              @RequestBody WalletPatchRequestDTO data) {

        return ResponseEntity.ok().body (myMainSerivce.patchWallet(userCxwaletSn, data));
    }


}
