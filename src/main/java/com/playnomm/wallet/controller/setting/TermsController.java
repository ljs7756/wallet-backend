package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.setting.response.TermsResponseDTO;
import com.playnomm.wallet.service.setting.TermsService;
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

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : TermsController
 * author :  ljs7756
 * date : 2022-12-22
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-22                ljs7756             최초 생성
 */
@Tag(name = "약관", description = "")
@RestController
@RequestMapping("/api/v1/terms")
public class TermsController {

    private TermsService service;

    public TermsController(TermsService service) {
        this.service = service;
    }

    @Operation(summary = "약관 조회", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TermsResponseDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getTermsList (
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (service.getTermsList(langCode, userSn));
    }

    @Hidden
    @Operation(summary = "약관 내용 조회", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = TermsResponseDTO.class)))
    @GetMapping("/detail/{stplatId}")
    public ResponseEntity<Object> getTermsDetail (
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @Parameter(description = "약관 아이디", in= ParameterIn.PATH) @PathVariable String stplatId) {

        return ResponseEntity.ok().body (service.getTermsDetail(langCode, stplatId));
    }

}
