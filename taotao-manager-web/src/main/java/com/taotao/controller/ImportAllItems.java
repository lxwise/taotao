package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.service.SearchItemService;

@Controller
public class ImportAllItems {
	
	@Autowired
	private SearchItemService searchItemService;
	
	/**
	 * 导入所有的商品的数据到索引库中
	 * @return
	 */
	@RequestMapping("/index/imporAll")
	@ResponseBody
	public TaotaoResult importAll() throws Exception{
		//引入服务
		//注入服务
		return searchItemService.importItemsToIndex();
		//调用方法
	 
	}
}
