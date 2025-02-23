package com.playnomm.wallet.exception;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.StatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description : 전역 에러 처리
 */
@ControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(WalletException.class)
	public ResponseEntity<Object> walletException (WalletException e) {
		ResultDTO resultDTO = new ResultDTO ();
		resultDTO.setCode (e.getCode ());
		resultDTO.setMessage (e.getMessage ());
		return ResponseEntity.ok ().body (resultDTO);
	}
}
