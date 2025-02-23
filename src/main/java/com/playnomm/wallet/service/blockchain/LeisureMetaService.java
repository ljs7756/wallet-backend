package com.playnomm.wallet.service.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.blockchain.BlockchainDTO;
import com.playnomm.wallet.dto.blockchain.ErcDTO;
import com.playnomm.wallet.dto.blockchain.KeyPairDTO;
import com.playnomm.wallet.dto.blockchain.LmDTO;
import com.playnomm.wallet.dto.blockchain.response.BalanceDTO;
import com.playnomm.wallet.dto.blockchain.response.LmTxDTO;
import com.playnomm.wallet.dto.blockchain.response.LmcNftDTO;
import com.playnomm.wallet.dto.blockchain.response.LmcNftsDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author : hzn
 * @date : 2022/12/13
 * @description :
 */
@Slf4j
@Service
public class LeisureMetaService extends AbstractBlockchainService {
	@Value("${cloud.aws.cdn.prefix}")
	private String awsCdnPrefix;

	@Autowired
	protected LeisureMetaService (AwsService awsService, BlockchainMapper blockchainMapper, CommonMapper commonMapper, @Value("${ethereum.api.key}") String apiKey, PnAuthService pnAuthService, ManageKeyService manageKeyService) {
		super (awsService, blockchainMapper, commonMapper, apiKey, pnAuthService, manageKeyService);
	}

	@Override
	public ResultDTO transfer (BlockchainDTO blockchainDTO) {
		return transfer (blockchainDTO, false);
	}

