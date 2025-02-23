package com.playnomm.wallet.service.blockchain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.blockchain.BlockchainDTO;
import com.playnomm.wallet.dto.blockchain.ErcDTO;
import com.playnomm.wallet.dto.blockchain.KeyPairDTO;
import com.playnomm.wallet.dto.blockchain.LmDTO;
import com.playnomm.wallet.dto.blockchain.response.BalanceDTO;
import com.playnomm.wallet.dto.blockchain.response.LmcNftDTO;
import com.playnomm.wallet.enums.ApiType;
import com.playnomm.wallet.enums.EthereumApiType;
import com.playnomm.wallet.enums.LeisureMetaApiType;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import com.playnomm.wallet.mapper.blockchain.BlockchainMapper;
import com.playnomm.wallet.mapper.common.CommonMapper;
import com.playnomm.wallet.service.AwsService;
import com.playnomm.wallet.service.ManageKeyService;
import com.playnomm.wallet.util.CommonUtil;
import com.playnomm.wallet.util.ConvertUtil;
import com.playnomm.wallet.util.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hzn
 * @date : 2022/12/19
 * @description :
 */
@Slf4j
public abstract class AbstractBlockchainService {
	protected final        AwsService       awsService;
	protected final        BlockchainMapper blockchainMapper;
	protected final        CommonMapper     commonMapper;
	protected final        String           apiKey;
	protected final        SecureRandom     seed;
	protected final        ObjectMapper     objectMapper              = new ObjectMapper ();
	protected final        PnAuthService    pnAuthService;
	protected final        ManageKeyService manageKeyService;
	protected static final String           LMC_NFT_SMRT_CNTRCT_ADRES = "LMC_NFT";
	protected static final String           ETH_NFT_SMRT_CNTRCT_ADRES = "ETH_NFT";
	protected static final String           BNB_NFT_SMRT_CNTRCT_ADRES = "BNB_NFT";

	protected AbstractBlockchainService (AwsService awsService, BlockchainMapper blockchainMapper, CommonMapper commonMapper, String apiKey, PnAuthService pnAuthService, ManageKeyService manageKeyService) {
		this.awsService = awsService;
		this.blockchainMapper = blockchainMapper;
		this.commonMapper = commonMapper;
		this.apiKey = apiKey;
		this.seed = new SecureRandom ();
		this.pnAuthService = pnAuthService;
		this.manageKeyService = manageKeyService;
	}

	public KeyPairDTO getKeyPair () throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException {
		String salt = CommonUtil.getUUID ("") + CommonUtil.getRandomCode (36) + CommonUtil.getUUID ("");
		seed.setSeed (salt.getBytes (StandardCharsets.UTF_8));
		ECKeyPair ecKeyPair = Keys.createEcKeyPair (seed);
		return new KeyPairDTO (awsService.awsKmsEncrypt (ecKeyPair.getPrivateKey ().toString (16)), awsService.awsKmsEncrypt (ecKeyPair.getPublicKey ().toString (16)), Wallet.createLight (salt, ecKeyPair).getAddress (), ecKeyPair);
	}

	public KeyPairDTO getRawKeyPair () throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException {
		String salt = CommonUtil.getUUID ("") + CommonUtil.getRandomCode (36) + CommonUtil.getUUID ("");
		seed.setSeed (salt.getBytes (StandardCharsets.UTF_8));
		ECKeyPair ecKeyPair = Keys.createEcKeyPair (seed);
		return new KeyPairDTO (ecKeyPair.getPrivateKey ().toString (16), ecKeyPair.getPublicKey ().toString (16), Wallet.createLight (salt, ecKeyPair).getAddress (), ecKeyPair);
	}

	public KeyPairDTO getKeyPair (String privateKey) {
		BigInteger privateKeyInteger = new BigInteger (awsService.awsKmsDecrypt (privateKey), 16);
		ECKeyPair ecKeyPair = ECKeyPair.create (privateKeyInteger);
		return new KeyPairDTO (awsService.awsKmsEncrypt (ecKeyPair.getPrivateKey ().toString (16)), awsService.awsKmsEncrypt (ecKeyPair.getPublicKey ().toString (16)), Keys.getAddress (ecKeyPair), ecKeyPair);
	}

	public ECKeyPair getECKeyPair (String privateKey) {
		BigInteger privateKeyInBT = new BigInteger (awsService.awsKmsDecrypt (privateKey), 16);
		ECKeyPair aPair = ECKeyPair.create (privateKeyInBT);
		return aPair;
	}

	protected void addTxHashLog (BlockchainDTO blockchainDTO, ApiType apiType, ResultDTO resultDTO) {
		addTxHashLog (blockchainDTO, apiType, resultDTO, null);
	}

