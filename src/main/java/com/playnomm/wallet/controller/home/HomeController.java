package com.playnomm.wallet.controller.home;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import com.playnomm.wallet.service.home.HomeService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.controller.home
 * fileName : HomeController
 * author :  ljs7756
 * date : 2022-12-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-12                ljs7756             최초 생성
 */
@Tag(name = "메인", description = "")
@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

    @Autowired
    HomeService homeService;

    @Operation(summary = "사용자 정보 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = HomeResponseDTO.class)))
    @GetMapping("/{userSn}")
    public ResponseEntity<Object> getUserInfo (HttpServletRequest request,
            @Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        return ResponseEntity.ok().body (homeService.getUserInfo(request, userSn));
    }

    @Operation(summary = "사용자 정보 조회-메인넷 변경", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = HomeResponseDTO.class)))
    @GetMapping("/{userSn}/{blcNtwrkId}")
    public ResponseEntity<Object> getUserInfoForMainNet (HttpServletRequest request,
                                               @Parameter(description = "사용자 id", in=ParameterIn.PATH) @PathVariable Integer userSn,
                                               @Parameter(description = "메인넷", in=ParameterIn.PATH,schema = @Schema(allowableValues = {"1000","1001","1002"})) @PathVariable String blcNtwrkId) {

        //로그인된 userSn인지 확인
        AuthenticationUtil.authUserSn(userSn);

        Map<String, Object> map = new HashMap<>();
        map.put("userSn", userSn);
        map.put("blcNtwrkId", blcNtwrkId);

        return ResponseEntity.ok().body (homeService.modifyMainNet(request,map));
    }
}