	private ResultDTO transfer (BlockchainDTO blockchainDTO, boolean swapAt) {
		ResultDTO resultDTO = null;
		LmDTO lmDTO = null;
		boolean isException = false;

		try {
			LocalDateTime now = CommonUtil.now (); // 트랜잭션 전송 시간

			if (!swapAt) lmDTO = (LmDTO) setData (blockchainDTO);
			else lmDTO = (LmDTO) blockchainDTO;
			lmDTO.setCreatDt (CommonUtil.dbNow (now));

			if (lmDTO.getFromAddress ().equalsIgnoreCase (lmDTO.getToAddress ())) throw new WalletException (StatusCode.CHECK_RECEIVER_ADDRESS);

			ResultDTO balanceDTO = getBalance (lmDTO);
			if (balanceDTO.getCode () != 200) throw new WalletException (StatusCode.BALANCE_NOT_FOUND);
			Map<String, Object> body = objectMapper.readValue ((String) balanceDTO.getData (), Map.class);
			Map<String, Object> lmMap = ((Map) body.get ("LM"));
			BigDecimal totalAmount = ConvertUtil.toBigDecimal (String.valueOf (lmMap.get ("totalAmount")), lmDTO.getDcmlpointLt ());
			if (totalAmount.compareTo (lmDTO.getAmount ().add (lmDTO.getCxFeeQty ())) == -1) throw new WalletException (StatusCode.NOT_ENOUGH_BALANCE);
			lmDTO.setTotalAmount (totalAmount); // 현재 밸런스 저장

			if (!swapAt) {
				// 수신자 확인
				if (commonMapper.selectUserInfo (Map.of ("CXWALET_ADRES", lmDTO.getToAddress (true), "BLC_NTWRK_ID", lmDTO.getNetworkId ())).isEmpty ()) throw new WalletException (StatusCode.NO_RECEIVER_INFO);

				String fromAddress = lmDTO.getFromAddress ();
				lmDTO.setFromAddress (lmDTO.getToAddress ());
				ResultDTO receiverBalanceDTO = getBalance (lmDTO);
				lmDTO.setTxHashLogData (null);
				if (receiverBalanceDTO.getCode () == 200) {
					Map<String, Object> receiverBody = objectMapper.readValue ((String) receiverBalanceDTO.getData (), Map.class);
					Map<String, Object> receiverLmMap = ((Map) receiverBody.get ("LM"));
					BigDecimal receiverTotalAmount = ConvertUtil.toBigDecimal (String.valueOf (receiverLmMap.get ("totalAmount")), lmDTO.getDcmlpointLt ());
					lmDTO.setReceiverTotalAmount (receiverTotalAmount); // 수신자 현재 밸런스 저장
				} else {
					lmDTO.setReceiverTotalAmount (BigDecimal.ZERO); // 수신자 현재 밸런스 저장
				}

				lmDTO.setFromAddress (fromAddress);
			}
			Set<String> keys = ((Map) lmMap.get ("unused")).keySet ();
			List<String> balanceList = new ArrayList<> (keys);

			Map<String, Object> dataParam = new HashMap<> ();
			Map<String, Object> outputsMap = new HashMap<> ();
			BigDecimal receiveQty = totalAmount.subtract (lmDTO.getAmount ());
			if (lmDTO.getCxFeeQty () != null) receiveQty = receiveQty.subtract (lmDTO.getCxFeeQty ());
			if (receiveQty.compareTo (BigDecimal.ZERO) > 0) outputsMap.put (lmDTO.getFromAddress (true), ConvertUtil.toBigInteger (receiveQty, lmDTO.getDcmlpointLt ()));
			if (swapAt) outputsMap.put (lmDTO.getGtwyAdres (), ConvertUtil.toBigInteger (lmDTO.getAmount (), lmDTO.getDcmlpointLt ()));
			else outputsMap.put (lmDTO.getToAddress (true), ConvertUtil.toBigInteger (lmDTO.getAmount (), lmDTO.getDcmlpointLt ()));
			if (swapAt) outputsMap.put ("playnomm", ConvertUtil.toBigInteger (lmDTO.getCxFeeQty (), lmDTO.getDcmlpointLt ()));
			dataParam.put ("inputs", balanceList);
			dataParam.put ("outputs", outputsMap);
			dataParam.put ("createdAt", CommonUtil.bcNow (now));
			dataParam.put ("tokenDefinitionId", lmDTO.getSymbol ());
			lmDTO.setSignUserUuid (lmDTO.getFromAddress (true));

			resultDTO = transaction (lmDTO, LeisureMetaApiType.TRANSFER_FUNGIBLE_TOKEN, dataParam);
		} catch (WalletException we) {
			log.error (we.getMessage ());
			isException = true;
			resultDTO = new ResultDTO (we.getCode (), we.getMessage ());
		} catch (Exception e) {
			log.error (e.getMessage ());
			isException = true;
			resultDTO = new ResultDTO (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		} finally {
			if (resultDTO == null) resultDTO = new ResultDTO (StatusCode.BASIC_ERROR);

			if (isException && resultDTO.getCode () != 200) addTxHashLog (lmDTO, LeisureMetaApiType.TRANSFER_FUNGIBLE_TOKEN, resultDTO);
			addTradeInfoLog (lmDTO, swapAt);

			if (resultDTO.getCode () == 200) {
				// DTO 변환
				LmTxDTO lmTxDTO = new LmTxDTO ();
				lmTxDTO.setTransactionHash ((String) resultDTO.getData ());
				resultDTO.setData (lmTxDTO);
			}
		}

		return resultDTO;
	}

	@Override
	public ResultDTO nfts (BlockchainDTO blockchainDTO) {
		ResultDTO resultDTO;
		try {
			LmDTO lmDTO = (LmDTO) setData (blockchainDTO);
			resultDTO = getNfts (lmDTO);
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return resultDTO;
	}

	@Override
	public ResultDTO nft (BlockchainDTO blockchainDTO) {
		ResultDTO resultDTO;
		try {
			LmDTO lmDTO = (LmDTO) blockchainDTO;
			resultDTO = new ResultDTO (StatusCode.ACCESS, blockchainMapper.selectTncnNftitmInfo (Map.of ("LANG_CODE", lmDTO.getLangCode (), "NFTITM_SN", lmDTO.getNftitmSn (), "USER_CMMN_SN", lmDTO.getUserCmmnSn ())).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_NFT_DATA);
			}));
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return resultDTO;
	}

	@Override
	public ResultDTO nftTransfer (BlockchainDTO blockchainDTO) {
		return nftTransfer (blockchainDTO, false);
	}

