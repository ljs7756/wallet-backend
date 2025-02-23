package com.playnomm.wallet.controller.blockchain;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.dto.blockchain.ErcDTO;
import com.playnomm.wallet.dto.blockchain.LmDTO;
import com.playnomm.wallet.dto.blockchain.response.*;
import com.playnomm.wallet.enums.StatusCode;
import com.playnomm.wallet.exception.WalletException;
import com.playnomm.wallet.service.blockchain.ErcService;
import com.playnomm.wallet.service.blockchain.LeisureMetaService;
import com.playnomm.wallet.service.blockchain.PnAuthService;
import com.playnomm.wallet.util.BeanValidationUtil;
import com.playnomm.wallet.util.RequestContextUtil;
import com.playnomm.wallet.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * @author : hzn
 * @date : 2022/12/12
 * @description :
 */
@Tag(name = "블록체인")
@RestController
@RequestMapping("/api/v1/bc")
@RequiredArgsConstructor
public class BlockchainController {
	private final ErcService         ercService;
	private final LeisureMetaService leisureMetaService;
	private final PnAuthService      pnAuthService;

	@Operation(summary = "밸런스 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = BalanceDTO.class))
	}))
	@GetMapping("/balance/{networkId}/{userCxwaletTknSn}/{userCmmnSn}")
	public ResponseEntity<Object> balance (@Parameter(example = "1000", description = "네트워크 식별값 : 1000 (LMC) | 1001 (ETH) | 1002 (BNB)", schema = @Schema(allowableValues = {"1000", "1001", "1002"})) @PathVariable String networkId,
											  @Parameter(example = "1", description = "사용자 지갑 토큰 SN") @PathVariable Integer userCxwaletTknSn,
			                        	      @Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		if (ObjectUtils.isEmpty (networkId) || ObjectUtils.isEmpty (userCxwaletTknSn) || ObjectUtils.isEmpty (userCmmnSn)) return ResponseEntity.ok (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR));

		ResultDTO resultDTO = null;
		switch (networkId) {
			case "1000" :
				LmDTO lmDTO = new LmDTO ();
				lmDTO.setNetworkId (networkId);
				lmDTO.setUserCxwaletTknSn (userCxwaletTknSn);
				lmDTO.setUserCmmnSn (userCmmnSn);
				lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
				resultDTO = leisureMetaService.balance (lmDTO);
				break;
			case "1001" :
			case "1002" :
				ErcDTO ercDTO = new ErcDTO ();
				ercDTO.setNetworkId (networkId);
				ercDTO.setUserCxwaletTknSn (userCxwaletTknSn);
				ercDTO.setUserCmmnSn (userCmmnSn);
				ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
				resultDTO = ercService.balance (ercDTO);
				break;
		}
		return ResponseEntity.ok ().body (resultDTO);
	}

	@Operation(summary = "ETH FT 전송", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
//					@SchemaProperty(name = "symbol", schema = @Schema(title = "토큰 심볼", type = "string", example = "ETH", required = true)),
					@SchemaProperty(name = "userCxwaletTknSn", schema = @Schema(title = "사용자 지갑 토큰 SN", type = "integer", example = "1", required = true)),
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7BF54762Cf3D4e6da15B03ce52F7E7F94ACf5B74", minLength = 40, maxLength = 42, required = true)),
					@SchemaProperty(name = "priority", schema = @Schema(title = "우선 순위", type = "integer", example = "1", allowableValues = {"1", "2", "3"}, defaultValue = "1", required = true)),
					@SchemaProperty(name = "amount", schema = @Schema(title = "토큰 수량", type = "number", example = "0.01", required = true))
			})
	})
	@PostMapping("/eth/transfer")
	public ResponseEntity<Object> ethTransfer (@RequestBody @Validated({ValidationGroups.ErcTransferGroup.class}) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) {
			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
		}
		ercDTO.setNetworkId ("1001");
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.transfer (ercDTO));
	}

	@Operation(summary = "BNB FT 전송", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
//					@SchemaProperty(name = "symbol", schema = @Schema(title = "토큰 심볼", type = "string", example = "BNB", required = true)),
					@SchemaProperty(name = "userCxwaletTknSn", schema = @Schema(title = "사용자 지갑 토큰 SN", type = "integer", example = "1", required = true)),
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7BF54762Cf3D4e6da15B03ce52F7E7F94ACf5B74", minLength = 40, maxLength = 42, required = true)),
					@SchemaProperty(name = "priority", schema = @Schema(title = "우선 순위", type = "integer", example = "1", allowableValues = {"1", "2", "3"}, defaultValue = "1", required = true)),
					@SchemaProperty(name = "amount", schema = @Schema(title = "토큰 수량", type = "number", example = "0.01", required = true))
			})
	})
	@PostMapping("/bnb/transfer")
	public ResponseEntity<Object> bnbTransfer (@RequestBody @Validated({ValidationGroups.ErcTransferGroup.class}) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) {
			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
		}
		ercDTO.setNetworkId ("1002");
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.transfer (ercDTO));
	}

	@Operation(summary = "LMC FT 전송", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7b056ef5037b2357cd1d379bc45243f30430df28", minLength = 40, maxLength = 42, required = true)),
					@SchemaProperty(name = "amount", schema = @Schema(title = "토큰 수량", type = "number", example = "100", required = true)),
					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", example = "IcOC9wdfFoOiLZ0rQsd/XBbZSQTLQWSRdNVhpAqxFk7Gs/OmhnweMMQi4uVicqzFwt5tinjrwNYGNuVN0zCdgzkQuLfGwRaoV3ZhRnqAR82l6+Yyj50EgJz1GY8/Gpgo3NYvff7TCE7FPQxhXfLZBMHRx2WujJ600yc29esUD7DtLqKA2SAFDoMZ8iIIWgZiWW7hu1Accm29bX5QCPsU0MqUYEhfsbso0eH+U7JJj9aqSHntqTTWacNAqL6Rb+oIR+BP3OSVAJAA0StlMhC/U/J1dFYKDrLYaAa8bow6K4xwn3OgBEg0sTzEDWkzKm4Td673pVWpcboOQc52j09FC2I2t9kBKBbnn7F5PZRVZfefWIH5WA8C5J1q+JAMhOYb+ZGqALFGOej91a8zSm6nfgwlzjGbR4RzLyHwaLTAjGXn9fjYN/Rq2S1HX4H0aepB5Mj0VI+yC710jxom+JwjpvppYM17PBmVm+qahsJ7rQRyezwnIguBO29BvJ7WqeudQyKF6kzT9h4lMnosI4NCJOTJRbE5ovabu6TDOwZ1dJ+ro8SJ3cLHu111ks7GS5DNxE248tW1U3eGIgvf9ehWKQdiHfKHFaZfgFQKjvojfR00nK2IAVx5OCbF0Mp0ctCtGULjX7YSJiEjqfWZE9YLrOXnlLI406iIOo4o1Fvhd4s=", required = true))
			})
	})
	@PostMapping("/lmc/transfer")
	public ResponseEntity<Object> lmcTransfer (@RequestBody @Validated({ValidationGroups.LmcTransferGroup.class}) LmDTO lmDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) {
			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
		}
		lmDTO.setNetworkId ("1000");
		lmDTO.setSymbol ("LM");
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.transfer (lmDTO));
	}

	@Operation(summary = "LMC NFT 전송", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7b056ef5037b2357cd1d379bc45243f30430df28", minLength = 40, maxLength = 42, required = true)),
					@SchemaProperty(name = "nftitmSn", schema = @Schema(title = "NFT 번호", type = "integer", example = "1017721", required = true)),
					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", example = "tIyAfOpom4HLoSDNsJjHunKuHa61OeEzOQiPQIlGhFusV/rjH4I55DHgn6qkhDe+Ezgu5KV73D7r6DXmbB6n27sgQS/A26lZLNCG9wY4sXHHaZcbga5wtWucEoVx/dmT3U/Gbhcdf3QwxqXhTyvKPiQFoK9pFQ7sQoEdE83MjiaguDGX2H6CFmblvTnko6hm6e7+6+XouLByLqcvwqol6AUHG4trWKy7I7XFT9DnwW0KQFnsg15O9XudOQ1BiNoDyE6BC+q56X25cePz+1zdkOVayetZYZlP6yXILGAOLr49yOxkqjTygvXxsAmBVa901n/r8cs3uJnu0EtqoUEFDAmijzbgVEnJTRz7g9TDVCsbvRoP50K63Cde/1nZmsi50vwUEeMfiHG76+1giMFkZqALwULlKI/sykN5VkjGmt0MkFHcBiWNMCm95GE7s9Dlos3aYPO/xmpo/eFlSCsRWE+ZeL+iZhkeDzf/z1lBmJoAuu7QscHjFfdf4bUaxDoc8xHuYK28scYEdxjJ5u0sv2ep2KApgesitHeLNkl1rxjophGH67iMOwJetQhlyFw2wvrjuvWvKdDmAFaXzr8chFcxbh6//g9xxEmUJW3v9Eouypj6ArJ8Bum25CSVbYwU7O82LgxBxTPffoQ3hG5/ZiHOAFLvF4B6CelRJRNVVQo=", required = true))
			})
	})
	@PostMapping("/lmc/nft/transfer")
	public ResponseEntity<Object> lmcNftTransfer (@RequestBody @Validated({ValidationGroups.LmcNftTransferGroup.class}) LmDTO lmDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) {
			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
		}
		lmDTO.setNetworkId ("1000");
		lmDTO.setSymbol ("LM");
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.nftTransfer (lmDTO));
	}

	@Operation(summary = "LMC NFT 목록", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = LmcNftsDTO.class))
	}))
	@GetMapping("/lmc/nfts/{userCmmnSn}")
	public ResponseEntity<Object> lmcNfts (@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		LmDTO lmDTO = new LmDTO ();
		lmDTO.setUserCmmnSn (userCmmnSn);
		lmDTO.setNetworkId ("1000");
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.nfts (lmDTO));
	}

	@Operation(summary = "LMC NFT 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = LmcNftDTO.class))
	}))
	@GetMapping("/lmc/nft/{nftitmSn}/{userCmmnSn}")
	public ResponseEntity<Object> lmcNft (@Parameter(example = "1", description = "NFT SN") @PathVariable Integer nftitmSn,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		LmDTO lmDTO = new LmDTO ();
		lmDTO.setUserCmmnSn (userCmmnSn);
		lmDTO.setNftitmSn (nftitmSn);
		lmDTO.setNetworkId ("1000");
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.nft (lmDTO));
	}

	@Operation(summary = "ETH / BNB 토큰 정보 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTokenDTO.class))
	}))
	@GetMapping("/erc/tokenInfo/{networkId}/{contractAddress}/{userCmmnSn}")
	public ResponseEntity<Object> ercTokenInfo (@Parameter(example = "1001", description = "네트워크 식별값 : 1001 (ETH) | 1002 (BNB)", schema = @Schema(allowableValues = {"1001", "1002"})) @PathVariable String networkId,
			@Parameter(example = "0x80dc4b76fadF3BE523FF49727580e49Dd996994F", description = "토큰 계약 주소") @PathVariable String contractAddress,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		if ("1000".equals (networkId)) throw new WalletException (StatusCode.INVALID_NETWORK_ID);
		ErcDTO ercDTO = new ErcDTO ();
		ercDTO.setNetworkId (networkId);
		ercDTO.setContractAddress (contractAddress);
		ercDTO.setUserCmmnSn (userCmmnSn);
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.contractInfo (ercDTO));
	}

	@Operation(summary = "ETH / BNB NFT 목록 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcNftsDTO.class))
	}))
	@GetMapping("/erc/nfts/{networkId}/{userCmmnSn}")
	public ResponseEntity<Object> ercNfts (@Parameter(example = "1001", description = "네트워크 식별값 : 1001 (ETH) | 1002 (BNB)", schema = @Schema(allowableValues = {"1001", "1002"})) @PathVariable String networkId,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		if ("1000".equals (networkId)) throw new WalletException (StatusCode.INVALID_NETWORK_ID);
		ErcDTO ercDTO = new ErcDTO ();
		ercDTO.setNetworkId (networkId);
		ercDTO.setUserCmmnSn (userCmmnSn);
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.nfts (ercDTO));
	}

	@Operation(summary = "ETH / BNB NFT 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcNftDTO.class))
	}))
	@GetMapping("/erc/nfts/{networkId}/{contractAddress}/{tokenId}/{userCmmnSn}")
	public ResponseEntity<Object> ercNft (@Parameter(example = "1001", description = "네트워크 식별값 : 1001 (ETH) | 1002 (BNB)", schema = @Schema(allowableValues = {"1001", "1002"})) @PathVariable String networkId,
			@Parameter(example = "0xff02b7d59975E76F67B63b20b813a9Ec0f6AbD60", description = "토큰 계약 주소") @PathVariable String contractAddress,
			@Parameter(example = "0", description = "토큰 ID") @PathVariable BigInteger tokenId,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn, HttpServletRequest request) {
		if ("1000".equals (networkId)) throw new WalletException (StatusCode.INVALID_NETWORK_ID);
		ErcDTO ercDTO = new ErcDTO ();
		ercDTO.setNetworkId (networkId);
		ercDTO.setTokenId (tokenId);
		ercDTO.setContractAddress (contractAddress);
		ercDTO.setUserCmmnSn (userCmmnSn);
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.nft (ercDTO));
	}

	@Operation(summary = "ETH 수수료 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxFeeDTO.class))
	}))
	@GetMapping("/eth/fee/{priority}/{userCxwaletTknSn}/{userCmmnSn}")
	public ResponseEntity<Object> ethFee (@Parameter(example = "1", description = "우선 순위", schema = @Schema(allowableValues = {"1", "2", "3"}, defaultValue = "1", type = "integer")) @PathVariable Integer priority,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn,
			@Parameter(example = "1", description = "사용자 지갑 토큰 SN") @PathVariable Integer userCxwaletTknSn,
			HttpServletRequest request) {
		ErcDTO ercDTO = new ErcDTO ();
		ercDTO.setNetworkId ("1001");
		ercDTO.setPriority (priority);
		ercDTO.setUserCmmnSn (userCmmnSn);
		ercDTO.setUserCxwaletTknSn (userCxwaletTknSn);
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.getTransferFee (ercDTO));
	}

	@Operation(summary = "BNB 수수료 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxFeeDTO.class))
	}))
	@GetMapping("/bnb/fee/{priority}/{userCxwaletTknSn}/{userCmmnSn}")
	public ResponseEntity<Object> bnbFee (@Parameter(example = "1", description = "우선 순위", schema = @Schema(allowableValues = {"1", "2", "3"}, defaultValue = "1", type = "integer")) @PathVariable Integer priority,
			@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn,
			@Parameter(example = "1", description = "사용자 지갑 토큰 SN") @PathVariable Integer userCxwaletTknSn,
			HttpServletRequest request) {
		ErcDTO ercDTO = new ErcDTO ();
		ercDTO.setNetworkId ("1002");
		ercDTO.setUserCmmnSn (userCmmnSn);
		ercDTO.setPriority (priority);
		ercDTO.setUserCxwaletTknSn (userCxwaletTknSn);
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.getTransferFee (ercDTO));
	}

	@Operation(summary = "LMC 스왑 수수료 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(example = "20", description = "스왑 수수료", type = "number"))
	}))
	@GetMapping("/lmc/swapfee")
	public ResponseEntity<Object> lmcSwapFee (HttpServletRequest request) {
		LmDTO lmDTO = new LmDTO ();
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.getSwapFee (lmDTO));
	}

	@Operation(summary = "LMC -> ETH 스왑", description = "LMC -> ETH")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "amount", schema = @Schema(title = "토큰 수량", type = "number", example = "0.01", required = true)),
					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", description = "LMC 서명을 위한 개인키", example = "HYc8obGgXYeDHK0TKUez13j5j7QnulFdcxoeOKQ5UjNCwJnCM8d36JGdYSzVzUYyEGh1QOw9SdpKoog5PqVatkyIgBc3keL7tKSySQNXfMzWKZ2L4p8KfL3LcNk5k6Wu1sIxFp/4FG1FfFGXrfVztEFZMPcof1vQ+5xkvn93WRqshDG+p2JI6LlRUDE7b5iXwnUbPGmAm+6V4HpDD+5A44MZ/EYwHWyLXurZrHCpqVm2EcXXU/iknFSFiB9trZokdRU7TAkecWBGhoIrfWs3L/lfS4AomnJkPEB6ltCH7oSdm+cr/OJtk3vZgEVp+to/DQPOrpdqX7kHiqq4V+Hnz6s6MU18hSLDCFT4OXt+NhLb87TyGcYKeBHtGtsJa62WCmYhYDEt9+v8eO97SoPpz0s7wmng+h6JOAifWRHTMX2S6XGGuFXrYLUiAv9KHrilbxfWzas9jqFM7nfWSijT1KySfvIhV/fmMJowQxhaNqluP/UZi0QUUHONhUjlM9EFTPsbu48pN4a0WGPNnTanjCVlWJ7n//2L2PHS2vJaPrSmERREqCgCqRSp/UDKLxQPErsfVuRTGBWaqt5olLWrg3LXfyat+b4H27CAbWG+2SBEDBIGipgqqZuMlkvA22aWjzEvqDe03os8jUlviqSiv3EzDj6FFYX1QNkEKvQlic4=", required = true))
			})
	})
	@PostMapping("/swap/lmc/eth")
	public ResponseEntity<Object> lmcSwap (@RequestBody @Validated({ValidationGroups.LmcSwapGroup.class}) LmDTO lmDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));

		lmDTO.setNetworkId ("1000");
		lmDTO.setToNetworkId ("1001");
		lmDTO.setGtwySeCode ("LM");
		lmDTO.setSymbol ("LM");
		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (leisureMetaService.swap (lmDTO));
	}

	@Operation(summary = "ETH -> LMC 스왑", description = "ETH -> LMC")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxDTO.class))
	}))
	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
			@Content(schemaProperties = {
					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
					@SchemaProperty(name = "priority", schema = @Schema(title = "우선 순위", type = "integer", example = "1", allowableValues = {"1", "2", "3"}, defaultValue = "1", required = true)),
					@SchemaProperty(name = "amount", schema = @Schema(title = "토큰 수량", type = "number", example = "0.01", required = true))
			})
	})
	@PostMapping("/swap/eth/lmc")
	public ResponseEntity<Object> ercSwap (@RequestBody @Validated({ValidationGroups.ErcSwapGroup.class}) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
		if (errors.hasErrors ()) return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));

		ercDTO.setNetworkId ("1001");
		ercDTO.setToNetworkId ("1000");
		ercDTO.setSymbol ("LM");
		ercDTO.setGasLimit (ercDTO.ETH_ERC_GAS_LIMIT);
		ercDTO.setGtwySeCode ("ETH");
		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
		return ResponseEntity.ok ().body (ercService.swap (ercDTO));
	}

	@Operation(summary = "회원탈퇴 가능여부 조회", description = "")
	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
			@SchemaProperty(name = "data", schema = @Schema(implementation = WithdrawalInfoDTO.class))
	}))
	@GetMapping("/withdrawal-info/{userCmmnSn}")
	public ResponseEntity<Object> withdrawalInfo (@Parameter(example = "20220830", description = "회원 공통 SN") @PathVariable Integer userCmmnSn) {
		List<Map<String, Object>> services = pnAuthService.listOfServices (userCmmnSn);
		WithdrawalInfoDTO withdrawalInfoDTO = new WithdrawalInfoDTO ();
		if (!ObjectUtils.isEmpty (services)) {
			long count = services.stream ().filter (m -> m.containsKey ("CXWL")).count ();
			if (services.size () == 1 && count == 1) withdrawalInfoDTO.setWhetherWithdrawAll ("Y");
			else withdrawalInfoDTO.setWhetherWithdrawAll ("N");
		} else {
			withdrawalInfoDTO.setWhetherWithdrawAll ("Y");
		}

		if (ercService.whetherOwnedAssets (userCmmnSn)) withdrawalInfoDTO.setWhetherOwnedErcAssets ("Y");
		else withdrawalInfoDTO.setWhetherOwnedErcAssets ("N");

		if (leisureMetaService.whetherOwnedAssets (userCmmnSn)) withdrawalInfoDTO.setWhetherOwnedLmcAssets ("Y");
		else withdrawalInfoDTO.setWhetherOwnedLmcAssets ("N");

		return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.ACCESS, withdrawalInfoDTO));
	}

