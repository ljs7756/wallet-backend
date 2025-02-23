package com.playnomm.wallet.dto.sample;

import lombok.Data;

import javax.persistence.*;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Data
@Entity
@Table(name = "SAMPLE_DETAIL_TABLE")
public class SampleDetailDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SAMPLE_DETAIL_SN")
	private Long sampleDetailSn;

	@Column(name = "SAMPLE_NAME")
	private String sampleName;

	@Column(name = "SAMPLE_PRICE")
	private Integer samplePrice;

	@Column(name = "SAMPLE_SN")
	private Long sampleSn;
}
