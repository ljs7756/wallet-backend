package com.playnomm.wallet.controller.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.BbsRequestDTO;
import com.playnomm.wallet.dto.setting.request.CategoryPostRequestDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import com.playnomm.wallet.dto.setting.response.CategoryResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.service.setting.FaqService;
import com.playnomm.wallet.util.BeanValidationUtil;
import io.swagger.v3.oas.annotations.Hidden;
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
 * fileName : FaqController
 * author :  ljs7756
 * date : 2022-12-19
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-19                ljs7756             최초 생성
 */
@Tag(name = "FAQ", description = "")
@RestController
@RequestMapping("/api/v1/faq")
public class FaqController {

    private FaqService faqService;

    public FaqController(FaqService faqService) {
        this.faqService = faqService;
    }

    @Operation(summary = "고객센터 전체 항목", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsResponseDTO.class)))
    @GetMapping("")
    public ResponseEntity<Object> getAllFaqList(
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode) {

        return ResponseEntity.ok().body (faqService.getFaqList(langCode, "all", -1));
    }

    @Operation(summary = "고객센터 카테고리 리스트", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class)))
    @GetMapping("/categoryList")
    public ResponseEntity<Object> getCategoryList(
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode) {

        return ResponseEntity.ok().body (faqService.getCategoryList(langCode));
    }
    @Operation(summary = "고객센터 카테고리 별 검색", description = "카테고리 리스트에 bbsNttClSn 값 (/categoryList) *Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsResponseDTO.class)))
    @GetMapping("/category/{bbsNttClSn}/{page}")
    public ResponseEntity<Object> getFaqList(
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
            @Parameter(description = "게시판 카테고리 번호", in= ParameterIn.PATH) @PathVariable String bbsNttClSn,
            @Parameter(description = "페이지 번호", in= ParameterIn.PATH) @PathVariable int page

    ) {

        return ResponseEntity.ok().body (faqService.getFaqList(langCode, bbsNttClSn, page));
    }
    @Operation(summary = "FAQ 상세 조회", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = BbsResponseDTO.class)))
    @GetMapping("/{nttSn}")
    public ResponseEntity<Object> getFaqDetail (@Parameter(description = "FAQ 고유번호", in= ParameterIn.PATH) @PathVariable int nttSn) {
        return ResponseEntity.ok().body (faqService.getDetail(nttSn));
    }

    @PostMapping("/add/category")
    @Hidden
    public ResponseEntity<Object> postCategory(@RequestBody CategoryPostRequestDTO params, Errors errors) {
        if (errors.hasErrors ()) {
            return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (faqService.postCategory(params));
    }

    @PostMapping("/add/detail")
    @Hidden
    public ResponseEntity<Object> postFaqDetail(@RequestBody BbsRequestDTO params, Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (faqService.postFaqDetail(params));
    }



    @Operation(summary = "문의하기 카테고리 리스트", description = "Swagger에서는 langCode 적용되지 않아 기본 KO로 동작합니다.")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class)))
    @GetMapping("/categoryList/ask")
    public ResponseEntity<Object> getQuestionCategoryList(
            @CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode) {

        return ResponseEntity.ok().body (faqService.getQuestionCategoryList(langCode));
    }

    @Operation(summary = "문의글 저장하기", description = "")
    @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schema = @Schema(implementation = ResultDTO.class)))
    @PostMapping("")
    public ResponseEntity<Object> postUserQuestion(@CookieValue(value = "langCode", defaultValue = "KO", required = false) String langCode,
                                                   @RequestBody @Valid BbsRequestDTO params, Errors errors) {
        if(errors.hasErrors()) {
            return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
        }

        return ResponseEntity.ok().body (faqService.postUserQuestion(langCode, params));
    }
}
