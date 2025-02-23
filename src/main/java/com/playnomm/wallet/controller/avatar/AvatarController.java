package com.playnomm.wallet.controller.avatar;

import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import com.playnomm.wallet.service.avatar.AvatarService;
import com.playnomm.wallet.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.avatar
 * fileName : AvatarController
 * author :  ljs7756
 * date : 2023-01-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-12                ljs7756             최초 생성
 */
@Tag(name = "아바타")
@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

    @Autowired
    AvatarService avatarService;

    @Operation(summary = "사용자 인증", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = HomeResponseDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getAuthorization (@Parameter(description = "사용자 id", in= ParameterIn.PATH) @PathVariable Integer userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (avatarService.getAuthorization(userSn));
    }
}
