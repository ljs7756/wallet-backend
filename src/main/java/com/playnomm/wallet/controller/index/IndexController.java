package com.playnomm.wallet.controller.index;

import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * packageName :  com.playnomm.wallet.controller.index
 * fileName : IndexController
 * author :  ljs7756
 * date : 2023-01-18
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2023-01-18                ljs7756             최초 생성
 */
@Hidden
@Tag(name = "인덱스", description = "")
@RestController
@RequestMapping("/")
public class IndexController {
    @Operation(summary = "인덱스", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation")
    @GetMapping("/")
    public ResponseEntity<Object> getUserInfo () {

        return ResponseEntity.ok().body ("Playnomm Wallet");
    }
}
