package com.playnomm.wallet.controller.token;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.token.request.SaveAdditionRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveDirectTokenRequestDTO;
import com.playnomm.wallet.dto.token.request.SaveOrderRequestDTO;
import com.playnomm.wallet.dto.token.response.TokenAdditionResponseDTO;
import com.playnomm.wallet.dto.token.response.TokenInfoResponseDTO;
import com.playnomm.wallet.service.token.TokenService;
import com.playnomm.wallet.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.token
 * fileName : TokenController
 * author :  ljs7756
 * date : 2022-12-15
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-15                ljs7756             최초 생성
 */
@Tag(name = "토큰", description = "")
@RestController
@RequestMapping("/api/v1/token")
public class TokenController {
    @Autowired
    TokenService tokenService;

    @Value("${image.domain}")
    String imageDomain;

    @Value("${image.icon.default.path}")
    String defaultIconImg;



    @Operation(summary = "사용자 토큰 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TokenInfoResponseDTO.class)))
    @GetMapping("/{userSn}/{blcNtwrkId}")
    public ResponseEntity<Object> getTokenList (@Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn,
                                                @Parameter(description = "메인넷", in=ParameterIn.PATH,schema = @Schema(allowableValues = {"1000","1001","1002"})) @PathVariable String blcNtwrkId) {

        //로그인한 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        Map<String, Object> map = new HashMap<>();
        map.put("userSn", userSn);
        map.put("blcNtwrkId", blcNtwrkId);
        map.put("imageDomain", imageDomain);
        map.put("defaultIconImg", defaultIconImg);

        return ResponseEntity.ok().body (tokenService.getTokenList(map));
    }

    @Operation(summary = "토큰 추가 리스트 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TokenAdditionResponseDTO.class)))
    @GetMapping("/all/{userSn}/{blcNtwrkId}")
    public ResponseEntity<Object> getTokenAllList (@Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn,
                                                @Parameter(description = "메인넷", in=ParameterIn.PATH,schema = @Schema(allowableValues = {"1000","1001","1002"})) @PathVariable String blcNtwrkId) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        Map<String, Object> map = new HashMap<>();
        map.put("userSn", userSn);
        map.put("blcNtwrkId", blcNtwrkId);
        map.put("imageDomain", imageDomain);
        map.put("defaultIconImg", defaultIconImg);

        String lmContract = "LM";
        if(blcNtwrkId.equals("1001")){
            lmContract = "0x80dc4b76fadF3BE523FF49727580e49Dd996994F";
        }else if(blcNtwrkId.equals("1002")){
            lmContract = "BNBLM";
        }
        map.put("lmContract", lmContract);

        return ResponseEntity.ok().body (tokenService.getTokenAllList(map));
    }

    @Operation(summary = "토큰 추가 저장", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PutMapping("/tokens")
    public ResponseEntity<Object> saveTokenList (@RequestBody List<SaveAdditionRequestDTO> saveAdditionRequestDTOList) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(saveAdditionRequestDTOList.get(0).getUserSn());

        return ResponseEntity.ok().body (tokenService.saveTokenList(saveAdditionRequestDTOList));
    }

    @Operation(summary = "직접 토큰 추가 저장", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PutMapping("/direct-token")
    public ResponseEntity<Object> saveDirectToken (@RequestBody SaveDirectTokenRequestDTO saveDirectTokenRequestDTO) {
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(saveDirectTokenRequestDTO.getUserSn());

        return ResponseEntity.ok().body (tokenService.saveDirectToken(saveDirectTokenRequestDTO));
    }

    @Operation(summary = "토큰 순서 저장", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PutMapping("/order")
    public ResponseEntity<Object> saveTokensOrder (@RequestBody List<SaveOrderRequestDTO> saveOrderRequestDTOList) {

        return ResponseEntity.ok().body (tokenService.saveTokensOrder(saveOrderRequestDTOList));
    }
}
