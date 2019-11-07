package com.taotao.service;
/**
 * 商品相关接口
 * @author liuxin
 *
 */

import java.util.List;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	/**
	 * 根据当前的页码和每页的行数进行分页查询
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult getItemList(Integer page,Integer rows);
	TaotaoResult addItem(TbItem item,String desc);
	/**
	 * 根据商品id查询商品描述，将查询结果封装到TaotaoResult中
	 * @param itemId
	 * @return
	 */
	TaotaoResult getItemDesc(Long itemId);
	
	/**
	* 更新商品
	* @param item
	* @param desc
	* @return
	*/
	TaotaoResult updateItem(TbItem item, String desc);

	/**
     * 根据商品id，更新商品状态：1-正常，2-下架，3-删除
     * @param ids
     * @param method
     * @return
     */
    TaotaoResult updateItemStatus(List<Long> ids, String method);
    
    /**
     * 根据商品的id查询商品的数据
     * @param itemId
     * @return
     */
    public TbItem getItemById(Long itemId);
    
    /**
     * 根据商品的id查询商品的描述
     * @param itemId
     * @return
     */
    public TbItemDesc getItemDescById(Long itemId);
}
