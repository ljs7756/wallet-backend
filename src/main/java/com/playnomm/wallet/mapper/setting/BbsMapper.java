package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.request.BbsRequestDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.util.HashMap;
import java.util.List;


@Mapper
@Alias("BbsMapper")
public interface BbsMapper {

    int getTotalCount(HashMap<String, Object> params);
    List<BbsResponseDTO> getBbsList(HashMap<String, Object> params);
    BbsResponseDTO getDetail(int nttSn);
    int postBbs(BbsRequestDTO params);

}
