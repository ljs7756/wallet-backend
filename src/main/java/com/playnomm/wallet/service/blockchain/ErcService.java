package com.playnomm.wallet.service.blockchain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.blockchain.BlockchainDTO;
import com.playnomm.wallet.dto.blockchain.ErcDTO;
import com.playnomm.wallet.dto.blockchain.LmDTO;
import com.playnomm.wallet.dto.blockchain.response.*;
import com.playnomm.wallet.enums.EthereumApiType;
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
import org.web3j.contracts.eip20.generated.ERC20;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticEIP1559GasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

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
public class ErcService extends AbstractBlockchainService {
	@Value("${ethereum.api.key.secret}")
	private       String             apiKeySecret;
	@Value("${ethereum.api.nft.url}")
	private       String             nftUrl;
	@Value("${etherscan.api.key}")
	private       String             etherscanApiKey;
	@Value("${etherscan.api.url}")
	private       String             etherscanApiUrl;
	@Value("${bscscan.api.key}")
	private       String             bscscanApiKey;
	@Value("${bscscan.api.url}")
	private       String             bscscanApiUrl;
	@Value("${ipfs.url}")
	private       String             ipfsUrl;
	@Value("${cloud.aws.cdn.prefix}")
	private       String             cloudAwsCdnPrefix;
	@Value("${image.nft.default.path}")
	private       String             nftDefaultPath;
	@Value("${image.nft.default.detail.path}")
	private       String             nftDefaultDetailPath;

	@Autowired
	protected ErcService (AwsService awsService, BlockchainMapper blockchainMapper, CommonMapper commonMapper, @Value ("${ethereum.api.key}") String apiKey, PnAuthService pnAuthService, ManageKeyService manageKeyService) {
		super (awsService, blockchainMapper, commonMapper, apiKey, pnAuthService, manageKeyService);
	}

	@Override
	public ResultDTO transfer (BlockchainDTO blockchainDTO) {
		return transfer (blockchainDTO, false);
	}

