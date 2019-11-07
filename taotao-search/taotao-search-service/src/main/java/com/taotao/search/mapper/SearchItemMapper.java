package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

public interface SearchItemMapper {
	//	查询所有商品数据
	List<SearchItem> getItemList();
	//根据商品的id查询商品的数据
		public SearchItem getSearchItemById(Long itemId);
}
