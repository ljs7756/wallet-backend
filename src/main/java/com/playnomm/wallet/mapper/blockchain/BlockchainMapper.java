package com.playnomm.wallet.mapper.blockchain;

import com.playnomm.wallet.dto.blockchain.response.GasTrackerDTO;
import com.playnomm.wallet.dto.blockchain.response.LmcNftDTO;
import com.playnomm.wallet.dto.blockchain.response.LmcNftsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.type.Alias;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author : hzn
 * @date : 2022/12/13
 * @description :
 */
@Mapper
@Alias("BlockchainMapper")
public interface BlockchainMapper {

	/**
	 * 사용자 암호화폐지갑 정보
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectUserCxwaletInfo (Map<String, Object> param);

	/**
	 * 사용자 암호화폐지갑 토큰 정보
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectUserCxwaletTknInfo (Map<String, Object> param);

	/**
	 * Tx Hash Log 등록
	 *
	 * @param param
	 */
	void insertTxHashLog (Map<String, Object> param);

	/**
	 * Trade Info Log 등록
	 *
	 * @param param
	 */
	void insertTradeInfoLog (Map<String, Object> param);

	/**
	 * 이번 주 보상 차수 조회
	 *
	 * @return
	 */
	Optional<Map<String, Object>> selectThisWeekRwardOdrInfo ();

	/**
	 * 블록체인 (암호화폐) 토큰 기본정보
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectTccoBlcTknBass (Map<String, Object> param);

	/**
	 * 토큰 정의 정보 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectTccoTknDefInfo (Map<String, Object> param);

	/**
	 * 토큰 FT 정보 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectTccoTknFtInfo (Map<String, Object> param);

	/**
	 * 토큰 NFT 정보 조회
	 *
	 * @param tknNftSn
	 * @return
	 */
	Optional<Map<String, Object>> selectTccoTknNftInfo (Integer tknNftSn);

	/**
	 * NFT 정보 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<LmcNftDTO> selectTncnNftitmInfo (Map<String, Object> param);

	/**
	 * NFT 목록 조회
	 *
	 * @param param
	 * @return
	 */
	List<LmcNftsDTO> selectTncnNftitmList (Map<String, Object> param);

	/**
	 * NFT 정보 수정
	 *
	 * @param param
	 */
	void updateTncnNftitmInfo (Map<String, Object> param);

	/**
	 * 사용자 암호화폐지갑 토큰 정보 목록
	 *
	 * @param userCmmnSn
	 * @return
	 */
	List<Map<String, Object>> selectUserCxwaletTknInfoList (Integer userCmmnSn);

	/**
	 * Gas Tracker 정보 조회
	 *
	 * @param networkId
	 * @return
	 */
	Optional<GasTrackerDTO> selectBlcNtwrkGasInfo (String networkId);

	/**
	 * ETH / BNB NFT 거래 내역 조회
	 *
	 * @param param
	 * @return
	 */
	Optional<Map<String, Object>> selectErcNftTradeInfo (Map<String, Object> param);
}
