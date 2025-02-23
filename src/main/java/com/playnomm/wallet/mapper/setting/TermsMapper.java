package com.playnomm.wallet.mapper.setting;

import com.playnomm.wallet.dto.setting.response.TermsResponseDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.List;

/**
 * packageName :  com.playnomm.wallet.mapper.setting
 * fileName : TermsMapper
 * author :  evilstorm
 * date : 2022/12/28
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022/12/28              evilstorm             최초 생성
 */
@Mapper
public interface TermsMapper {

    List<TermsResponseDTO> getTermsList(HashMap<String, Object> params);
    TermsResponseDTO getTermsDetail(HashMap<String, Object> params);

}
