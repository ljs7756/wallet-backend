package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.setting.response.BbsListResponseDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import com.playnomm.wallet.enums.BBSType;
import com.playnomm.wallet.service.setting.BbsService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * packageName :  com.playnomm.wallet.controller.setting
 * fileName : BbsController
 * author :  ljs7756
 * date : 2022-12-20
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-20                ljs7756             최초 생성
 */
@Tag(name = "공지사항 & 이벤트", description = "")
@RestController
@RequestMapping("/api/v1/bbs")
public class BbsController {
    private BbsService bbsService;

    public BbsController(BbsService service) {
        this.bbsService = service;
    }
    @Operation(summary = "전체(공지사항,이벤트) 조회", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsListResponseDTO.class)))
    @GetMapping("/{page}")
    public ResponseEntity<Object> getBbsList (
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @PathVariable(value = "page") int page) {

        return ResponseEntity.ok().body (bbsService.getBbsList(langCode, page, BBSType.EVENT.getType(), BBSType.NOTICE.getType()));
    }

    @Operation(summary = "공지사항 조회", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsListResponseDTO.class)))
    @GetMapping("/notice/{page}")
    public ResponseEntity<Object> getNotice (
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @PathVariable(value = "page") int page) {

        return ResponseEntity.ok().body (bbsService.getBbsList(langCode, page, BBSType.NOTICE.getType()));

    }

    @Operation(summary = "이벤트 조회", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsListResponseDTO.class)))
    @GetMapping("/event/{page}")
    public ResponseEntity<Object> getEvent (
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @PathVariable(value = "page") int page) {

        return ResponseEntity.ok().body (bbsService.getBbsList(langCode, page, BBSType.EVENT.getType()));
   }

    @Operation(summary = "공지사항, 이벤트 상세 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsResponseDTO.class)))
    @GetMapping("/detail/{nttSn}")
    public ResponseEntity<Object> getBbsDetail (@PathVariable int nttSn) {
        return ResponseEntity.ok().body (bbsService.getDetail(nttSn));
    }

    @Hidden
    @Operation(summary = "공지사항, 이벤트 수정", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsResponseDTO.class)))
    @PatchMapping("/{nttSn}")
    public ResponseEntity<Object> patchBbs (@PathVariable int nttSn) {

        return ResponseEntity.ok().body (bbsService.getDetail(nttSn));
    }

}
