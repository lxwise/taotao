package com.taotao.search.service;

import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;

public interface SearchItemService {
	//	导入所有的商品数据到索引库中
	TaotaoResult importItemsToIndex() throws Exception;
	
	//根据搜索的条件搜索结果
	/**
	 * 
	 * @param queryString 查询的条件
	 * @param page  查询当前的页码
	 * @param rows 每页显示的行数 controller中写死
	 * @return
	 * @throws Exception 
	 */
	public SearchResult search(String queryString,Integer page,Integer rows) throws Exception;

	/**
	 * 更新索引库
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult updateSearchItemById(Long itemId) throws Exception;
}
	