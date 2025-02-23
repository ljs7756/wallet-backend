package com.playnomm.wallet.dto.sample;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.playnomm.wallet.dto.SearchDTO;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Data
@Entity
@Table(name = "SAMPLE_TABLE")
public class SampleDTO extends SearchDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, length = 11, name = "SAMPLE_SN")
	private Long sampleSn;

	@Column(name = "CATEGORY_CODE")
	private String categoryCode;

	@Column(name = "CATEGORY_NAME")
	private String categoryName;

	@Column(name = "CREATED_DATE_TIME")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdDateTime;

	@Column(name = "USE_AT")
	private String useAt = "Y";

	@OneToMany
	@JoinColumn(name = "SAMPLE_SN", referencedColumnName = "SAMPLE_SN")
	@OrderBy("sampleDetailSn desc")
	private List<SampleDetailDTO> sampleDetailDTOList;
}
