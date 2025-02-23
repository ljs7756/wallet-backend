package com.playnomm.wallet.dto;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : hzn
 * @date : 2022/11/07
 * @description :
 */
@Getter
public class PageDTO {
	/**
	 * 요청 페이지
	 */
	private int              page      = 0;
	/**
	 * 페이지당 출력될 목록 수
	 */
	private int              size      = 10;
	/**
	 * 정렬 컬럼
	 */
	private String           sort      = "";
	/**
	 * 정렬 방향
	 */
	private Sort.Direction   direction = Sort.Direction.DESC;
	/**
	 * 다중 정렬
	 */
	private List<Sort.Order> orders    = new ArrayList<> ();

	public void setPage (int page) {
		this.page = page <= 0 ? 0 : page;
	}

	public void setSize (int size) {
		this.size = size;
	}

	public void setSort (String sort) {
		this.sort = sort;
	}

	public void setDirection (Sort.Direction direction) {
		this.direction = ObjectUtils.isEmpty (direction) ? Sort.Direction.DESC : direction;
	}

	public void addOrder (String sort, Sort.Direction direction) {
		if (!ObjectUtils.isEmpty (sort) && !ObjectUtils.isEmpty (direction)) {
			if (direction.equals (Sort.Direction.ASC)) {
				orders.add (Sort.Order.asc (sort));
			} else {
				orders.add (Sort.Order.desc (sort));
			}
		}
	}

	public PageRequest toPageRequest () {
		if (ObjectUtils.isEmpty (orders) && !ObjectUtils.isEmpty (sort)) {
			return PageRequest.of (page, size, Sort.by (direction, sort));
		} else if (!ObjectUtils.isEmpty (orders)) {
			return PageRequest.of (page, size, Sort.by (orders));
		}
		return PageRequest.of (page, size);
	}

}
