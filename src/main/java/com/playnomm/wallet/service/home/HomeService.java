package com.playnomm.wallet.service.home;

import com.playnomm.wallet.config.security.JwtProvider;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.home.response.HomeResponseDTO;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.mapper.home.HomeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * packageName :  com.playnomm.wallet.service
 * fileName : HomeService
 * author :  ljs7756
 * date : 2022-12-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-12                ljs7756             최초 생성
 */
@Service
public class HomeService {

    HomeResponseDTO homeResponseDTO;
    @Autowired
    HomeMapper homeMapper;
    @Autowired
    JwtProvider jwtProvider;

    @Value("${playnomm.auth.api.url}")
    String authApiUrl;

    @Value("${root.path}")
    String rootPath;


    public ResultDTO getUserInfo(HttpServletRequest request, Integer userSn)
    {


        try {

            homeResponseDTO = homeMapper.selectUserInfo(userSn);

            //header jwt 추출
            String authJwtToken = jwtProvider.extractToken(request);
            
            //kyc 인증 여부 api 호출
            Map<String, Object> kycParams = jwtProvider.getKycAt(homeResponseDTO.getUserCmmnSn(), authJwtToken);

            homeResponseDTO.setKycCrtfcAt(String.valueOf(kycParams.get("kycAt")));

            //아바타 이미지 하드코딩
            if(homeResponseDTO.getAvataProflUrl() == null){
                homeResponseDTO.setAvataProflUrl(rootPath+"/images/sample/face.svg");
            }



        }catch (Exception e){
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }

        return new ResultDTO(StatusCode.ACCESS,homeResponseDTO);
    }

    public ResultDTO modifyMainNet(HttpServletRequest request, Map<String, Object> params)
    {
        int resultCnt = homeMapper.modifyBaseNetwork(params);
        if(resultCnt > 0) homeResponseDTO = homeMapper.selectUserInfo((Integer)params.get("userSn"));

         //api 전송 시작
        try {

            //header jwt 추출
            String authJwtToken = jwtProvider.extractToken(request);

            //kyc 인증여부 api 호출
            Map<String, Object> kycParams = jwtProvider.getKycAt(homeResponseDTO.getUserCmmnSn(), authJwtToken);

            homeResponseDTO.setKycCrtfcAt(String.valueOf(kycParams.get("kycAt")));

            //아바타 이미지 하드코딩
            if(homeResponseDTO.getAvataProflUrl() == null){
                homeResponseDTO.setAvataProflUrl(rootPath+"/images/sample/face.svg");
            }


        }catch (Exception e){
            return new ResultDTO(StatusCode.INTERNAL_SERVER_ERROR);
        }


        ResultDTO resultDTO = new ResultDTO(StatusCode.ACCESS,homeResponseDTO);
        return resultDTO;
    }

}
