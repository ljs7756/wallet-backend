package com.playnomm.wallet.repository.sample;

import com.playnomm.wallet.dto.sample.QSampleDTO;
import com.playnomm.wallet.dto.sample.QSampleDetailDTO;
import com.playnomm.wallet.dto.sample.SampleDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
public class SampleRepositoryImpl extends QuerydslRepositorySupport implements SampleRepositoryCustom {
	@Autowired
	private JPAQueryFactory queryFactory;

	public SampleRepositoryImpl () {
		super (SampleDTO.class);
	}

	@Override
	public Page<SampleDTO> search (SampleDTO sampleDTO, Pageable pageable) {
		QSampleDTO qSampleDTO = QSampleDTO.sampleDTO;
		QSampleDetailDTO qSampleDetailDTO = QSampleDetailDTO.sampleDetailDTO;

		//조건 설정
		BooleanBuilder builder = new BooleanBuilder ();
		builder.and (qSampleDTO.useAt.eq (sampleDTO.getUseAt ()));
		if (!ObjectUtils.isEmpty (sampleDTO.getCategoryName ())) {
			builder.and (qSampleDTO.categoryName.like (sampleDTO.getCategoryName ()));
		}

		//정렬 설정
		List<Sort.Order> orderList = pageable.getSort ().toList ();
		OrderSpecifier[] orderSpecifier = new OrderSpecifier[orderList.size ()];
		PathBuilder<SampleDTO> entityPath = new PathBuilder<> (SampleDTO.class, "sampleDTO");
		for (int i = 0, n = orderList.size (); i < n; i++) {
			Sort.Order order = orderList.get (i);
			PathBuilder<Object> path = entityPath.get (order.getProperty ());
			orderSpecifier[i] = new OrderSpecifier (Order.valueOf (order.getDirection ().name ()), path);
		}

		List<SampleDTO> content = queryFactory
				.selectFrom (qSampleDTO)
				.leftJoin (qSampleDTO.sampleDetailDTOList, qSampleDetailDTO)
				.where (builder)
				.offset (pageable.getOffset ())
				.limit (pageable.getPageSize ())
				.orderBy (orderSpecifier)
				.groupBy (qSampleDTO.categoryCode)
				.fetch ();

		Long count = queryFactory
				.select (qSampleDTO.count ()).from (qSampleDTO)
				.where (builder)
				.fetchOne ();
		return new PageImpl<> (content, pageable, count);
	}
}