	private ResultDTO nftTransfer (BlockchainDTO blockchainDTO, boolean swapAt) {
		ResultDTO resultDTO = null;
		LmDTO lmDTO = null;
		Map<String, Object> param = new HashMap<> ();
		Integer nommUserSn = null;
		boolean isException = false;

		try {
			LocalDateTime now = CommonUtil.now (); // 트랜잭션 전송 시간

			if (!swapAt) lmDTO = (LmDTO) setData (blockchainDTO);
			else lmDTO = (LmDTO) blockchainDTO;
			lmDTO.setCreatDt (CommonUtil.dbNow (now));

			if (lmDTO.getFromAddress ().equalsIgnoreCase (lmDTO.getToAddress ())) throw new WalletException (StatusCode.CHECK_RECEIVER_ADDRESS);

			// 수신자 PLAY_NOMM 서비스 USER_SN 호출
			Map<String, Object> receiver = commonMapper.selectUserInfo (Map.of ("CXWALET_ADRES", lmDTO.getToAddress (true), "BLC_NTWRK_ID", lmDTO.getNetworkId ())).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_RECEIVER_INFO);
			});
			List<Map<String, Object>> services = pnAuthService.listOfServices ((Integer) receiver.get ("USER_CMMN_SN"));
			nommUserSn = services.stream ().filter (m -> m.containsKey ("NOMM")).map (m -> (Integer) m.get ("NOMM")).findFirst ().orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_PLAYNOMM_RECEIVER_INFO);
			});

			param.put ("NFTITM_SN", lmDTO.getNftitmSn ());
			param.put ("LANG_CODE", lmDTO.getLangCode ());
			LmcNftDTO nft = blockchainMapper.selectTncnNftitmInfo (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_NFT_INFO);
			});

			param.clear ();
			param.put ("createdAt", CommonUtil.bcNow (now));
			param.put ("definitionId", nft.getTknDfnId ());
			param.put ("tokenId", nft.getTknId ());
			param.put ("input", nft.getNftSignTxhash ());
			String output;
			if (!swapAt) output = lmDTO.getToAddress (true);
			else output = lmDTO.getGtwyAdres ();
			param.put ("output", output);
			param.put ("memo", "Internal-Transfer");
			lmDTO.setSignUserUuid (lmDTO.getFromAddress (true));

			resultDTO = transaction (lmDTO, LeisureMetaApiType.TRANSFER_NFT, param);
		} catch (WalletException we) {
			log.error (we.getMessage ());
			isException = true;
			resultDTO = new ResultDTO (we.getCode (), we.getMessage ());
		} catch (Exception e) {
			log.error (e.getMessage ());
			isException = true;
			resultDTO = new ResultDTO (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		} finally {
			if (resultDTO == null) resultDTO = new ResultDTO (StatusCode.BASIC_ERROR);

			if (isException && resultDTO.getCode () != 200) {
				addTxHashLog (lmDTO, LeisureMetaApiType.TRANSFER_NFT, resultDTO);
			} else if (!isException && resultDTO.getCode () != 200) {
				Map<String, Object> map;
				try {
					map = objectMapper.readValue (resultDTO.getMessage (), Map.class);
					resultDTO.setMessage (((Map) map.get ("value")).get ("msg").toString ());
				} catch (JsonProcessingException e) {
					resultDTO.setMessage (e.getMessage ());
				}
			}
			addNftTradeInfoLog (lmDTO, swapAt);

			if (resultDTO.getCode () == 200) {
				param.clear ();
				param.put ("NFTITM_SN", lmDTO.getNftitmSn ());
				param.put ("USER_SN", nommUserSn);
				param.put ("TXHASH_SN", lmDTO.getTxHashLogData ().get ("TXHASH_SN"));
				param.put ("SIGN_TXHASH", lmDTO.getTxHash ());
				if (!swapAt) {
					param.put ("OWNER_USER_SN", nommUserSn);
				} else {
					param.put ("OWNER_USER_SN", null);
					param.put ("DELETE_AT", "Y"); // FIXME : 자동 burn 되나?
				}
				blockchainMapper.updateTncnNftitmInfo (param);

				// DTO 변환
				LmTxDTO lmTxDTO = new LmTxDTO ();
				lmTxDTO.setTransactionHash ((String) resultDTO.getData ());
				resultDTO.setData (lmTxDTO);
			}
		}

		return resultDTO;
	}

	@Override
	public ResultDTO swap (BlockchainDTO blockchainDTO) {
		LmDTO lmDTO;
		try {
			lmDTO = (LmDTO) setData (blockchainDTO);
 			if (!validateGatewayAddress (lmDTO)) {
				KeyPairDTO keyPairDTO = getKeyPair (lmDTO.getPrivateKey ());
				keyPairDTO.setAddress (lmDTO.getToAddress (true));
				updateAccount (lmDTO.getFromAddress (true), keyPairDTO);
			}
			String gtwyAdres = commonMapper.selectGtwyBass (lmDTO.getGtwySeCode ()).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_GATEWAY_INFO);
			});
			lmDTO.setGtwyAdres (gtwyAdres);

			Map<String, Object> walletParam = new HashMap<> ();
			walletParam.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			walletParam.put ("SYS_ID", "CXWL");
			walletParam.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			Map<String, Object> walletInfo = blockchainMapper.selectUserCxwaletInfo (walletParam).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_INFO);
			});
			lmDTO.setToAddress ((String) walletInfo.get ("CXWALET_ADRES"));

			ResultDTO swapDTO = getSwapFee (lmDTO);
			lmDTO.setCxFeeQty (new BigDecimal (String.valueOf (swapDTO.getData ())));

			ErcDTO ercDTO = new ErcDTO ();
			ercDTO.setNetworkId (lmDTO.getToNetworkId ());
			ercDTO.setSymbol (lmDTO.getSymbol ());
			ercDTO.setUserCmmnSn (lmDTO.getUserCmmnSn ());
			ResultDTO receiverBalanceDTO = balance (ercDTO);
			BalanceDTO balanceDTO = (BalanceDTO) receiverBalanceDTO.getData ();
			lmDTO.setReceiverTotalAmount (balanceDTO.getBalance ()); // 수신자 현재 밸런스 저장
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return transfer (lmDTO, true);
	}

	@Override
	public ResultDTO nftSwap (BlockchainDTO blockchainDTO) {
		LmDTO lmDTO;
		try {
			lmDTO = (LmDTO) setData (blockchainDTO);
			if (!validateGatewayAddress (lmDTO)) {
				KeyPairDTO keyPairDTO = getKeyPair (lmDTO.getPrivateKey ());
				keyPairDTO.setAddress (lmDTO.getToAddress (true));
				updateAccount (lmDTO.getFromAddress (true), keyPairDTO);
			}
			String gtwyAdres = commonMapper.selectGtwyBass (lmDTO.getGtwySeCode ()).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_GATEWAY_INFO);
			});
			lmDTO.setGtwyAdres (gtwyAdres);

			Map<String, Object> walletParam = new HashMap<> ();
			walletParam.put ("USER_CMMN_SN", blockchainDTO.getUserCmmnSn ());
			walletParam.put ("SYS_ID", "CXWL");
			walletParam.put ("BLC_NTWRK_ID", blockchainDTO.getToNetworkId ());
			Map<String, Object> walletInfo = blockchainMapper.selectUserCxwaletInfo (walletParam).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_INFO);
			});
			lmDTO.setToAddress ((String) walletInfo.get ("CXWALET_ADRES"));

			ResultDTO swapDTO = getSwapFee (lmDTO);
			lmDTO.setCxFeeQty (new BigDecimal (String.valueOf (swapDTO.getData ())));
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return nftTransfer (lmDTO, true);
	}

	@Override
	public boolean whetherOwnedAssets (Integer userCmmnSn) {
		boolean result = false;
		try {
			LmDTO lmDTO = new LmDTO ();
			lmDTO.setNetworkId ("1000");
			lmDTO.setSymbol ("LM");
			lmDTO.setUserCmmnSn (userCmmnSn);
			lmDTO = (LmDTO) setData (lmDTO);

			ResultDTO balanceDTO = balance (lmDTO);
			Map<String, Object> param = new HashMap<> ();
			param.put ("USER_CMMN_SN", lmDTO.getUserCmmnSn ());
			List<LmcNftsDTO> nftitmList = blockchainMapper.selectTncnNftitmList (param);
			if (((BalanceDTO) balanceDTO.getData ()).getBalance ().compareTo (BigDecimal.ZERO) == 1 || !ObjectUtils.isEmpty (nftitmList)) result = true;
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return result;
	}

	public ResultDTO addPublicKeySummaries (String uuid, KeyPairDTO keyPairDTO) {
		ResultDTO resultDTO;

		try {
			Map<String, Object> dataParam = new HashMap<> ();
			dataParam.put ("createdAt", CommonUtil.bcNow ().toString ());
			dataParam.put ("account", uuid);

			Map<String, Object> summaries = new HashMap<> ();
			summaries.put (keyPairDTO.getAddress (), "");
			dataParam.put ("summaries", summaries);

			LmDTO lmDTO = new LmDTO ();
			lmDTO.setNetworkId ("1000");
			Map<String, Object> networkInfo = commonMapper.selectBlcNtwkBass (lmDTO.getNetworkId ()).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_NETWORK_INFO);
			});
			lmDTO.setRpcUrl (networkInfo.get ("BLC_RPC_URL").toString ());
			lmDTO.setFromAddress (uuid);
			lmDTO.setSignUserUuid ("playnomm");
			lmDTO.setPrivateKey (manageKeyService.joinMngrPrivateKey ("playnomm"));

			resultDTO = transaction (lmDTO, LeisureMetaApiType.ADD_PUBLICKEY_SUMMARIES, dataParam);
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}

		return resultDTO;
	}

	private ResultDTO getNfts (LmDTO lmDTO) throws JsonProcessingException {
		// LMC 조회 후 진행
		//		Map<String, Object> param = new HashMap<> ();
		//		param.put ("movable", "free");
		//		param.put ("pathVariable", lmDTO.getFromAddress (true));
		//		ResultDTO resultDTO = HttpUtil.send (lmDTO.getRpcUrl (), LeisureMetaApiType.NFT_BALANCE, param);
		//		addTxHashLog (lmDTO, LeisureMetaApiType.NFT_BALANCE, resultDTO);
		//
		//		Map<String, Object> data = objectMapper.readValue (String.valueOf (resultDTO.getData ()), Map.class);
		//		List<String> tokenIdList = new ArrayList<> (data.keySet ());
		//		if (!ObjectUtils.isEmpty (tokenIdList)) {
		//			param.put ("tokenIdList", tokenIdList);
		//			param.put ("OWNER_USER_SN", lmDTO.getUserSn ());
		//			List<LmcNftDTO> nftitmList = blockchainMapper.selectTncnNftitmList (param);
		//			if (ObjectUtils.isEmpty (nftitmList)) throw new WalletException (StatusCode.NO_NFTS_DATA);
		//			else nftitmList.stream ().forEach (nft -> nft.setNftitmCntntsThumbFilePath (awsCdnPrefix + nft.getNftitmCntntsThumbFilePath ()));
		//			resultDTO.setData (nftitmList);
		//		} else {
		//			throw new WalletException (StatusCode.NO_NFTS_DATA);
		//		}
		//		return resultDTO;

		// DB 조회
		Map<String, Object> param = new HashMap<> ();
		param.put ("USER_CMMN_SN", lmDTO.getUserCmmnSn ());
		List<LmcNftsDTO> nftitmList = blockchainMapper.selectTncnNftitmList (param);
		if (!ObjectUtils.isEmpty (nftitmList)) nftitmList.stream ().forEach (nft -> nft.setNftitmCntntsThumbFilePath (awsCdnPrefix + nft.getNftitmCntntsThumbFilePath ()));
		return new ResultDTO (StatusCode.ACCESS, nftitmList);
	}

	private List<Map<String, Object>> getSignedRequestBody (LmDTO lmDTO, List<Map<String, Object>> requestBody) {
		ECKeyPair keyPair = getECKeyPair (lmDTO.getPrivateKey ());
		BigInteger txHashHex = new BigInteger (lmDTO.getTxHash (), 16);
		byte[] hexMessageHex = txHashHex.toByteArray ();

		// BigInteger parsing 오류 처리
		if (hexMessageHex.length == 33) {
			byte[] test = new byte[32];
			for (int j = 1; j < hexMessageHex.length; j++) {
				test[j - 1] = hexMessageHex[j];
			}
			hexMessageHex = test;
		}

		Sign.SignatureData signMessageHex = Sign.signMessage (hexMessageHex, keyPair, false);
		byte[] signVHex = signMessageHex.getV ();
		byte[] signRHex = signMessageHex.getR ();
		byte[] signSHex = signMessageHex.getS ();

		Map<String, Object> sig = new HashMap<> ();
		Map<String, Object> sigRequest = new HashMap<> ();

		sigRequest.put ("v", signVHex[0]);
		sigRequest.put ("r", new BigInteger (1, signRHex).toString (16));
		sigRequest.put ("s", new BigInteger (1, signSHex).toString (16));

		sig.put ("sig", sigRequest);
		sig.put ("account", lmDTO.getSignUserUuid ());

		List<Map<String, Object>> requestArray = new ArrayList<> ();
		Map<String, Object> request = new HashMap<> ();
		request.put ("sig", sig);
		request.put ("value", requestBody.get (0));
		requestArray.add (request);

		return requestArray;
	}

	private List<Map<String, Object>> getRequestBody (LmDTO lmDTO, Map<String, Object> dataParam, LeisureMetaApiType leisureMetaApiType) {
		List<Map<String, Object>> requestBody = new ArrayList<> ();
		Map<String, Object> requestForm = new HashMap<> ();
		Map<String, Object> txForm = new HashMap<> ();
		Map<String, Object> apiForm = new HashMap<> ();
		apiForm.put ("networkId", lmDTO.getNetworkId ());
		apiForm.putAll (dataParam);
		txForm.put (leisureMetaApiType.getApiName (), apiForm);
		requestForm.put (leisureMetaApiType.getTxName (), txForm);
		requestBody.add (requestForm);
		return requestBody;
	}

	private ResultDTO transaction (LmDTO lmDTO, LeisureMetaApiType leisureMetaApiType, Map<String, Object> dataParam) throws JsonProcessingException {
		//txhash
		leisureMetaApiType.setUri ("/txhash");
		List<Map<String, Object>> requestBody = getRequestBody (lmDTO, dataParam, leisureMetaApiType);
		ResultDTO txhashDTO = HttpUtil.send (lmDTO.getRpcUrl (), leisureMetaApiType, requestBody);
		if (txhashDTO.getCode () != 200) throw new WalletException (txhashDTO.getCode (), txhashDTO.getMessage ());
		lmDTO.setTxHash ((String) (objectMapper.readValue ((String) txhashDTO.getData (), List.class)).get (0));
		addTxHashLog (lmDTO, leisureMetaApiType, txhashDTO, requestBody);
		//signed hash
		leisureMetaApiType.setUri ("/tx");
		List<Map<String, Object>> signedRequestBody = getSignedRequestBody (lmDTO, requestBody);
		ResultDTO resultDTO = HttpUtil.send (lmDTO.getRpcUrl (), leisureMetaApiType, signedRequestBody);
		String txHash = null;
		if (resultDTO.getCode () == 200) {
			List<String> txHashList = objectMapper.readValue ((String) resultDTO.getData (), List.class);
			txHash = txHashList.get (0);
			lmDTO.setTxHash (txHash);
			resultDTO.setData (txHash);
		} else {
			Map<String, Object> data = objectMapper.readValue (resultDTO.getMessage (), Map.class);
			resultDTO = new ResultDTO (resultDTO.getCode (), (String) ((Map) data.get ("value")).get ("msg"), objectMapper.writeValueAsString (data));
		}
		addTxHashLog (lmDTO, leisureMetaApiType, resultDTO, signedRequestBody);
		return resultDTO;
	}

	public ResultDTO updateAccount (String uuid, KeyPairDTO keyPairDTO) {
		ResultDTO resultDTO;

		try {
			Map<String, Object> dataParam = new HashMap<> ();
			dataParam.put ("createdAt", CommonUtil.bcNow ().toString ());
			dataParam.put ("account", uuid);
			dataParam.put ("ethAddress", keyPairDTO.getAddress ());
			dataParam.put ("guardian", "playnomm");

			LmDTO lmDTO = new LmDTO ();
			lmDTO.setNetworkId ("1000");
			Map<String, Object> networkInfo = commonMapper.selectBlcNtwkBass (lmDTO.getNetworkId ()).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_NETWORK_INFO);
			});
			lmDTO.setRpcUrl (networkInfo.get ("BLC_RPC_URL").toString ());
			lmDTO.setFromAddress (uuid);
			lmDTO.setSignUserUuid (uuid);
			lmDTO.setPrivateKey (keyPairDTO.getPrivateKey ());

			resultDTO = transaction (lmDTO, LeisureMetaApiType.UPDATE_ACCOUNT, dataParam);
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}

		return resultDTO;
	}

	public ResultDTO getSwapFee (LmDTO lmDTO) {
		ResultDTO resultDTO;

		try {
			Map<String, Object> param = new HashMap<> ();
			param.put ("STD_CODE_GROUP_ID", "TRADE_TY_DETAIL_CODE");
			param.put ("STD_CODE", "FP37");
			param.put ("LANG_CODE", lmDTO.getLangCode ());
			Map<String, Object> codeInfo = commonMapper.selectStdCodeInfo (param).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_COMMON_CODE_INFO);
			});
			resultDTO = new ResultDTO (StatusCode.ACCESS, codeInfo.get ("REFRN_3_VALUE"));
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}

		return resultDTO;
	}
}
