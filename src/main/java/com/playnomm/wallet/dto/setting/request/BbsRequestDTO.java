package com.playnomm.wallet.dto.setting.request;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * packageName :  com.playnomm.wallet.dto.setting.request
 * fileName : FaqRequestDTO
 * author :  ljs7756
 * date : 2022-12-20
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-20                ljs7756             최초 생성
 */
@Getter
@Setter
public class BbsRequestDTO {
    @NotEmpty(message = "필수 파라메터가 없습니다.(bbsNttClSn)")
    @Schema(example = "2202", description = "질문하기 카테고리 일련번호")
    private String bbsNttClSn;
    @NotEmpty(message = "필수 파라메터가 없습니다.(nttCn)")
    @Schema(example = "문의 내용입니다.", description = "내용")
    private String nttCn;
    @NotEmpty(message = "필수 파라메터가 없습니다.(nttUserEmail)")
    @Schema(example = "kimmc@playnomm.com", description = "등록하는 사용자 이메일")
    private String nttUserEmail;
    @Schema(example = "kimmc", description = "등록하는 사용자 ID")
    private String nttUserId;
    @Schema(example = "KO", description = "언어")
    private String langCode;


    @Hidden
    @Schema(example = "문의합니다", description = "제목")
    private String nttSj;
    @Hidden
    @Schema(example = "01CXWL-FAQ", description = "게시판 ID")
    private String bbsId;
    @Hidden
    @Schema(example = "CXWL", description = "시스템 ID")
    private String sysId;
    @Hidden
    @Schema(example = "Y", description = "공지 여부")
    private String nttNoticeAt;
    @Hidden
    @Schema(example = "Y", description = "첨부파일 추가 여부")
    private String nttAtchmnflAt;
    @Hidden
    @Schema(example = "Y", description = "사용 여부")
    private String useAt;
    @Hidden
    @Schema(example = "Y", description = "게시물 공계 여부")
    private String nttOthbcAt;
    @Hidden
    @Schema(example = "1", description = "정렬 순서")
    private int nttSortOrdr;
    @Hidden
    @Schema(example = "P", allowableValues = {"P", "A"}, description = "계정 유형 코드 P: 사용자, M: 관리자")
    private String acntTyCode;



}
