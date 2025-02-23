package com.playnomm.wallet.service.setting;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.setting.request.BbsRequestDTO;
import com.playnomm.wallet.dto.setting.response.BbsListResponseDTO;
import com.playnomm.wallet.dto.setting.response.BbsResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.setting.BbsMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class BbsService {

    private final int recordSize = 10;

    @Value("${SYS_ID}")
    private String sysId;

    private BbsMapper bbsMapper;

    public BbsService(BbsMapper mapper) {
        this.bbsMapper = mapper;
    }

    public int getTotalCount(HashMap<String, Object> params) {
        return bbsMapper.getTotalCount(params);
    }

    public ResultDTO getBbsList(String langCode, int page, String ...boardType) {

        HashMap<String, Object> params = new HashMap();
        params.put("sysId", sysId);
        params.put("langCode", langCode);
        params.put("bbsIds", boardType);
        params.put("itemPerPage", recordSize);
        params.put("offset", ((page-1)*recordSize));

        int totalCount =  getTotalCount(params);

        List<BbsResponseDTO> result = bbsMapper.getBbsList(params);
        BbsListResponseDTO brDTO = new BbsListResponseDTO(page, totalCount, recordSize, result);

        return new ResultDTO(StatusCode.ACCESS, brDTO);
    }
    public ResultDTO getDetail(int nttSn) {
        BbsResponseDTO result = bbsMapper.getDetail(nttSn);
        return new ResultDTO(StatusCode.ACCESS, result);
    }
    public int postBbs(BbsRequestDTO params) {
        return bbsMapper.postBbs(params);
    }

}