	public ResultDTO transfer (BlockchainDTO blockchainDTO, boolean swapAt) {
		ResultDTO resultDTO = null;

		ErcDTO ercDTO = null;
		TransactionReceipt transactionReceipt = null;
		try {
			LocalDateTime now = CommonUtil.now (); // 트랜잭션 전송 시간

			if (!swapAt) ercDTO = (ErcDTO) setData (blockchainDTO);
			else ercDTO = (ErcDTO) blockchainDTO;
			ercDTO.setCreatDt (CommonUtil.dbNow (now));

			if (ercDTO.getFromAddress ().equalsIgnoreCase (ercDTO.getToAddress ())) throw new WalletException (StatusCode.CHECK_RECEIVER_ADDRESS);

			ResultDTO balanceDTO = getBalance (ercDTO);
			BigDecimal totalAmount = (BigDecimal) balanceDTO.getData ();
			if (totalAmount.compareTo (BigDecimal.ZERO) == 0) throw new WalletException (StatusCode.BALANCE_NOT_FOUND);
			if (totalAmount.compareTo (ercDTO.getAmount ().add (ercDTO.getCxFeeQty ())) == -1) throw new WalletException (StatusCode.NOT_ENOUGH_BALANCE);
			ercDTO.setTotalAmount (totalAmount); // 현재 밸런스 저장

			if (!swapAt) {
				String fromAddress = ercDTO.getFromAddress ();
				ercDTO.setFromAddress (ercDTO.getToAddress ());
				ResultDTO receiverBalanceDTO = getBalance (ercDTO);
				ercDTO.setFromAddress (fromAddress);
				ercDTO.setReceiverTotalAmount ((BigDecimal) receiverBalanceDTO.getData ()); // 수신자 현재 밸런스 저장
			}
			Web3j web3j = ercDTO.getWeb3j ();
			Credentials credentials = Credentials.create (ercDTO.getPrivateKey ());

			BigInteger gasLimit = ercDTO.getBlcNtwrkBassTknAt ().equals ("Y") ? ercDTO.ETH_GAS_LIMIT : ercDTO.ETH_ERC_GAS_LIMIT;
			GasTrackerDTO gasTrackerDTO = gasTracker (ercDTO);
			BigInteger gasPrice;
			int priority = ercDTO.getPriority ().intValue ();
			if (priority == 1) gasPrice = gasTrackerDTO.getSafeGasPrice ();
			else if (priority == 2) gasPrice = gasTrackerDTO.getPropseGasPrice ();
			else gasPrice = gasTrackerDTO.getFastGasPrice ();
			if (blockchainDTO.getNetworkId ().equals ("1001")) {
				BigDecimal baseFeePerGas, maxPriorityFeePerGas, maxFeePerGas;
				if (blockchainDTO.getChainId () == 5) { // testnet
					EthBlock ethBlock = ercDTO.getWeb3j ().ethGetBlockByNumber (DefaultBlockParameterName.PENDING, true).send ();
					baseFeePerGas = Convert.fromWei (ethBlock.getBlock ().getBaseFeePerGas ().toString (), Convert.Unit.GWEI);
					maxPriorityFeePerGas = Convert.toWei (ercDTO.getPriority ().toString (), Convert.Unit.GWEI);
				} else {
					baseFeePerGas = gasTrackerDTO.getSugestGasPrice ();
					maxPriorityFeePerGas = Convert.toWei (gasPrice.subtract (baseFeePerGas.toBigInteger ()).toString (), Convert.Unit.GWEI);
				}
				maxFeePerGas = Convert.toWei (baseFeePerGas.multiply (BigDecimal.valueOf (2)).toPlainString (), Convert.Unit.GWEI).add (maxPriorityFeePerGas);

				try {
					if ("Y".equals (ercDTO.getBlcNtwrkBassTknAt ())) {
						transactionReceipt = Transfer.sendFundsEIP1559 (web3j, credentials, ercDTO.getToAddress (), ercDTO.getAmount (), Convert.Unit.ETHER, gasLimit, maxPriorityFeePerGas.toBigInteger (), maxFeePerGas.toBigInteger ()).send ();
					} else {
						ContractGasProvider contractGasProvider = new StaticEIP1559GasProvider (ercDTO.getChainId ().longValue (), maxFeePerGas.toBigInteger (), maxPriorityFeePerGas.toBigInteger (), gasLimit);
						ERC20 erc20 = ERC20.load (ercDTO.getContractAddress (), web3j, credentials, contractGasProvider);
						transactionReceipt = erc20.transfer (ercDTO.getToAddress (), ConvertUtil.toBigInteger (ercDTO.getAmount (), ercDTO.getDcmlpointLt ())).send ();
					}
				} catch (Exception e) {
					throw new Exception (e.getMessage ());
				}
			} else if (blockchainDTO.getNetworkId ().equals ("1002")) {
				if (blockchainDTO.getChainId () == 56) gasPrice = BigInteger.TEN; // testnet
				try {
					if ("Y".equals (ercDTO.getBlcNtwrkBassTknAt ())) {
						Transfer transfer = new Transfer (web3j, new RawTransactionManager (web3j, credentials, ercDTO.getChainId ().longValue ()));
						transactionReceipt = transfer.sendFunds (ercDTO.getToAddress (), ercDTO.getAmount (), Convert.Unit.ETHER, gasPrice, gasLimit).send ();
					} else {
						ERC20 erc20 = ERC20.load (ercDTO.getContractAddress (), web3j, credentials, new StaticGasProvider (gasPrice, gasLimit));
						transactionReceipt = erc20.transfer (ercDTO.getToAddress (), ConvertUtil.toBigInteger (ercDTO.getAmount (), ercDTO.getDcmlpointLt ())).send ();
					}
				} catch (Exception e) {
					throw new Exception (e.getMessage ());
				}
			}
			resultDTO = new ResultDTO<> (StatusCode.ACCESS, transactionReceipt);
			BigDecimal gasUsed = new BigDecimal (transactionReceipt.getGasUsed ().toString ());
			BigDecimal effectiveGasPrice;
			try {
				effectiveGasPrice = Convert.fromWei (new BigInteger (transactionReceipt.getEffectiveGasPrice ().replace ("0x", ""), 16).toString (), Convert.Unit.ETHER);
			} catch (NumberFormatException e) {
				effectiveGasPrice = Convert.fromWei (new BigDecimal (gasPrice.toString ()), Convert.Unit.ETHER);
			}
			ercDTO.setCxFeeQty (effectiveGasPrice.multiply (gasUsed));
			ercDTO.setTxHash (transactionReceipt.getTransactionHash ().replace ("0x", ""));
		} catch (WalletException we) {
			log.error (we.getMessage ());
			resultDTO = new ResultDTO (we.getCode (), we.getMessage ());
		} catch (Exception e) {
			log.error (e.getMessage ());
			resultDTO = new ResultDTO (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		} finally {
			if (resultDTO == null) resultDTO = new ResultDTO (StatusCode.BASIC_ERROR);

			addTxHashLog (ercDTO, EthereumApiType.TRANSFER, resultDTO);
			addTradeInfoLog (ercDTO, swapAt);

			if (resultDTO.getCode () == 200) {
				// DTO 변환
				ErcTxDTO ercTxDTO = new ErcTxDTO ();
				ercTxDTO.setTransactionHash (transactionReceipt.getTransactionHash ());
				ercTxDTO.setBlockHash (transactionReceipt.getBlockHash ());
				ercTxDTO.setBlockNumber (transactionReceipt.getBlockNumber ());
				ercTxDTO.setGasUsed (transactionReceipt.getGasUsed ());
				ercTxDTO.setFrom (transactionReceipt.getFrom ());
				ercTxDTO.setTo (transactionReceipt.getTo ());
				resultDTO.setData (ercTxDTO);
			}
		}

		return resultDTO;
	}

	@Override
	public ResultDTO nfts (BlockchainDTO blockchainDTO) {
		ResultDTO resultDTO;
		try {
			ErcDTO ercDTO = (ErcDTO) setData (blockchainDTO);
			resultDTO = getNfts (ercDTO);
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
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
			ErcDTO ercDTO = (ErcDTO) setData (blockchainDTO);
			Map<String, Object> param = new HashMap<> ();
			param.put ("chainId", ercDTO.getChainId ());
			param.put ("tokenAddress", ercDTO.getContractAddress ());
			param.put ("tokenId", ercDTO.getTokenId ());
			Map<String, Object> headers = new HashMap<> ();
			headers.put ("Authorization", "Basic " + new String (Base64.getEncoder ().encode ((apiKey + ":" + apiKeySecret).getBytes ())));

			resultDTO = HttpUtil.send (nftUrl, EthereumApiType.NFT, param, headers);
			if (resultDTO.getCode () != 200) {
				String responseBody = resultDTO.getMessage ();
				Map<String, Object> map = objectMapper.readValue (responseBody, Map.class);
				resultDTO.setMessage ((String) map.get ("message"));
			} else {
				Map<String, Object> data = objectMapper.readValue ((String) resultDTO.getData (), Map.class);
				ErcNftDTO ercNftDTO = new ErcNftDTO ();
				ercNftDTO.setContract ((String) data.get ("contract"));
				ercNftDTO.setTokenId ((String) data.get ("tokenId"));
				Map<String, Object> metadata = (Map) data.get ("metadata");
				String name, image;
				if (!ObjectUtils.isEmpty (metadata)) {
					name = (String) metadata.get ("name");
					if (ObjectUtils.isEmpty (name)) name = "Untitled#" + ercNftDTO.getTokenId ();
					else name += "#" + ercNftDTO.getTokenId ();

					image = (String) metadata.get ("image");
					if (ObjectUtils.isEmpty (image)) image = cloudAwsCdnPrefix + nftDefaultDetailPath;
					else if (image.contains ("ipfs")) image = ipfsUrl + image.replaceAll ("ipfs/", "").replaceAll ("ipfs://", "");

					ercNftDTO.setDescription ((String) metadata.get ("description"));
					ercNftDTO.setExternalUrl ((String) metadata.get ("external_url"));
				} else {
					name = "Untitled#" + ercNftDTO.getTokenId ();
					image = cloudAwsCdnPrefix + nftDefaultDetailPath;
				}
				ercNftDTO.setName (name);
				ercNftDTO.setImage (image);

				// 최근 거래 내역 조회
				Map<String, Object> ercNftTradeInfo = blockchainMapper.selectErcNftTradeInfo (Map.of ("NFT_TKN_ID", ercNftDTO.getTokenId (), "USER_CMMN_SN", ercDTO.getUserCmmnSn ())).orElse (Map.of ());
				if (!ObjectUtils.isEmpty (ercNftTradeInfo)) {
					ercNftDTO.setCxTradeSn ((Integer) ercNftTradeInfo.get ("CX_TRADE_SN"));
					ercNftDTO.setTradeOccrrncDt ((LocalDateTime) ercNftTradeInfo.get ("TRADE_OCCRRNC_DT"));
					ercNftDTO.setTrsmtrCxwaletAdres ((String) ercNftTradeInfo.get ("TRSMTR_CXWALET_ADRES"));
					ercNftDTO.setSignTxhash ((String) ercNftTradeInfo.get ("SIGN_TXHASH"));
				} else {
					ercNftDTO.setCxTradeSn (null);
				}

				resultDTO.setData (ercNftDTO);
			}
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return resultDTO;
	}

	@Override
	public ResultDTO nftTransfer (BlockchainDTO blockchainDTO) {
		return null;
	}

	@Override
	public ResultDTO swap (BlockchainDTO blockchainDTO) {
		ErcDTO ercDTO;
		try {
			ercDTO = (ErcDTO) setData (blockchainDTO);
			String gtwyAdres = commonMapper.selectGtwyBass (blockchainDTO.getGtwySeCode ()).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_GATEWAY_INFO);
			});
			blockchainDTO.setGtwyAdres (gtwyAdres);

			Map<String, Object> walletParam = new HashMap<> ();
			walletParam.put ("USER_CMMN_SN", ercDTO.getUserCmmnSn ());
			walletParam.put ("SYS_ID", "CXWL");
			walletParam.put ("BLC_NTWRK_ID", ercDTO.getToNetworkId ());
			Map<String, Object> walletInfo = blockchainMapper.selectUserCxwaletInfo (walletParam).orElseThrow (() -> {
				throw new WalletException (StatusCode.NO_WALLET_INFO);
			});
			ercDTO.setToAddress ((String) walletInfo.get ("CXWALET_ADRES"));

			GasTrackerDTO gasTrackerDTO = gasTracker (ercDTO);
			BigInteger gasPrice;
			int priority = ercDTO.getPriority ().intValue ();
			if (priority == 1) gasPrice = gasTrackerDTO.getSafeGasPrice ();
			else if (priority == 2) gasPrice = gasTrackerDTO.getPropseGasPrice ();
			else gasPrice = gasTrackerDTO.getFastGasPrice ();
			ercDTO.setCxFeeQty (new BigDecimal (gasPrice));

			LmDTO lmDTO = new LmDTO ();
			lmDTO.setNetworkId (ercDTO.getToNetworkId ());
			lmDTO.setSymbol ("LM");
			lmDTO.setUserCmmnSn (ercDTO.getUserCmmnSn ());
			ResultDTO receiverBalanceDTO = balance (lmDTO);
			BalanceDTO balanceDTO = (BalanceDTO) receiverBalanceDTO.getData ();
			ercDTO.setReceiverTotalAmount (balanceDTO.getBalance ()); // 수신자 현재 밸런스 저장
		} catch (WalletException we) {
			log.error (we.getMessage ());
			throw we;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return transfer (ercDTO, true);
	}

