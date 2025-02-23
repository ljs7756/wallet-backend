package com.playnomm.wallet.service.sample;

import com.playnomm.wallet.dto.PageDTO;
import com.playnomm.wallet.dto.sample.SampleDTO;
import com.playnomm.wallet.repository.sample.SampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Service
public class SampleService {
	@Autowired
	private SampleRepository sampleRepository;

	public SampleDTO getSample (Long sn, String useAt) {
		return sampleRepository.findBySampleSnAndUseAt (sn, useAt);
	}

	public Page<SampleDTO> search (SampleDTO sampleDTO, PageDTO pageDTO) {
		return sampleRepository.search (sampleDTO, pageDTO.toPageRequest ());
	}
}
