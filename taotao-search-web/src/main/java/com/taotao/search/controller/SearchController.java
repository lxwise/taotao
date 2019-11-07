package com.taotao.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.SearchResult;
import com.taotao.search.service.SearchItemService;

@Controller
public class SearchController {
	@Value("${ITEM_ROWS}")
	private Integer ITEM_ROWS;
	
	@Autowired
	private SearchItemService service;
	/**
	 * 根据条件搜索商品数据
	 * @param page
	 * @param queryString
	 * @return
	 */
	@RequestMapping("/search")
	
	public String search(@RequestParam("q")String queryString, 
			@RequestParam(defaultValue="1")Integer page, Model model) throws Exception{
		//引入
		//注入
		//调用
		//处理乱码
		queryString = new String(queryString.getBytes("iso-8859-1"),"utf-8");
		SearchResult searchResult = service.search(queryString, page, ITEM_ROWS);
		//设置数据传到jsp中
		model.addAttribute("query",queryString);
		model.addAttribute("totalPages",searchResult.getPageCount());//总页数
		model.addAttribute("itemList", searchResult.getItemList());//list集合商品信息
		model.addAttribute("page", page);
		//返回
		return "search";
	}
}
