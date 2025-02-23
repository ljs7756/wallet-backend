package com.playnomm.wallet.repository.sample;

import com.playnomm.wallet.dto.sample.SampleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
public interface SampleRepository extends JpaRepository<SampleDTO, Long>, SampleRepositoryCustom {
	SampleDTO findBySampleSnAndUseAt (Long sampleSn, String useAt);

	@Query(value = "UPDATE SAMPLE_TABLE SET USE_AT = 'N' WHERE SAMPLE_SN IN :snList", nativeQuery = true)
	void deleteAllBySampleSnIn (@Param("snList") List<Long> snList);
}
