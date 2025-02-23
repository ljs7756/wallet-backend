package com.playnomm.wallet.controller.sample;

import com.playnomm.wallet.dto.PageDTO;
import com.playnomm.wallet.dto.sample.SampleDTO;
import com.playnomm.wallet.service.sample.SampleService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description : sample
 */
@Hidden
@RestController
@RequestMapping("/api/v1/sample")
public class SampleApiController {

	@Autowired
	private SampleService sampleService;

	@GetMapping("/{sampleSn}")
	public ResponseEntity<Object> sample (@PathVariable Long sampleSn) {
		return ResponseEntity.status (200).body (sampleService.getSample (sampleSn, "Y"));
	}

	@GetMapping("/search")
	public ResponseEntity<Object> search (SampleDTO sampleDTO, PageDTO pageDTO) {
		return ResponseEntity.status (200).body (sampleService.search (sampleDTO, pageDTO));
	}
}
