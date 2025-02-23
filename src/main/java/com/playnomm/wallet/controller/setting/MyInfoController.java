package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.CertificationEmailPwdRequestDTO;
import com.playnomm.wallet.dto.setting.request.CertificationRequestDTO;
import com.playnomm.wallet.dto.setting.request.ModifyPasswordRequestDTO;
import com.playnomm.wallet.dto.setting.request.VerificationCodeRequestDTO;
import com.playnomm.wallet.dto.setting.response.MyInfoResponseDTO;
import com.playnomm.wallet.dto.setting.response.WithdrawResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.setting.MyInfoService;
import com.playnomm.wallet.util.AuthenticationUtil;
import com.playnomm.wallet.util.BeanValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : MyInfoController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "내정보관리", description = "")
@RestController
@RequestMapping("/api/v1/my-info")
public class MyInfoController {

    private MyInfoService service;
    public MyInfoController(MyInfoService service) {
        this.service = service;
    }

    @Operation(summary = "내 정보 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = MyInfoResponseDTO.class)))
    @GetMapping("/{userCmmnSn}/{userSn}")
    public ResponseEntity<Object> getMyInfo (
            HttpServletRequest request,
            @Parameter(description = "사용자 공통 일련번호", in= ParameterIn.PATH) @PathVariable int userCmmnSn,
            @Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn){

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.getMyInfo(request, userCmmnSn, userSn));
    }

    @Operation(summary = "아이디(이메일)와 비밀번호 조합으로 사용자 인증", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/certification/pwd")
    public ResponseEntity<Object> certificationWithPwd (HttpServletRequest request, @RequestBody @Valid CertificationEmailPwdRequestDTO body, Errors errors){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(body.getUserCmmnSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.certificationWithPwd(request, body));
    }

    @Operation(summary = "아이디(이메일)에 인증 코드 전송", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/get/certification/code/email")
    public ResponseEntity<Object> certificationEmailSendCode (HttpServletRequest request, @RequestBody @Valid CertificationRequestDTO body, Errors errors){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(body.getUserCmmnSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.sendVerificationCodeWithEmail(request, body));
    }

    @Operation(summary = "아이디(이메일)와 비밀번호로 사용자 인증 후 인증 코드 전송", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/get/certification/code/pwd")
    public ResponseEntity<Object> certificationWithPwdAndSendCode (HttpServletRequest request, @RequestBody @Valid CertificationEmailPwdRequestDTO body, Errors errors){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(body.getUserCmmnSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.sendVerificationCodeWithPwd(request, body));
    }

    @Operation(summary = "비밀번호 변경", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/modify/pwd")
    public ResponseEntity<Object> modifyPassword (HttpServletRequest request, @RequestBody @Valid ModifyPasswordRequestDTO body, Errors errors){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(body.getUserCmmnSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.modifyPassword(request, body));
    }


    @Operation(summary = "인증 코드 확인", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("/certification/code")
    public ResponseEntity<Object> certificationCode (HttpServletRequest request, @RequestBody @Valid VerificationCodeRequestDTO body, Errors errors){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(body.getUserCmmnSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.certificationCode(request, body));
    }



    @Operation(summary = "회원 탈퇴", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @DeleteMapping("/{userCmmnSn}")
    public ResponseEntity<Object> withdrawMember (HttpServletRequest request, @Parameter(description = "사용자 공통 일련번호", in= ParameterIn.PATH) @PathVariable int userCmmnSn){
        //로그인된 userCmmnSn인지 확인
        AuthenticationUtil.authUserCmmnSn(userCmmnSn);

        return ResponseEntity.ok().body (service.withdrawMember(request, userCmmnSn));
    }



}
