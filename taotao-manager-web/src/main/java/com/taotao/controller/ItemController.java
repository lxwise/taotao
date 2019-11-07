package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;

@Controller
public class ItemController {
	@Autowired
	private ItemService itemService;
	@RequestMapping(value="/item/list",method=RequestMethod.GET)
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows) {
		
		return itemService.getItemList(page, rows);
	}
	/**添加商品**/
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult addItem(TbItem item,String desc) {
		TaotaoResult result = itemService.addItem(item, desc);
		return result;
	}
	/**
	 * 根据商品id，获取商品描述
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value="/item/desc/{itemId}")
	@ResponseBody
	public TaotaoResult getItemDesc(@PathVariable Long itemId) {
		TaotaoResult result = itemService.getItemDesc(itemId);
		return result;
	}
	
	/**
	 * 更新（修改）商品
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping("/item/update")
	@ResponseBody
	public TaotaoResult updateItem(TbItem item, String desc) {
		TaotaoResult result = itemService.updateItem(item, desc);
		return result;
	}

    /**
	 * 更新商品状态 
	 */
	@RequestMapping("/rest/item/{method}")
	@ResponseBody
	public TaotaoResult updateItemStatus(@RequestParam(value="ids")List<Long> ids,@PathVariable String method) {
		TaotaoResult result = itemService.updateItemStatus(ids,method);
		return result;
	}
	
}
