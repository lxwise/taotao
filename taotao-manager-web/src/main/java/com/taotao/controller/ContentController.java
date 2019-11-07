package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;

@Controller
public class ContentController {
	//$.post("/content/save",$("#contentAddForm").serialize(), function(data)

	@Autowired
	private ContentService contentService;
	@RequestMapping(value="/content/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult saveContent(TbContent tContent) {
		//1引入服务
		//2注入服务
		
		//3调用
		return contentService.saveContent(tContent);
	}
	/**
	 * 内容管理列表查询
	 */
	@RequestMapping(value="/content/query/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getContentList(Long categoryId,Integer page,Integer rows) {
		EasyUIDataGridResult content = contentService.getContentList(categoryId, page, rows);
		return content;
	}
	
	/***
	 * 内容编辑
	 */
	@RequestMapping(value="/rest/content/edit",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult updateContent(Long id,TbContent tbContent) {
		return contentService.updateContent(id, tbContent);
		
	}
	/**
	 * 内容删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(String [] ids) {
		return contentService.deleteContent(ids);
	}
	
}
