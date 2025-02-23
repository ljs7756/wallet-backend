package com.playnomm.wallet.repository.sample;

import com.playnomm.wallet.dto.sample.SampleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
public interface SampleRepositoryCustom {
	Page<SampleDTO> search (SampleDTO sampleDTO, Pageable pageable);
}
