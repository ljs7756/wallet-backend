package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.ChangeLanguageDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSAuthDTO;
import com.playnomm.wallet.dto.setting.request.ChangeSymbolDTO;
import com.playnomm.wallet.dto.setting.response.BriefMyInfoDTO;
import com.playnomm.wallet.dto.setting.response.ConfigurationDTO;
import com.playnomm.wallet.service.setting.ConfigurationService;
import com.playnomm.wallet.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : ConfigurationController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "설정", description = "")
@RestController
@RequestMapping("/api/v1/configuration")
public class ConfigurationController {
    private ConfigurationService service;

    public ConfigurationController(ConfigurationService service) {
        this.service = service;
    }

    @Operation(summary = "설정 정보 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BriefMyInfoDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getConfiguration(@Parameter(description = "사용자 ID", in= ParameterIn.PATH) @PathVariable int userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.getConfiguration(userSn));
    }

    @Operation(summary = "통화 변경", description = "symbolCode: KRW,USD")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ConfigurationDTO.class)))
    @PatchMapping("/{userSn}/symbol")
    public ResponseEntity<Object> patchSymbol(@Parameter(description = "사용자 ID", in= ParameterIn.PATH) @PathVariable int userSn,
                                              @RequestBody @Valid ChangeSymbolDTO params) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.patchSymbol(userSn, params));
    }

    @Operation(summary = "언어 변경", description = "langCode: KO,EN")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ConfigurationDTO.class)))
    @PatchMapping("/{userSn}/lang")
    public ResponseEntity<Object> patchLanguage(@Parameter(description = "사용자 ID", in= ParameterIn.PATH) @PathVariable int userSn,
                                                @RequestBody @Valid ChangeLanguageDTO params) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.patchLanguage(userSn, params));
    }
    @Operation(summary = "생체인증 사용 여부 변경", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PatchMapping("/{userSn}/sAuth")
    public ResponseEntity<Object> patchSAuth(@Parameter(description = "사용자 ID", in= ParameterIn.PATH) @PathVariable int userSn,
                                             @RequestBody @Valid ChangeSAuthDTO params) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.patchSAuth(userSn, params));
    }

}
