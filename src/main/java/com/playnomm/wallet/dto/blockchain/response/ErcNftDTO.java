package com.playnomm.wallet.dto.blockchain.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author : hzn
 * @date : 2023/01/10
 * @description :
 */
@Setter
@Getter
public class ErcNftDTO {
	@Schema(example = "0x495f947276749ce646f68ac8c248420045cb7b5e", description = "계약 주소", type = "string")
	private String        contract;
	@Schema(example = "30525461628701321596829457633536467399064712353917245261906823441357413024244", description = "토큰 ID", type = "string")
	private String        tokenId;
	@Schema(example = "Blue", description = "NFT 이름", type = "string")
	private String        name;
	@Schema(example = "zieea oouhs xmnex ecnqf styja tgfhw ynjij nfttq ecthm rhfaw bju", description = "NFT 설명", type = "string")
	private String        description;
	@Schema(example = "https://arweave.net/7kYf67WUdFjKnfFWrg98ba0ZgDIqCbe9s_txuL-OJtE", description = "이미지", type = "string")
	private String        image;
	@Schema(example = "https://1984ny.dk/", description = "외부 링크", type = "string")
	private String        externalUrl;
	@Schema(example = "1", description = "거래 SN", type = "integer")
	private Integer       cxTradeSn;
	@Schema(example = "2022-09-28 00:00:00", description = "받은 시각", type = "string")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime tradeOccrrncDt;
	@Schema(example = "17af7bcbecc9963df6b4f2704f784b4a485df262", description = "보낸 사람", type = "string")
	private String        trsmtrCxwaletAdres;
	@Schema(example = "60eb0562e1ecb53216ced1baa596852da62676fd1d01b89cc0af3853a04222f7", description = "트랜잭션 해시", type = "string")
	private String        signTxhash;
}
