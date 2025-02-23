package com.playnomm.wallet.mapper.common;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : hzn
 * @date : 2022/12/30
 * @description :
 */
@Mapper
public interface CommonMapper {
	/**
	 * 네트워크 정보 조회
	 *
	 * @param networkId
	 * @return
	 */
	Optional<Map<String, Object>> selectBlcNtwkBass (String networkId);

	/**
	 * 네트워크 목록 조회
	 *
	 * @return
	 */
	List<Map<String, Object>> selectBlcNtwkBassList ();

	/**
	 * 공통 코드 정보 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectStdCodeInfo (Map<String, Object> param);

	/**
	 * 사용자 정보 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectUserInfo (Map<String, Object> param);

	/**
	 * 관리자 정보 조회
	 *
	 * @param blcMngrId
	 * @return
	 */
	Optional<Map<String, Object>> selectManagerInfo (String blcMngrId);

	/**
	 * 토큰 심볼 목록
	 *
	 * @return
	 */
	List<String> selectSymbolList ();

	/**
	 * 코인마켓캡 데이터 등록
	 */
	void insertCoinmkcapSts (Map<String, Object> param);

	/**
	 * 코인마켓캡 데이터 수정
	 */
	void updateCoinmkcapSts (Map<String, Object> param);

	/**
	 * 환율 정보 등록
	 *
	 * @param param
	 */
	void insertExrBass (Map<String, Object> param);

	/**
	 * 환율 정보 수정
	 */
	void updateExrBass (Map<String, Object> param);

	/**
	 * 환율 정보 조회
	 *
	 * @param symbol
	 * @return
	 */
	Optional<Map<String, Object>> selectExrBass (String symbol);

	/**
	 * 기본 게이트웨이 조회
	 *
	 * @param gtwySeCode
	 * @return
	 */
	Optional<String> selectGtwyBass (String gtwySeCode);
}
