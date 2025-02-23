package com.playnomm.wallet.exception;

import com.playnomm.wallet.dto.ResultDTO;
import com.playnomm.wallet.enums.StatusCode;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description : 화이트 라벨 에러 페이지 처리
 */
@RestController
public class CustomErrorController implements ErrorController {

	@GetMapping("/error")
	public ResponseEntity<Object> error (HttpServletRequest request, HttpServletResponse response) {
		ResultDTO resultDTO = null;
		int status = response.getStatus ();
		switch (status) {
			case 400:
				resultDTO = new ResultDTO (StatusCode.BAD_REQUEST);
				break;
			case 401:
				resultDTO = new ResultDTO (StatusCode.TOKEN_UNAUTHORIZED);
				break;
			case 403:
				resultDTO = new ResultDTO (StatusCode.TOKEN_EXPIRED);
				break;
			case 404:
				resultDTO = new ResultDTO (StatusCode.NOT_FOUND);
				break;
			case 500:
				resultDTO = new ResultDTO (StatusCode.INTERNAL_SERVER_ERROR);
				break;
			default:
				resultDTO = new ResultDTO (StatusCode.BASIC_ERROR);
		}
		return ResponseEntity.ok ().body (resultDTO);
	}
}
