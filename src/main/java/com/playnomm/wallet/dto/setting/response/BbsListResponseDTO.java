package com.playnomm.wallet.dto.setting.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * packageName :  com.playnomm.wallet.dto.setting.response
 * fileName : BbsListResponseDTO
 * author :  evilstorm
 * date : 2022/12/23
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/23              evilstorm             최초 생성
 */
@Getter
@Setter
public class BbsListResponseDTO {
    @Schema(example = "13", type = "integer", description = "총 아이템 수")
    private int totalItemCount;
    @Schema(example = "1", type = "integer", description = "현재 페이지 수")
    private int currentPage;
    @Schema(example = "10", type = "integer",  description = "페이지 당 아이템 수")
    private int itemPerPage;
    @Schema(example = "true", type = "boolean", description = "다음 페이지 존재 여부")
    private boolean hasNext = false;

    @Schema(description = "공지사항 리스트", implementation = BbsResponseDTO.class)
    private List<BbsResponseDTO> data;

    public BbsListResponseDTO(
            int currentPage, int totalItemCount, int itemPerPage, List<BbsResponseDTO> data
            ){
        this.currentPage = currentPage;
        this.totalItemCount = totalItemCount;
        this.itemPerPage = itemPerPage;
        this.data = data;

        calcHasNext(currentPage, totalItemCount, itemPerPage);
    }

    private void calcHasNext(int currentPage, int totalItemCount, int itemPerPage) {
        if((currentPage * itemPerPage) < totalItemCount) {
            hasNext = true;
        }
    }


}
