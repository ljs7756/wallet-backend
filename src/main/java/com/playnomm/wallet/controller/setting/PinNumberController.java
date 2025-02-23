package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.PinNumberPatchRequestDTO;
import com.playnomm.wallet.dto.setting.response.PinNumberCheckResponseDTO;
import com.playnomm.wallet.dto.setting.response.PinNumberEnableStateResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.setting.PinNumberService;
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

import javax.validation.Valid;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : PinNumberController
 * author :  evilstorm
 * date : 2023/01/10
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023/01/10              evilstorm             최초 생성
 */
@Tag(name = "Pin 번호 관련", description = "")
@RestController
@RequestMapping("/api/v1/pin")
public class PinNumberController {
    private PinNumberService service;
    public PinNumberController(PinNumberService service) {
        this.service = service;
    }

    @Operation(summary = "핀번호 설정 여부", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = PinNumberEnableStateResponseDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getPinNumberEnableState (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable int userSn){
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);
        return ResponseEntity.ok().body (service.getPinNumberEnableState(userSn));
    }

    @Operation(summary = "핀번호 확인", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = PinNumberCheckResponseDTO.class)))
    @PostMapping("/check")
    public ResponseEntity<Object> checkPinNumber (@RequestBody @Valid PinNumberPatchRequestDTO body, Errors errors){
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(body.getUserSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.checkPinNumber(body));
    }
    @Operation(summary = "핀번호 설정", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("")
    public ResponseEntity<Object> postPinNumber (@RequestBody @Valid PinNumberPatchRequestDTO body, Errors errors){
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(body.getUserSn());

        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.postPinNumber(body));
    }


    @Operation(summary = "핀번호 변경", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PatchMapping("")
    public ResponseEntity<Object> modifyPinNumber (@RequestBody @Valid PinNumberPatchRequestDTO body, Errors errors){
        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(body.getUserSn());
        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<>(StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (service.modifyPinNumber(body));
    }


}
