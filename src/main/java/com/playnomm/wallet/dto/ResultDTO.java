package com.playnomm.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.playnomm.wallet.enums.StatusCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * packageName :  com.playnomm.wallet.dto.response
 * fileName : ResultDTO
 * author :  ljs7756
 * date : 2022-12-12
 * description :
 * ===========================================================
 * DATE                 AUTHOR                NOTE
 * -----------------------------------------------------------
 * 2022-12-12                ljs7756             최초 생성
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultDTO<T> {
	@Schema(example = "200", description = "상태 코드")
	private int    code;
	@Schema(example = "ok.", description = "메시지")
	private String message;
	private T      data;

	public ResultDTO () {
	}

	public ResultDTO (StatusCode statusCode) {
		this.code = statusCode.getCode ();
		this.message = statusCode.getMessage ();
	}

	public ResultDTO (StatusCode statusCode, T data) {
		this.code = statusCode.getCode ();
		this.message = statusCode.getMessage ();
		this.data = data;
	}

	public ResultDTO (int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResultDTO (int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}
}