//	@Operation(summary = "ETH NFT 전송", description = "")
//	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
//			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
//			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
//			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
//	}))
//	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
//			@Content(schemaProperties = {
//					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
//					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7b056ef5037b2357cd1d379bc45243f30430df28", minLength = 40, maxLength = 42, required = true)),
//					@SchemaProperty(name = "nftitmSn", schema = @Schema(title = "NFT 번호", type = "integer", example = "1017721", required = true)),
//					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", example = "tIyAfOpom4HLoSDNsJjHunKuHa61OeEzOQiPQIlGhFusV/rjH4I55DHgn6qkhDe+Ezgu5KV73D7r6DXmbB6n27sgQS/A26lZLNCG9wY4sXHHaZcbga5wtWucEoVx/dmT3U/Gbhcdf3QwxqXhTyvKPiQFoK9pFQ7sQoEdE83MjiaguDGX2H6CFmblvTnko6hm6e7+6+XouLByLqcvwqol6AUHG4trWKy7I7XFT9DnwW0KQFnsg15O9XudOQ1BiNoDyE6BC+q56X25cePz+1zdkOVayetZYZlP6yXILGAOLr49yOxkqjTygvXxsAmBVa901n/r8cs3uJnu0EtqoUEFDAmijzbgVEnJTRz7g9TDVCsbvRoP50K63Cde/1nZmsi50vwUEeMfiHG76+1giMFkZqALwULlKI/sykN5VkjGmt0MkFHcBiWNMCm95GE7s9Dlos3aYPO/xmpo/eFlSCsRWE+ZeL+iZhkeDzf/z1lBmJoAuu7QscHjFfdf4bUaxDoc8xHuYK28scYEdxjJ5u0sv2ep2KApgesitHeLNkl1rxjophGH67iMOwJetQhlyFw2wvrjuvWvKdDmAFaXzr8chFcxbh6//g9xxEmUJW3v9Eouypj6ArJ8Bum25CSVbYwU7O82LgxBxTPffoQ3hG5/ZiHOAFLvF4B6CelRJRNVVQo=", required = true))
//			})
//	})
//	@PostMapping("/eth/nft/transfer")
//	public ResponseEntity<Object> ethNftTransfer (@RequestBody @Validated({ValidationGroups.ErcNftTransferGroup.class}) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
//		if (errors.hasErrors ()) {
//			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
//		}
//		ercDTO.setNetworkId ("1001");
//		ercDTO.setSymbol ("ETH");
//		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
//		return ResponseEntity.ok ().body (ercService.nftTransfer (ercDTO));
//	}
//
//	@Operation(summary = "BNB NFT 전송", description = "")
//	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
//			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
//			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
//			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
//	}))
//	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
//			@Content(schemaProperties = {
//					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
//					@SchemaProperty(name = "toAddress", schema = @Schema(title = "수신자 지갑주소", type = "string", example = "0x7b056ef5037b2357cd1d379bc45243f30430df28", minLength = 40, maxLength = 42, required = true)),
//					@SchemaProperty(name = "nftitmSn", schema = @Schema(title = "NFT 번호", type = "integer", example = "1017721", required = true)),
//					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", example = "tIyAfOpom4HLoSDNsJjHunKuHa61OeEzOQiPQIlGhFusV/rjH4I55DHgn6qkhDe+Ezgu5KV73D7r6DXmbB6n27sgQS/A26lZLNCG9wY4sXHHaZcbga5wtWucEoVx/dmT3U/Gbhcdf3QwxqXhTyvKPiQFoK9pFQ7sQoEdE83MjiaguDGX2H6CFmblvTnko6hm6e7+6+XouLByLqcvwqol6AUHG4trWKy7I7XFT9DnwW0KQFnsg15O9XudOQ1BiNoDyE6BC+q56X25cePz+1zdkOVayetZYZlP6yXILGAOLr49yOxkqjTygvXxsAmBVa901n/r8cs3uJnu0EtqoUEFDAmijzbgVEnJTRz7g9TDVCsbvRoP50K63Cde/1nZmsi50vwUEeMfiHG76+1giMFkZqALwULlKI/sykN5VkjGmt0MkFHcBiWNMCm95GE7s9Dlos3aYPO/xmpo/eFlSCsRWE+ZeL+iZhkeDzf/z1lBmJoAuu7QscHjFfdf4bUaxDoc8xHuYK28scYEdxjJ5u0sv2ep2KApgesitHeLNkl1rxjophGH67iMOwJetQhlyFw2wvrjuvWvKdDmAFaXzr8chFcxbh6//g9xxEmUJW3v9Eouypj6ArJ8Bum25CSVbYwU7O82LgxBxTPffoQ3hG5/ZiHOAFLvF4B6CelRJRNVVQo=", required = true))
//			})
//	})
//	@PostMapping("/bnb/nft/transfer")
//	public ResponseEntity<Object> bnbNftTransfer (@RequestBody @Validated({ValidationGroups.ErcNftTransferGroup.class}) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
//		if (errors.hasErrors ()) {
//			return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
//		}
//		ercDTO.setNetworkId ("1002");
//		ercDTO.setSymbol ("BNB");
//		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
//		return ResponseEntity.ok ().body (ercService.nftTransfer (ercDTO));
//	}
//
//	@Operation(summary = "LMC NFT -> ETH NFT 스왑", description = "LMC NFT -> ETH NFT")
//	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
//			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
//			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
//			@SchemaProperty(name = "data", schema = @Schema(implementation = LmTxDTO.class))
//	}))
//	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
//			@Content(schemaProperties = {
//					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
//					@SchemaProperty(name = "nftitmSn", schema = @Schema(title = "토큰 SN", type = "integer", example = "1", required = true)),
//					@SchemaProperty(name = "privateKey", schema = @Schema(title = "개인키", type = "string", description = "LMC 서명을 위한 개인키", example = "HYc8obGgXYeDHK0TKUez13j5j7QnulFdcxoeOKQ5UjNCwJnCM8d36JGdYSzVzUYyEGh1QOw9SdpKoog5PqVatkyIgBc3keL7tKSySQNXfMzWKZ2L4p8KfL3LcNk5k6Wu1sIxFp/4FG1FfFGXrfVztEFZMPcof1vQ+5xkvn93WRqshDG+p2JI6LlRUDE7b5iXwnUbPGmAm+6V4HpDD+5A44MZ/EYwHWyLXurZrHCpqVm2EcXXU/iknFSFiB9trZokdRU7TAkecWBGhoIrfWs3L/lfS4AomnJkPEB6ltCH7oSdm+cr/OJtk3vZgEVp+to/DQPOrpdqX7kHiqq4V+Hnz6s6MU18hSLDCFT4OXt+NhLb87TyGcYKeBHtGtsJa62WCmYhYDEt9+v8eO97SoPpz0s7wmng+h6JOAifWRHTMX2S6XGGuFXrYLUiAv9KHrilbxfWzas9jqFM7nfWSijT1KySfvIhV/fmMJowQxhaNqluP/UZi0QUUHONhUjlM9EFTPsbu48pN4a0WGPNnTanjCVlWJ7n//2L2PHS2vJaPrSmERREqCgCqRSp/UDKLxQPErsfVuRTGBWaqt5olLWrg3LXfyat+b4H27CAbWG+2SBEDBIGipgqqZuMlkvA22aWjzEvqDe03os8jUlviqSiv3EzDj6FFYX1QNkEKvQlic4=", required = true))
//			})
//	})
//	@PostMapping("/nft-swap/lmc/eth")
//	public ResponseEntity<Object> lmcNftSwap (@RequestBody @Validated(ValidationGroups.LmcNftSwapGroup.class) LmDTO lmDTO, Errors errors, HttpServletRequest request) {
//		if (errors.hasErrors ()) return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
//
//		lmDTO.setNetworkId ("1000");
//		lmDTO.setToNetworkId ("1001");
//		lmDTO.setGtwySeCode ("LM");
//		lmDTO.setSymbol ("LM");
//		lmDTO.setLangCode (RequestContextUtil.getLocaleString (request));
//		return ResponseEntity.ok ().body (leisureMetaService.nftSwap (lmDTO));
//	}
//
//	@Operation(summary = "ETH NFT -> LMC NFT 스왑", description = "ETH NFT -> LMC NFT")
//	@ApiResponse(responseCode = "200", description = "successful operation", content = @Content(schemaProperties = {
//			@SchemaProperty(name = "code", schema = @Schema(example = "200", description = "상태 코드", type = "integer")),
//			@SchemaProperty(name = "message", schema = @Schema(example = "OK.", description = "메시지", type = "string")),
//			@SchemaProperty(name = "data", schema = @Schema(implementation = ErcTxDTO.class))
//	}))
//	@io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
//			@Content(schemaProperties = {
//					@SchemaProperty(name = "userCmmnSn", schema = @Schema(title = "공통 회원 번호", type = "integer", example = "20220830", required = true)),
//					@SchemaProperty(name = "priority", schema = @Schema(title = "우선 순위", type = "integer", example = "1", allowableValues = {"1", "2", "3"}, defaultValue = "1", required = true)),
//					@SchemaProperty(name = "contractAddress", schema = @Schema(title = "NFT 계약 주소", type = "string", example = "0xff02b7d59975E76F67B63b20b813a9Ec0f6AbD60", required = true)),
//					@SchemaProperty(name = "tokenId", schema = @Schema(title = "토큰 ID", type = "number", example = "1", required = true))
//			})
//	})
//	@PostMapping("/nft-swap/eth/lmc")
//	public ResponseEntity<Object> ercNftSwap (@RequestBody @Validated(ValidationGroups.ErcNftSwapGroup.class) ErcDTO ercDTO, Errors errors, HttpServletRequest request) {
//		if (errors.hasErrors ()) return ResponseEntity.ok ().body (new ResultDTO<> (StatusCode.REQUIRED_PARAMETERS_ERROR, BeanValidationUtil.getErrorMap (errors)));
//
//		ercDTO.setNetworkId ("1001");
//		ercDTO.setToNetworkId ("1000");
//		ercDTO.setSymbol ("LM");
//		ercDTO.setGasLimit (ercDTO.ETH_ERC_GAS_LIMIT);
//		ercDTO.setGtwySeCode ("ETH");
//		ercDTO.setLangCode (RequestContextUtil.getLocaleString (request));
//		return ResponseEntity.ok ().body (ercService.nftSwap (ercDTO));
//	}
}
