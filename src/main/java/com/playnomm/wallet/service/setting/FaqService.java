package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.BbsRequestDTO;
import com.playnomm.wallet.dto.setting.request.CategoryPostRequestDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import com.playnomm.wallet.dto.setting.response.CategoryResponseDTO;
import com.playnomm.wallet.enums.BBSType;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.FaqMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class FaqService {

    private final int recordSize = 10;

    private FaqMapper faqMapper;

    @Value("${SYS_ID}")
    private String sysId;

    public FaqService(FaqMapper mapper) {
        this.faqMapper = mapper;
    }

    public ResultDTO getCategoryList(String langCode) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("lang", langCode);
        params.put("bbsId", BBSType.FAQ.getType());
        params.put("sysId", sysId);

        List<CategoryResponseDTO> result = faqMapper.getCategoryList(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }


    public ResultDTO getQuestionCategoryList(String langCode) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("lang", langCode);
        params.put("bbsId", BBSType.ASK.getType());
        params.put("sysId", sysId);

        List<CategoryResponseDTO> result = faqMapper.getCategoryList(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public ResultDTO getFaqList(String langCode, String category, int page) {

        HashMap<String, Object> params = new HashMap<>();
        params.put("category", category);
        params.put("lang", langCode);
        params.put("bbsId", BBSType.FAQ.getType());
        params.put("sysId", sysId);
        params.put("itemPerPage", recordSize);
        params.put("offset", ((page-1)*recordSize));

        List<BbsResponseDTO> result = faqMapper.getFaqList(params);
        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO getDetail(int nttSn) {
        BbsResponseDTO result = faqMapper.getDetail(nttSn);
        return new ResultDTO(StatusCode.ACCESS, result);
    }

    public ResultDTO postCategory(CategoryPostRequestDTO params) {
        params.setSysId(sysId);
        params.setBbsId(BBSType.FAQ.getType());

        int result = faqMapper.postCategory(params);
        if(result < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO postFaqDetail(BbsRequestDTO params) {
        params.setSysId(sysId);

        int result = faqMapper.postFaqDetail(params);
        if(result < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

    public ResultDTO postUserQuestion(String langCode, BbsRequestDTO params) {

        params.setBbsId(BBSType.ASK.getType());
        params.setSysId(sysId);
        params.setLangCode(langCode);
        params.setNttNoticeAt("N");
        params.setNttOthbcAt("N");
        params.setNttAtchmnflAt("N");
        params.setUseAt("Y");
        //P: User, M: Manager
        params.setAcntTyCode("P");

        int result = faqMapper.postUserQuestion(params);

        if(result < 1) {
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }
        return new ResultDTO(StatusCode.ACCESS, null);
    }

}