	/**
	 * Tx Hash Log 등록
	 *
	 * @param blockchainDTO
	 * @param apiType
	 * @param resultDTO
	 * @param requestBody
	 */
	protected void addTxHashLog (BlockchainDTO blockchainDTO, ApiType apiType, ResultDTO resultDTO, List<Map<String, Object>> requestBody) {
		Map<String, Object> txHashLogParam;
		String txName = "";
		try {
			boolean isLmApiType = apiType instanceof LeisureMetaApiType;
			if (!isLmApiType && !"/tx".equals (apiType.getUri ())) blockchainDTO.setTxHashLogData (null);

			if (ObjectUtils.isEmpty (blockchainDTO.getTxHashLogData ())) {
				txHashLogParam = new HashMap<> ();
				if (isLmApiType) {
					txName = ((LeisureMetaApiType) apiType).getTxName ();
					txHashLogParam.put ("TX_KND_NM", txName);
					txHashLogParam.put ("GUARDIAN_ACNT_ID", ((LmDTO) blockchainDTO).getGuardian ());
				}
				txHashLogParam.put ("TX_NM", apiType.getApiName ());
				txHashLogParam.put ("USER_CXWALET_TKN_SN", blockchainDTO.getUserCxwaletTknSn ());
				txHashLogParam.put ("BLC_TKN_SN", blockchainDTO.getBlcTknSn ());
				txHashLogParam.put ("BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
				txHashLogParam.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
				txHashLogParam.put ("CREAT_DT", ObjectUtils.isEmpty (blockchainDTO.getCreatDt ()) ? CommonUtil.dbNow () : blockchainDTO.getCreatDt ());
				txHashLogParam.put ("ACNT_ID", blockchainDTO.getFromAddress (true));
				txHashLogParam.put ("TXHASH_ID", blockchainDTO.getTxHash ());
				txHashLogParam.put ("REQUST_URL", blockchainDTO.getRpcUrl ().concat (apiType.getUri ()));
				txHashLogParam.put ("REQUST_HTTP_METHOD", apiType.getMethod ());
				if (requestBody != null) txHashLogParam.put ("REQUST_PARAMTR", objectMapper.writeValueAsString (requestBody));
				txHashLogParam.put ("HTTP_RESULT_CODE", resultDTO.getCode ());
				txHashLogParam.put ("SIGN_TXHASH", blockchainDTO.getTxHash ());
				txHashLogParam.put ("SYS_REGISTER_SN", blockchainDTO.getUserSn ());
				txHashLogParam.put ("RSPNS_RESULT", resultDTO.getData () == null ? resultDTO.getMessage () : String.valueOf (resultDTO.getData ()));
				blockchainDTO.setTxHashLogData (txHashLogParam);
			} else {
				txHashLogParam = blockchainDTO.getTxHashLogData ();
				txHashLogParam.put ("TXHASH_ID", blockchainDTO.getTxHash ());
				txHashLogParam.put ("SIGN_TXHASH", blockchainDTO.getTxHash ());
				txHashLogParam.put ("REQUST_URL", blockchainDTO.getRpcUrl ().concat (apiType.getUri ()));
				txHashLogParam.put ("REQUST_PARAMTR", objectMapper.writeValueAsString (requestBody));
				if (isLmApiType) txHashLogParam.put ("ACNT_ID", ((LmDTO) blockchainDTO).getSignUserUuid ());
				else txHashLogParam.put ("ACNT_ID", blockchainDTO.getFromAddress (true));
				txHashLogParam.put ("HTTP_RESULT_CODE", resultDTO.getCode ());
				txHashLogParam.put ("RSPNS_RESULT", resultDTO.getData () == null ? resultDTO.getMessage () : String.valueOf (resultDTO.getData ()));
			}
			blockchainMapper.insertTxHashLog (txHashLogParam);

			log.info ("==================== txHash log ====================");
			log.info ("Api Name : {}", apiType.getApiName ());
			log.info ("Tx Name : {}", txName);
			log.info ("Status : {}", resultDTO.getCode ());
			log.info ("====================================================");
		} catch (Exception e) {
			log.error (e.getMessage ());
		}
	}

	protected void addTradeInfoLog (BlockchainDTO blockchainDTO) {
		addTradeInfoLog (blockchainDTO, false);
	}

	protected void addNftTradeInfoLog (BlockchainDTO blockchainDTO) {
		addNftTradeInfoLog (blockchainDTO, false);
	}

	/**
	 * transfer log 등록
	 *
	 * @param blockchainDTO
	 * @param swapAt        swap 여부
	 */
	protected void addTradeInfoLog (BlockchainDTO blockchainDTO, boolean swapAt) {
		try {
			String tradeTyCode = "T";
			String blcAcntTyCode = "P";
			Map<String, Object> param = new HashMap<> ();
			param.put ("SYS_ID", "CXWL");
			param.put ("BLC_TKN_SN", blockchainDTO.getBlcTknSn ());
			param.put ("BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
			param.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
			param.put ("CX_TRADE_NO", CommonUtil.createCxTradeNo (tradeTyCode, CommonUtil.now (), String.valueOf (blockchainDTO.getUserSn ())));
			Map<String, Object> thisWeekRwardOrdInfo = blockchainMapper.selectThisWeekRwardOdrInfo ().orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_REWARD_ORDER_INFO);
			});
			param.put ("RWARD_ODR_SN", thisWeekRwardOrdInfo.get ("RWARD_ODR_SN"));
			param.put ("BLC_ACNT_TY_CODE", blcAcntTyCode);
			param.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			param.put ("USER_SN", blockchainDTO.getUserSn ());
			param.put ("TRADE_TY_CODE", tradeTyCode);
			if (!swapAt) {
				blockchainDTO.setToNetworkId (blockchainDTO.getNetworkId ());
			}
			param.put ("TRADE_TY_DETAIL_CODE", CommonUtil.getTransferTradeTyDetailCode (blockchainDTO.getNetworkId (), blockchainDTO.getToNetworkId (), "from", tradeTyCode)); // from Code
			param.put ("TRADE_OCCRRNC_DT", blockchainDTO.getCreatDt ());
			param.put ("SYS_REGISTER_SN", blockchainDTO.getUserSn ());

			Map<String, Object> txHashLogParam = blockchainDTO.getTxHashLogData ();
			int code = (int) txHashLogParam.get ("HTTP_RESULT_CODE");
			if (code == 200) {
				param.put ("BLC_PROCESS_STTUS_CODE", "ED00");
				param.put ("BLC_TRNSMIS_RESULT_CODE", "0000");
			} else {
				param.put ("BLC_PROCESS_STTUS_CODE", "ED99");
				param.put ("BLC_TRNSMIS_RESULT_CODE", code);
			}
			param.put ("TXHASH_SN", txHashLogParam.get ("TXHASH_SN"));
			param.put ("BLC_TRNSMIS_DT", blockchainDTO.getCreatDt ());
			param.put ("BLC_RSPNS_DT", CommonUtil.dbNow ());
			param.put ("SIGN_TXHASH", txHashLogParam.get ("SIGN_TXHASH"));

			param.put ("TRSMTR_BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
			param.put ("TRSMTR_CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
			param.put ("TRSMTR_BLC_ACNT_TY_CODE", blcAcntTyCode);
			param.put ("TRSMTR_USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			param.put ("TRSMTR_USER_SN", blockchainDTO.getUserSn ());
			param.put ("TRSMTR_CXWALET_ADRES", blockchainDTO.getFromAddress (true));
			param.put ("RCVER_BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			param.put ("RCVER_CX_SYMBOL_CODE", blockchainDTO.getToSymbol ());
			param.put ("RCVER_BLC_ACNT_TY_CODE", blcAcntTyCode);

			if (blockchainDTO.getNetworkId ().equals ("1000")) { //LMC 전용
				Map<String, Object> tknFtInfo = blockchainMapper.selectTccoTknFtInfo (param).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_TOKEN_FT_INFO);
				});
				Integer tknDfnSn = Integer.valueOf (String.valueOf (tknFtInfo.get ("TKN_DFN_SN")));
				param.put ("CX_TKN_DFN_SN", tknDfnSn);
			}

			String smrtCntrctAdres;
			Integer userCxWaletTknSn = blockchainDTO.getUserCxwaletTknSn ();
			Map<String, Object> walletParam = new HashMap<> ();
			if (userCxWaletTknSn != null && userCxWaletTknSn.longValue () > 0) {
				walletParam.put ("USER_CXWALET_TKN_SN", userCxWaletTknSn);
				Map<String, Object> userCxwaletInfo = blockchainMapper.selectUserCxwaletTknInfo (walletParam).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_WALLET_TOKEN_INFO);
				});
				smrtCntrctAdres = (String) userCxwaletInfo.get ("SMRT_CNTRCT_ADRES");
			} else {
				walletParam.put ("BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
				walletParam.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
				Map<String, Object> tokenBaseInfo = blockchainMapper.selectTccoBlcTknBass (walletParam).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_TOKEN_INFO);
				});
				smrtCntrctAdres = (String) tokenBaseInfo.get ("SMRT_CNTRCT_ADRES");
			}
			param.put ("TRADE_CNTRCT_ADRES", smrtCntrctAdres);

			String toSmrtCntrctAdres;
			Integer rcverUserCmmnSn, rcverUserSn;
			if (swapAt) {
				rcverUserCmmnSn = blockchainDTO.getUserCmmnSn ();
				rcverUserSn = blockchainDTO.getUserSn ();
				Map<String, Object> tokenParam = new HashMap<> ();
				tokenParam.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
				tokenParam.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
				Map<String, Object> tokenBaseInfo = blockchainMapper.selectTccoBlcTknBass (tokenParam).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_TOKEN_INFO);
				});
				toSmrtCntrctAdres = (String) tokenBaseInfo.get ("SMRT_CNTRCT_ADRES");
			} else {
				Map<String, Object> rcverUserInfo = commonMapper.selectUserInfo (Map.of ("CXWALET_ADRES", blockchainDTO.getToAddress (true), "BLC_NTWRK_ID", blockchainDTO.getNetworkId ())).orElse (Map.of ()); // 수신자 정보
				rcverUserCmmnSn = (Integer) rcverUserInfo.get ("USER_CMMN_SN");
				rcverUserSn = (Integer) rcverUserInfo.get ("USER_SN");
				toSmrtCntrctAdres = smrtCntrctAdres;
			}

			param.put ("RCVER_USER_CMMN_SN", rcverUserCmmnSn);
			param.put ("RCVER_USER_SN", rcverUserSn);
			param.put ("RCVER_CXWALET_ADRES", blockchainDTO.getToAddress (true));
			param.put ("CX_RWARD_PYMNT_AT", "N");
			BigDecimal amount = blockchainDTO.getAmount ().multiply (BigDecimal.valueOf (-1));
			BigDecimal cxFeeQty = BigDecimal.ZERO;
			if (blockchainDTO.getCxFeeQty () != null) {
				cxFeeQty = blockchainDTO.getCxFeeQty ().multiply (BigDecimal.valueOf (-1));
			}
			BigDecimal remaningAmount;
			if (swapAt && !"1000".equals (blockchainDTO.getNetworkId ())) remaningAmount = blockchainDTO.getTotalAmount ().add (amount);
			else remaningAmount = blockchainDTO.getTotalAmount ().add (amount).add (cxFeeQty);
			if (remaningAmount.compareTo (BigDecimal.ZERO) < 1) remaningAmount = BigDecimal.ZERO;
			param.put ("CX_TRADE_QTY", amount);
			param.put ("CX_FEE_QTY", cxFeeQty);
			param.put ("CX_BLCE_QTY", remaningAmount);
			blockchainMapper.insertTradeInfoLog (param); // from 로그
			param.put ("PREV_CX_TRADE_SN", param.get ("CX_TRADE_SN"));

			param.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			param.put ("USER_CMMN_SN", rcverUserCmmnSn);
			param.put ("USER_SN", rcverUserSn);
			param.put ("TRADE_TY_CODE", tradeTyCode);
			param.put ("TRADE_TY_DETAIL_CODE", CommonUtil.getTransferTradeTyDetailCode (blockchainDTO.getNetworkId (), blockchainDTO.getToNetworkId (), "to", tradeTyCode)); // to Code
			param.put ("TRADE_CNTRCT_ADRES", toSmrtCntrctAdres);
			BigDecimal toAmount = blockchainDTO.getAmount ();
			BigDecimal toRemaningAmount = blockchainDTO.getReceiverTotalAmount ().add (toAmount);
			if (toRemaningAmount.compareTo (BigDecimal.ZERO) < 1) toRemaningAmount = BigDecimal.ZERO;
			param.put ("CX_TRADE_QTY", toAmount);
			param.put ("CX_FEE_QTY", null);
			param.put ("CX_BLCE_QTY", toRemaningAmount);
			blockchainMapper.insertTradeInfoLog (param); // to 로그
		} catch (WalletException e) {
			log.error (e.getMessage ());
		} catch (Exception e) {
			log.error (e.getMessage ());
		}
	}

	/**
	 * nft transfer log 등록
	 *
	 * @param blockchainDTO
	 * @param swapAt        swap 여부
	 */
	protected void addNftTradeInfoLog (BlockchainDTO blockchainDTO, boolean swapAt) {
		try {
			String tradeTyCode = "N";
			String blcAcntTyCode = "P";
			Map<String, Object> param = new HashMap<> ();
			param.put ("SYS_ID", "CXWL");
			param.put ("BLC_TKN_SN", blockchainDTO.getBlcTknSn ());
			param.put ("BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
			param.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
			param.put ("CX_TRADE_NO", CommonUtil.createCxTradeNo (tradeTyCode, CommonUtil.now (), String.valueOf (blockchainDTO.getUserSn ())));
			Map<String, Object> thisWeekRwardOrdInfo = blockchainMapper.selectThisWeekRwardOdrInfo ().orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_REWARD_ORDER_INFO);
			});
			param.put ("RWARD_ODR_SN", thisWeekRwardOrdInfo.get ("RWARD_ODR_SN"));
			param.put ("BLC_ACNT_TY_CODE", blcAcntTyCode);
			param.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			param.put ("USER_SN", blockchainDTO.getUserSn ());
			param.put ("TRADE_TY_CODE", tradeTyCode);
			if (!swapAt) {
				blockchainDTO.setToNetworkId (blockchainDTO.getNetworkId ());
			}
			param.put ("TRADE_TY_DETAIL_CODE", CommonUtil.getTransferTradeTyDetailCode (blockchainDTO.getNetworkId (), blockchainDTO.getToNetworkId (), "from", tradeTyCode)); // from Code
			param.put ("TRADE_OCCRRNC_DT", blockchainDTO.getCreatDt ());
			param.put ("SYS_REGISTER_SN", blockchainDTO.getUserSn ());

			Map<String, Object> txHashLogParam = blockchainDTO.getTxHashLogData ();
			int code = (int) txHashLogParam.get ("HTTP_RESULT_CODE");
			if (code == 200) {
				param.put ("BLC_PROCESS_STTUS_CODE", "ED00");
				param.put ("BLC_TRNSMIS_RESULT_CODE", "0000");
			} else {
				param.put ("BLC_PROCESS_STTUS_CODE", "ED99");
				param.put ("BLC_TRNSMIS_RESULT_CODE", code);
			}
			param.put ("TXHASH_SN", txHashLogParam.get ("TXHASH_SN"));
			param.put ("BLC_TRNSMIS_DT", blockchainDTO.getCreatDt ());
			param.put ("BLC_RSPNS_DT", CommonUtil.dbNow ());
			param.put ("SIGN_TXHASH", txHashLogParam.get ("SIGN_TXHASH"));

			param.put ("TRSMTR_BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
			param.put ("TRSMTR_BLC_ACNT_TY_CODE", blcAcntTyCode);
			param.put ("TRSMTR_USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			param.put ("TRSMTR_USER_SN", blockchainDTO.getUserSn ());
			param.put ("TRSMTR_CXWALET_ADRES", blockchainDTO.getFromAddress (true));
			param.put ("RCVER_BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			param.put ("RCVER_BLC_ACNT_TY_CODE", blcAcntTyCode);

			if (blockchainDTO.getNetworkId ().equals ("1000")) { //LMC 전용
				LmDTO lmDTO = (LmDTO) blockchainDTO;
				LmcNftDTO nftitm = blockchainMapper.selectTncnNftitmInfo (Map.of ("LANG_CODE", lmDTO.getLangCode (), "NFTITM_SN", lmDTO.getNftitmSn ())).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_NFT_INFO);
				});
				param.put ("NFTITM_SN", nftitm.getNftitmSn ());
				param.put ("RANDBOX_OPN_AT", nftitm.getRandboxOpnAt ());
				param.put ("RANDBOX_SN", nftitm.getRandboxSn ());
				param.put ("NFTITM_RARE_CODE", nftitm.getNftitmRareCode ());
				param.put ("NFTITM_RARE_WGHTVAL", nftitm.getNftitmRareWghtval ());

				Map<String, Object> tknNftInfo = blockchainMapper.selectTccoTknNftInfo (nftitm.getTknNftSn ()).orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_TOKEN_NFT_INFO);
				});
				Integer tknDfnSn = Integer.valueOf (String.valueOf (tknNftInfo.get ("TKN_DFN_SN")));
				param.put ("CX_TKN_DFN_SN", tknDfnSn);
				param.put ("NFT_TKN_ID", nftitm.getTknId ());
			} else {
				ErcDTO ercDTO = (ErcDTO) blockchainDTO;
				param.put ("NFT_TKN_ID", ercDTO.getTokenId ().toString ());
			}

			String smrtCntrctAdres = null;
			if (blockchainDTO.getNetworkId ().equals ("1001")) smrtCntrctAdres = ETH_NFT_SMRT_CNTRCT_ADRES;
			else if (blockchainDTO.getNetworkId ().equals ("1002")) smrtCntrctAdres = BNB_NFT_SMRT_CNTRCT_ADRES;
			else if (blockchainDTO.getNetworkId ().equals ("1000")) smrtCntrctAdres = LMC_NFT_SMRT_CNTRCT_ADRES;
			param.put ("TRADE_CNTRCT_ADRES", smrtCntrctAdres);

			String toSmrtCntrctAdres = null;
			Integer rcverUserCmmnSn, rcverUserSn;
			if (swapAt) {
				rcverUserCmmnSn = blockchainDTO.getUserCmmnSn ();
				rcverUserSn = blockchainDTO.getUserSn ();
				if (blockchainDTO.getToNetworkId ().equals ("1001")) toSmrtCntrctAdres = ETH_NFT_SMRT_CNTRCT_ADRES;
				else if (blockchainDTO.getToNetworkId ().equals ("1002")) toSmrtCntrctAdres = BNB_NFT_SMRT_CNTRCT_ADRES;
				else if (blockchainDTO.getToNetworkId ().equals ("1000")) toSmrtCntrctAdres = LMC_NFT_SMRT_CNTRCT_ADRES;
			} else {
				Map<String, Object> rcverUserInfo = commonMapper.selectUserInfo (Map.of ("CXWALET_ADRES", blockchainDTO.getToAddress (true), "BLC_NTWRK_ID", blockchainDTO.getNetworkId ())).orElse (Map.of ()); // 수신자 정보
				rcverUserCmmnSn = (Integer) rcverUserInfo.get ("USER_CMMN_SN");
				rcverUserSn = (Integer) rcverUserInfo.get ("USER_SN");
				toSmrtCntrctAdres = smrtCntrctAdres;
			}

			param.put ("RCVER_USER_CMMN_SN", rcverUserCmmnSn);
			param.put ("RCVER_USER_SN", rcverUserSn);
			param.put ("RCVER_CXWALET_ADRES", blockchainDTO.getToAddress (true));
			param.put ("CX_RWARD_PYMNT_AT", "N");
			blockchainMapper.insertTradeInfoLog (param); // from 로그
			param.put ("PREV_CX_TRADE_SN", param.get ("CX_TRADE_SN"));

			param.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			param.put ("USER_CMMN_SN", rcverUserCmmnSn);
			param.put ("USER_SN", rcverUserSn);
			param.put ("TRADE_TY_CODE", tradeTyCode);
			param.put ("TRADE_TY_DETAIL_CODE", CommonUtil.getTransferTradeTyDetailCode (blockchainDTO.getNetworkId (), blockchainDTO.getToNetworkId (), "to", tradeTyCode)); // to Code
			param.put ("TRADE_CNTRCT_ADRES", toSmrtCntrctAdres);
			blockchainMapper.insertTradeInfoLog (param); // to 로그
		} catch (WalletException e) {
			log.error (e.getMessage ());
		} catch (Exception e) {
			log.error (e.getMessage ());
		}
	}

	/**
	 * 기본 정보 설정
	 *
	 * @param blockchainDTO
	 * @return
	 * @throws IOException
	 */
	protected Map<String, Object> getBaseInfo (BlockchainDTO blockchainDTO) throws IOException {
		Map<String, Object> result = new HashMap<> ();

		String networkId = blockchainDTO.getNetworkId ();
		Map<String, Object> networkInfo = commonMapper.selectBlcNtwkBass (networkId).orElseThrow (() -> {
			throw new WalletException (StatusCode.NO_NETWORK_INFO);
		});
		String rpcUrl = (String) networkInfo.get ("BLC_RPC_URL");
		if (!blockchainDTO.getNetworkId ().equals ("1000")) {
			blockchainDTO.setWeb3j (Web3j.build (new HttpService (rpcUrl.concat (apiKey))));
			blockchainDTO.setChainId (blockchainDTO.getWeb3j ().ethChainId ().send ().getChainId ().intValue ());
			blockchainDTO.setRpcUrl (rpcUrl);
		} else {
			result.put ("BLC_RPC_URL", rpcUrl);
		}

		Map<String, Object> param = new HashMap<> ();
		if (ObjectUtils.isEmpty (blockchainDTO.getUserCxwaletTknSn ())) {
			param.put ("BLC_NTWRK_ID", blockchainDTO.getNetworkId ());
			param.put ("CX_SYMBOL_CODE", blockchainDTO.getSymbol ());
			Map<String, Object> tccoBlcTknBass = blockchainMapper.selectTccoBlcTknBass (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_TOKEN_INFO);
			}); // 토큰 기본 정보

			param.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			param.put ("SYS_ID", "CXWL");
			Map<String, Object> userCxwaletInfo = blockchainMapper.selectUserCxwaletInfo (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_INFO);
			}); // 사용자 암호화폐지갑 정보

			param.put ("USER_CXWALET_SN", userCxwaletInfo.get ("USER_CXWALET_SN"));
			param.put ("CX_SYMBOL_CODE", tccoBlcTknBass.get ("CX_SYMBOL_CODE"));
			Map<String, Object> userCxwaletTknInfo = blockchainMapper.selectUserCxwaletTknInfo (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_TOKEN_INFO);
			}); // 사용자 암호화폐지갑 토큰 정보

			result.putAll (tccoBlcTknBass);
			result.putAll (userCxwaletInfo);
			result.putAll (userCxwaletTknInfo);
		} else {
			param.put ("USER_CXWALET_TKN_SN", blockchainDTO.getUserCxwaletTknSn ());
			param.put ("BLC_NTWRK_ID", networkId);
			Map<String, Object> userCxwaletTknInfo = blockchainMapper.selectUserCxwaletTknInfo (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_TOKEN_INFO);
			}); // 사용자 암호화폐지갑 토큰 정보

			result.putAll (userCxwaletTknInfo);
		}
		return result;
	}

	/**
	 * 게이트웨이 주소 검증
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected boolean validateGatewayAddress (BlockchainDTO blockchainDTO) throws IOException {
		// LMC 게이트웨이 주소 정보
		Map<String, Object> networkInfo = commonMapper.selectBlcNtwkBass ("1000").orElseThrow (() -> {
			throw new WalletException (StatusCode.NO_NETWORK_INFO);
		});
		Map<String, Object> param = new HashMap<> ();
		param.put ("pathVariable", blockchainDTO.getFromAddress (true));
		ResultDTO accountDTO = HttpUtil.send ((String) networkInfo.get ("BLC_RPC_URL"), LeisureMetaApiType.ACCOUNT, param);
		addTxHashLog (blockchainDTO, LeisureMetaApiType.ACCOUNT, accountDTO);
		if (accountDTO.getCode () != 200) throw new WalletException (StatusCode.ACCOUNT_NOT_FOUND);
		Map<String, Object> data = objectMapper.readValue ((String) accountDTO.getData (), Map.class);
		String gatewayKey, gatewayAddress = null;
		if (("1000".equals (blockchainDTO.getNetworkId ()) && "1001".equals (blockchainDTO.getToNetworkId ())) || ("1001".equals (blockchainDTO.getNetworkId ()) && "1000".equals (blockchainDTO.getToNetworkId ()))) {
			gatewayKey = "ethAddress";
		} else if (("1000".equals (blockchainDTO.getNetworkId ()) && "1002".equals (blockchainDTO.getToNetworkId ())) || ("1002".equals (blockchainDTO.getNetworkId ()) && "1000".equals (blockchainDTO.getToNetworkId ()))) {
			gatewayKey = "bnbAddress"; // TODO : 가칭이므로 수정 필요
		} else {
			throw new WalletException (StatusCode.UNSUPPORTED_SWAP);
		}
		for (String key : data.keySet ()) {
			if (key.equals (gatewayKey)) {
				gatewayAddress = String.valueOf (data.get (key));
				break;
			}
		}

		// 지갑 주소 정보
		param.clear ();
		param.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
		param.put ("SYS_ID", "CXWL");
		param.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
		Map<String, Object> walletInfo = blockchainMapper.selectUserCxwaletInfo (param).orElseThrow (() -> {
			throw new WalletException (StatusCode.NO_WALLET_INFO);
		});
		String walletAddress = (String) walletInfo.get ("CXWALET_ADRES");
		blockchainDTO.setToAddress (walletAddress);

		return !ObjectUtils.isEmpty (gatewayAddress) && gatewayAddress.equals (walletAddress);
	}

	protected BlockchainDTO setData (BlockchainDTO blockchainDTO) throws IOException {
		Map<String, Object> baseInfo = getBaseInfo (blockchainDTO);
		if ("1000".equals (blockchainDTO.getNetworkId ())) {
			LmDTO lmDTO = (LmDTO) blockchainDTO;
			lmDTO.setUserSn ((Integer) baseInfo.get ("USER_SN"));
			lmDTO.setRpcUrl ((String) baseInfo.get ("BLC_RPC_URL"));
			lmDTO.setFromAddress ((String) baseInfo.get ("CXWALET_ADRES")); // 송신자 지갑주소
			if (ObjectUtils.isEmpty (lmDTO.getPrivateKey ())) {
				lmDTO.setPrivateKey (manageKeyService.joinPrivateKey (lmDTO.getUserSn (), lmDTO.getFromAddress (true), lmDTO.getNetworkId ())); // 송신자 개인키
			}
			if (ObjectUtils.isEmpty (lmDTO.getSymbol ())) {
				lmDTO.setSymbol ((String) baseInfo.get ("CX_SYMBOL_CODE")); // 심볼
			}
			lmDTO.setDcmlpointLt ((Integer) baseInfo.get ("DCMLPOINT_LT")); // 소수점 길이
			lmDTO.setBlcTknSn ((Integer) baseInfo.get ("BLC_TKN_SN")); // BLC 토큰 일련번호
			return lmDTO;
		} else {
			ErcDTO ercDTO = (ErcDTO) blockchainDTO;
			ercDTO.setUserSn ((Integer) baseInfo.get ("USER_SN"));
			ercDTO.setBlcNtwrkBassTknAt ((String) baseInfo.get ("BLC_NTWRK_BASS_TKN_AT")); // 네트워크 기본 토큰 여부
			ercDTO.setFromAddress ((String) baseInfo.get ("CXWALET_ADRES")); // 송신자 지갑주소
			ercDTO.setPrivateKey (awsService.awsKmsDecrypt (manageKeyService.joinPrivateKey (ercDTO.getUserSn (), ercDTO.getFromAddress (true), ercDTO.getNetworkId ()))); // 송신자 개인키
			if (ObjectUtils.isEmpty (ercDTO.getSymbol ())) {
				ercDTO.setSymbol ((String) baseInfo.get ("CX_SYMBOL_CODE")); // 심볼
			}
			if (ObjectUtils.isEmpty (ercDTO.getContractAddress ())) {
				ercDTO.setContractAddress ((String) baseInfo.get ("SMRT_CNTRCT_ADRES")); // 토큰 계약 주소
			}
			ercDTO.setDcmlpointLt ((Integer) baseInfo.get ("DCMLPOINT_LT")); // 소수점 길이
			ercDTO.setBlcTknSn ((Integer) baseInfo.get ("BLC_TKN_SN")); // BLC 토큰 일련번호
			return ercDTO;
		}
	}

	protected ResultDTO getBalance (BlockchainDTO blockchainDTO) throws Exception {
		if (blockchainDTO instanceof ErcDTO) {
			ErcDTO ercDTO = (ErcDTO) blockchainDTO;
			Web3j web3j = ercDTO.getWeb3j ();
			BigDecimal balance;
			if ("Y".equals (ercDTO.getBlcNtwrkBassTknAt ())) {
				EthGetBalance ethGetBalance = web3j.ethGetBalance (ercDTO.getFromAddress (), DefaultBlockParameterName.LATEST).send ();
				balance = ConvertUtil.toBigDecimal (ethGetBalance.getBalance ().toString (), ercDTO.getDcmlpointLt ());
			} else {
				if (ObjectUtils.isEmpty (ercDTO.getContractAddress ())) {
					balance = BigDecimal.ZERO;
				} else {
					TransactionManager transactionManager = new ClientTransactionManager (web3j, ercDTO.getFromAddress ());
					ERC20 erc20 = ERC20.load (ercDTO.getContractAddress (), web3j, transactionManager, new DefaultGasProvider ());
					balance = ConvertUtil.toBigDecimal (String.valueOf (erc20.balanceOf (ercDTO.getFromAddress ()).send ()), ercDTO.getDcmlpointLt ());
				}
			}
			ResultDTO resultDTO = new ResultDTO<> (StatusCode.ACCESS, balance);
			addTxHashLog (ercDTO, EthereumApiType.BALANCE, resultDTO);
			return resultDTO;
		} else {
			LmDTO lmDTO = (LmDTO) blockchainDTO;
			Map<String, Object> param = new HashMap<> ();
			param.put ("movable", lmDTO.getMovable ());
			param.put ("pathVariable", lmDTO.getFromAddress (true));
			ResultDTO resultDTO = HttpUtil.send (lmDTO.getRpcUrl (), LeisureMetaApiType.BALANCE, param);
			addTxHashLog (lmDTO, LeisureMetaApiType.BALANCE, resultDTO);
			if (resultDTO.getCode () != 200) {
				resultDTO.setData (0);
			}
			return resultDTO;
		}
	}

	/**
	 * FT 조회
	 *
	 * @param blockchainDTO
	 * @return
	 */
	public ResultDTO balance (BlockchainDTO blockchainDTO) {
		ResultDTO resultDTO;
		try {
			if ("1000".equals (blockchainDTO.getNetworkId ())) {
				LmDTO lmDTO = (LmDTO) setData (blockchainDTO);
				resultDTO = getBalance (lmDTO);

				Object totalAmount;
				String codeStr = String.valueOf (resultDTO.getCode ());
				if (codeStr.startsWith ("4")) {
					resultDTO.setCode (StatusCode.ACCESS.getCode ());
					resultDTO.setMessage (StatusCode.ACCESS.getMessage ());
					totalAmount = resultDTO.getData ();
				} else if (codeStr.startsWith ("5")) {
					throw new WalletException (StatusCode.LMC_SERVER_ERROR);
				} else {
					Map<String, Object> data = objectMapper.readValue (String.valueOf (resultDTO.getData ()), Map.class);
					totalAmount = ((Map) data.get ("LM")).get ("totalAmount");
				}

				BalanceDTO balanceDTO = new BalanceDTO ();
				balanceDTO.setCxwaletAdres (lmDTO.getFromAddress ());
				if (!ObjectUtils.isEmpty (totalAmount)) {
					BigDecimal balance = ConvertUtil.toBigDecimal (new BigDecimal (String.valueOf (totalAmount)), lmDTO.getDcmlpointLt ());
					balanceDTO.setBalance (balance);
					Map<String, Object> exrBass = commonMapper.selectExrBass (lmDTO.getSymbol ()).orElseThrow (() -> {
						throw new WalletException (StatusCode.NO_EXR_BASS);
					});
					BigDecimal usdExr = (BigDecimal) exrBass.get ("CX_EXR_USD_CRRNCY_AMT");
					BigDecimal krwExr = (BigDecimal) exrBass.get ("CX_EXR_KRW_CRRNCY_AMT");
					balanceDTO.setUsdExr (usdExr);
					balanceDTO.setKrwExr (krwExr);
					balanceDTO.setUsdPrice (balance.multiply (usdExr));
					balanceDTO.setKrwPrice (balance.multiply (krwExr));
					balanceDTO.setKrwPercentChange ((BigDecimal) exrBass.get ("CX_EXR_KRW_PERCENT_CHANGE_1H"));
					balanceDTO.setUsdPercentChange ((BigDecimal) exrBass.get ("CX_EXR_USD_PERCENT_CHANGE_1H"));
				}
				resultDTO.setData (balanceDTO);
			} else {
				ErcDTO ercDTO = (ErcDTO) setData (blockchainDTO);
				resultDTO = getBalance (ercDTO);

				BigDecimal balance = (BigDecimal) resultDTO.getData ();
				BalanceDTO balanceDTO = new BalanceDTO ();
				balanceDTO.setCxwaletAdres (ercDTO.getFromAddress ());
				if (!ObjectUtils.isEmpty (balance)) {
					balanceDTO.setBalance (balance);
					Map<String, Object> exrBass = commonMapper.selectExrBass (ercDTO.getSymbol ()).orElseThrow (() -> {
						throw new WalletException (StatusCode.NO_EXR_BASS);
					});
					BigDecimal usdExr = (BigDecimal) exrBass.get ("CX_EXR_USD_CRRNCY_AMT");
					BigDecimal krwExr = (BigDecimal) exrBass.get ("CX_EXR_KRW_CRRNCY_AMT");
					balanceDTO.setUsdExr (usdExr);
					balanceDTO.setKrwExr (krwExr);
					balanceDTO.setUsdPrice (balance.multiply (usdExr));
					balanceDTO.setKrwPrice (balance.multiply (krwExr));
					balanceDTO.setKrwPercentChange ((BigDecimal) exrBass.get ("CX_EXR_KRW_PERCENT_CHANGE_1H"));
					balanceDTO.setUsdPercentChange ((BigDecimal) exrBass.get ("CX_EXR_USD_PERCENT_CHANGE_1H"));
				}
				resultDTO.setData (balanceDTO);
			}
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return resultDTO;
	}

	/**
	 * FT 전송
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO transfer (BlockchainDTO blockchainDTO);

	/**
	 * NFT 목록 조회
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO nfts (BlockchainDTO blockchainDTO);

	/**
	 * NFT 조회
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO nft (BlockchainDTO blockchainDTO);

	/**
	 * NFT 전송
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO nftTransfer (BlockchainDTO blockchainDTO);

	/**
	 * 스왑
	 *
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO swap (BlockchainDTO blockchainDTO);

	/**
	 * NFT 스왑
	 * @param blockchainDTO
	 * @return
	 */
	protected abstract ResultDTO nftSwap (BlockchainDTO blockchainDTO);

	/**
	 * 자산 소유 여부
	 *
	 * @param userCmmnSn
	 * @return
	 * @throws IOException
	 */
	protected abstract boolean whetherOwnedAssets (Integer userCmmnSn);
}