	@Override
	public ResultDTO nftSwap (BlockchainDTO blockchainDTO) {
		return null;
	}

	@Override
	public boolean whetherOwnedAssets (Integer userCmmnSn) {
		boolean result = false;
		try {
			List<Map<String, Object>> userCxwaletTknInfoList = blockchainMapper.selectUserCxwaletTknInfoList (userCmmnSn);
			String networkId;
			for (Map<String, Object> userCxwaletTknInfo : userCxwaletTknInfoList) {
				networkId = (String) userCxwaletTknInfo.get ("BLC_NTWRK_ID");
//				if ("1000".equals (networkId)) continue; // FIXME : BNB 활성화시 수정
				if ("1001".equals (networkId)) {
					ErcDTO ercDTO = new ErcDTO ();
					ercDTO.setUserCmmnSn (userCmmnSn);
					ercDTO.setNetworkId (networkId);
					ercDTO.setSymbol ((String) userCxwaletTknInfo.get ("CX_SYMBOL_CODE"));
					ercDTO = (ErcDTO) setData (ercDTO);
					ResultDTO balanceDTO = balance (ercDTO);
					ResultDTO nftsDTO = getNfts (ercDTO);
					if (((BalanceDTO) balanceDTO.getData ()).getBalance ().compareTo (BigDecimal.ZERO) == 1 || !ObjectUtils.isEmpty (nftsDTO.getData ())) {
						result = true;
						break;
					}
				}
			}
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return result;
	}

	public GasTrackerDTO gasTracker (BlockchainDTO blockchainDTO) throws JsonProcessingException {
		//		GasTrackerDTO gasTrackerDTO = new GasTrackerDTO ();
		//		ResultDTO resultDTO;
		//		Map<String, Object> param = new HashMap<> ();
		//		if ("1001".equals (blockchainDTO.getNetworkId ())) {
		//			param.put ("apiKey", etherscanApiKey);
		//			resultDTO = HttpUtil.send (etherscanApiUrl, EthereumApiType.ETH_GAS_TRACKER, param);
		//		} else {
		//			param.put ("apiKey", bscscanApiKey);
		//			resultDTO = HttpUtil.send (bscscanApiUrl, EthereumApiType.BNB_GAS_TRACKER, param);
		//		}
		//
		//		Map<String, Object> data = objectMapper.readValue ((String) resultDTO.getData (), Map.class);
		//		Map<String, Object> result = (Map) data.get ("result");
		//		gasTrackerDTO.setLastBlck (new BigInteger ((String) result.get ("LastBlock")));
		//		gasTrackerDTO.setSafeGasPrice (new BigInteger ((String) result.get ("SafeGasPrice")));
		//		gasTrackerDTO.setPropseGasPrice (new BigInteger ((String) result.get ("ProposeGasPrice")));
		//		gasTrackerDTO.setFastGasPrice (new BigInteger ((String) result.get ("FastGasPrice")));
		//		if (!ObjectUtils.isEmpty (result.get ("suggestBaseFee"))) gasTrackerDTO.setSugestGasPrice (new BigDecimal ((String) result.get ("suggestBaseFee")).toBigInteger ());
		//		return gasTrackerDTO;

		return blockchainMapper.selectBlcNtwrkGasInfo (blockchainDTO.getNetworkId ()).orElseThrow (() -> {throw new WalletException (StatusCode.NO_GAS_TRACKER_INFO);});
	}

	public ResultDTO contractInfo (ErcDTO ercDTO) {
		ResultDTO resultDTO;
		try {
			ercDTO = (ErcDTO) setData (ercDTO);
			ERC20 erc20 = ERC20.load (ercDTO.getContractAddress (), ercDTO.getWeb3j (), Credentials.create (ercDTO.getPrivateKey ()), new DefaultGasProvider ());
			ErcTokenDTO ercTokenDTO = new ErcTokenDTO ();
			ercTokenDTO.setSymbol (erc20.symbol ().send ());
			ercTokenDTO.setName (erc20.name ().send ());
			ercTokenDTO.setDecimals (erc20.decimals ().send ());
			ercTokenDTO.setTotalSupply (ConvertUtil.toBigDecimal (erc20.totalSupply ().send ().toString (), ercDTO.getDcmlpointLt ()).toBigInteger ());
			resultDTO = new ResultDTO (StatusCode.ACCESS, ercTokenDTO);
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
		} catch (Exception e) {
			log.error (e.getMessage ());
			//			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
			throw new WalletException (StatusCode.NO_CONTRACT_INFORMATION);
		}
		return resultDTO;
	}

	private ResultDTO getNfts (ErcDTO ercDTO) {
		Map<String, Object> param = new HashMap<> ();
		param.put ("chainId", ercDTO.getChainId ());
		param.put ("walletAddress", ercDTO.getFromAddress ());
		Map<String, Object> headers = new HashMap<> ();
		headers.put ("Authorization", "Basic " + new String (Base64.getEncoder ().encode ((apiKey + ":" + apiKeySecret).getBytes ())));

		ResultDTO resultDTO = HttpUtil.send (nftUrl, EthereumApiType.NFTS, param, headers);
		try {
			Map<String, Object> data = objectMapper.readValue ((String) resultDTO.getData (), Map.class);
			List<Map<String, Object>> assets = (List) data.get ("assets");
			List<ErcNftsDTO> nfts = new ArrayList<> ();
			assets.stream ().forEach (m -> {
				ErcNftsDTO ercNftsDTO = new ErcNftsDTO ();
				ercNftsDTO.setContract ((String) m.get ("contract"));
				ercNftsDTO.setTokenId ((String) m.get ("tokenId"));
				ercNftsDTO.setType ((String) m.get ("type"));
				Map<String, Object> metadata = (Map) m.get ("metadata");
				String name, image;
				if (!ObjectUtils.isEmpty (metadata)) {
					name = (String) metadata.get ("name");
					if (ObjectUtils.isEmpty (name)) name = "Untitled#" + ercNftsDTO.getTokenId ();
					else name += "#" + ercNftsDTO.getTokenId ();

					image = (String) metadata.get ("image");
					if (ObjectUtils.isEmpty (image)) image = cloudAwsCdnPrefix + nftDefaultPath;
					else if (image.contains ("ipfs")) image = ipfsUrl + image.replaceAll ("ipfs/", "").replaceAll ("ipfs://", "");
				} else {
					name = "Untitled#" + ercNftsDTO.getTokenId ();
					image = cloudAwsCdnPrefix + nftDefaultPath;
				}
				ercNftsDTO.setName (name);
				ercNftsDTO.setImage (image);

				nfts.add (ercNftsDTO);
			});
			resultDTO.setData (nfts);
		} catch (Exception e) {
		}
		return resultDTO;
	}

	/**
	 * FT 전송 수수료
	 * @param ercDTO
	 * @return
	 */
	public ResultDTO getTransferFee (ErcDTO ercDTO) {
		ResultDTO resultDTO;
		try {
			ercDTO = (ErcDTO) setData (ercDTO);

			GasTrackerDTO gasTrackerDTO = gasTracker (ercDTO);
			BigInteger gasPrice;
			int priority = ercDTO.getPriority ().intValue ();
			if (priority == 1) gasPrice = gasTrackerDTO.getSafeGasPrice ();
			else if (priority == 2) gasPrice = gasTrackerDTO.getPropseGasPrice ();
			else gasPrice = gasTrackerDTO.getFastGasPrice ();
			BigInteger gasLimit = ercDTO.getBlcNtwrkBassTknAt ().equals ("Y") ? ercDTO.ETH_GAS_LIMIT : ercDTO.ETH_ERC_GAS_LIMIT;

			ErcTxFeeDTO ercTxFeeDTO = new ErcTxFeeDTO ();
			if ("1001".equals (ercDTO.getNetworkId ())) {
				BigDecimal baseFeePerGas, maxPriorityFeePerGas, maxFeePerGas;
				if (ercDTO.getChainId () == 5) { // testnet
					EthBlock ethBlock = ercDTO.getWeb3j ().ethGetBlockByNumber (DefaultBlockParameterName.PENDING, true).send ();
					baseFeePerGas = Convert.fromWei (ethBlock.getBlock ().getBaseFeePerGas ().toString (), Convert.Unit.GWEI);
					maxPriorityFeePerGas = BigDecimal.valueOf (ercDTO.getPriority ());
					gasPrice = baseFeePerGas.add (maxPriorityFeePerGas).toBigInteger ();
				} else {
					baseFeePerGas = gasTrackerDTO.getSugestGasPrice ();
					maxPriorityFeePerGas = new BigDecimal (gasPrice.subtract (baseFeePerGas.toBigInteger ()));
				}
				maxFeePerGas = baseFeePerGas.multiply (BigDecimal.valueOf (2)).add (maxPriorityFeePerGas);

				BigDecimal fee = Convert.fromWei (gasPrice.multiply (gasLimit).toString (), Convert.Unit.GWEI);
				BigDecimal maxFee = Convert.fromWei (maxFeePerGas.multiply (BigDecimal.valueOf (gasLimit.longValue ())).toPlainString (), Convert.Unit.GWEI);

				System.out.println (fee.toPlainString ());
				System.out.println (maxFee.toPlainString ());

				ercTxFeeDTO.setFee (fee);
				ercTxFeeDTO.setMaxFee (maxFee);

				Map<String, Object> exrBass = commonMapper.selectExrBass ("ETH").orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_EXR_BASS);
				});
				BigDecimal usdExr = (BigDecimal) exrBass.get ("CX_EXR_USD_CRRNCY_AMT");
				ercTxFeeDTO.setUsdFee (fee.multiply (usdExr));
				ercTxFeeDTO.setUsdMaxFee (maxFee.multiply (usdExr));
			} else {
				if (ercDTO.getChainId () == 97) gasPrice = BigInteger.TEN; // testnet
				BigDecimal fee = Convert.fromWei (gasPrice.multiply (gasLimit).toString (), Convert.Unit.ETHER);

				ercTxFeeDTO.setFee (fee);
				Map<String, Object> exrBass = commonMapper.selectExrBass ("BNB").orElseThrow (() -> {
					throw new WalletException (StatusCode.NO_EXR_BASS);
				});
				BigDecimal usdExr = (BigDecimal) exrBass.get ("CX_EXR_USD_CRRNCY_AMT");
				ercTxFeeDTO.setUsdFee (fee.multiply (usdExr));
			}

			resultDTO = new ResultDTO (StatusCode.ACCESS, ercTxFeeDTO);
		} catch (WalletException e) {
			log.error (e.getMessage ());
			throw e;
		} catch (Exception e) {
			log.error (e.getMessage ());
			throw new WalletException (StatusCode.BASIC_ERROR.getCode (), e.getMessage ());
		}
		return resultDTO;
	}
}
