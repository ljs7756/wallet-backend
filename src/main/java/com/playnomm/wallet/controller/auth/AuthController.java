package com.playnomm.wallet.controller.auth;

import com.playnomm.wallet.config.security.CustomUserDetails;
import com.playnomm.wallet.config.security.JwtProvider;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.auth.request.InsertUserRequestDTO;
import com.playnomm.wallet.dto.auth.request.LogOutRequestDTO;
import com.playnomm.wallet.dto.auth.request.RefreshTokenRequestDTO;
import com.playnomm.wallet.dto.auth.response.AuthResponseDTO;
import com.playnomm.wallet.dto.auth.response.InsertUserResponseDTO;
import com.playnomm.wallet.dto.auth.response.PrivateKeyResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.ManageKeyService;
import com.playnomm.wallet.service.auth.AuthService;
import com.playnomm.wallet.util.AuthenticationUtil;
import com.playnomm.wallet.util.PlaynommUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.auth
 * fileName : AuthController
 * author :  ljs77
 * date : 2022-11-28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-11-28                ljs77             최초 생성
 */
@Tag(name = "인증", description = "")
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    @Autowired
    AuthService authService;
    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    ManageKeyService manageKeyService;

    @Operation(summary = "토큰 발급", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @GetMapping("/login/{userSn}/{loginUuid}")
    public ResponseEntity<Object> login (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn,
                                         @Parameter(description = "브라우저 식별 uuid", in= ParameterIn.PATH) @PathVariable String loginUuid) {

        return ResponseEntity.ok().body (jwtProvider.generateJwtToken(userSn, "S", loginUuid));
    }

    @Operation(summary = "토큰 재발급", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
    @PostMapping("/login/refresh")
    public ResponseEntity<Object> loginRefresh (@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {

        return ResponseEntity.ok().body (authService.updateAccessToken(refreshTokenRequestDTO));
    }

    @Operation(summary = "회원가입", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = InsertUserResponseDTO.class)))
    @PostMapping("/login/join")
    public ResponseEntity<Object> insertUser (@RequestBody InsertUserRequestDTO insertUserRequestDTO) {

            ResultDTO resultDTO = authService.InsertUser(insertUserRequestDTO);

            //회원 생성이 완료되면 통합회원 api 전송
            if(resultDTO.getCode() == 200){

                //회원 정보 조회
                String pnAuthorization = PlaynommUtil.getPnAuthorization(Long.valueOf(insertUserRequestDTO.getUserCmmnSn()));

                String accessToken = jwtProvider.generateAccessToken(((InsertUserResponseDTO) resultDTO.getData()).getUserSn());

                //api 전송 시작
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                httpHeaders.set("PN-Authorization", pnAuthorization);
                httpHeaders.set("Authorization", "Bearer " + accessToken);

                //api 전송
                Map<String, Object> apiResult = authService.sendUserSn(httpHeaders, ((InsertUserResponseDTO) resultDTO.getData()).getUserSbscrbDt(), insertUserRequestDTO);
                if(!apiResult.get("code").equals(200)){
                    return ResponseEntity.ok().body (new ResultDTO<>(Integer.parseInt(apiResult.get("code").toString()), apiResult.get("msg").toString()));
                }

                //private key 생성
                ResultDTO privateKeyResultDTO = authService.getPrivateKey(((InsertUserResponseDTO) resultDTO.getData()).getUserSn());

                if(privateKeyResultDTO.getCode()==200){
                    // private key를 담아 전송
                    InsertUserResponseDTO insertUserResponseDTO = (InsertUserResponseDTO)resultDTO.getData();
                    insertUserResponseDTO.setPrivateKey(((PrivateKeyResponseDTO)privateKeyResultDTO.getData()).getPrivateKey());
                    resultDTO.setData(insertUserResponseDTO);

                }
            }

        return ResponseEntity.ok().body (resultDTO);
    }

    @Operation(summary = "로그아웃", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/logout")
    public ResponseEntity<Object> logout (@RequestBody LogOutRequestDTO logOutRequestDTO) {

        return ResponseEntity.ok().body (authService.logout(logOutRequestDTO));
    }

    @Operation(summary = "프라이빗 키 생성", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @GetMapping("/private-key/{userSn}")
    public ResponseEntity<Object> getPrivateKey (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn) {
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (authService.getPrivateKey(userSn));
    }

    @Operation(summary = "로그인 중복 확인", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @GetMapping("/login/duplication/{userSn}/{loginUuid}")
    public ResponseEntity<Object> checkDuplicate (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn,
                                                  @Parameter(description = "브라우저 식별 uuid", in= ParameterIn.PATH) @PathVariable String loginUuid) {

        return ResponseEntity.ok().body (authService.checkDuplicate(userSn, loginUuid));
    }

    @Operation(summary = "kcy 인증 여부 확인", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @GetMapping("/login/kyc/{userSn}")
    public ResponseEntity<Object> checKyc (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn) {

        return ResponseEntity.ok().body (authService.checKyc(userSn));
    }

//    @Operation(summary = "private key migration", description = "")
//    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
//    @GetMapping("/login/migration")
//    public ResponseEntity<Object> migration () {
//
//        return ResponseEntity.ok().body (manageKeyService.migrationPrivateKey());
//    }
//
//    @Operation(summary = "decode key", description = "")
//    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
//    @PostMapping("/login/decodekey/{key}")
//    public ResponseEntity<Object> migrationPrK (@RequestBody String key) {
//
//        return ResponseEntity.ok().body (manageKeyService.decodeKey(key));
//    }
//
//    @Operation(summary = "incode key", description = "")
//    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = AuthResponseDTO.class)))
//    @PostMapping("/login/incodeKey/{key}")
//    public ResponseEntity<Object> incodeKeyPrK (@RequestBody String key) {
//
//        return ResponseEntity.ok().body (manageKeyService.incodeKey(key));
//    }


}
