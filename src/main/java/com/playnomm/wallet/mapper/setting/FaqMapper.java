package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.CategoryPostRequestDTO;
import com.playnomm.wallet.dto.setting.request.BbsRequestDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import com.playnomm.wallet.dto.setting.response.CategoryResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.util.HashMap;
import java.util.List;


@Mapper
@Alias("FaqMapper")
public interface FaqMapper {
    List<CategoryResponseDTO> getCategoryList(HashMap<String, Object> params);
    List<BbsResponseDTO> getFaqList(HashMap<String, Object> params);
    BbsResponseDTO getDetail(int nttSn);
    int postCategory(CategoryPostRequestDTO params);
    int postFaqDetail(BbsRequestDTO params);
    int postUserQuestion(BbsRequestDTO params);

}
