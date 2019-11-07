package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.IDUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {
	//注入服务
	@Autowired
	private TbItemMapper mapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	@Autowired
	private JmsTemplate jmstemplate;
	
	@Autowired
	private JedisClient client;
	
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;
	
	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;
	
	@Resource(name="topicDestination")
	private Destination destination;
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		
		//设置分页信息
		if(page==null)page=1;
		if(rows==null)rows=30;
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example = new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);
		//取分页信息
		PageInfo<TbItem> info = new PageInfo<>(list);
		
		//创建返回结果对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal((int)info.getTotal());
		result.setRows(info.getList());
		return result;
	}
	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		// 1、生成商品id
		final long itemId = IDUtils.genItemId();
		// 2、补全TbItem对象的属性
		item.setId(itemId);
		//商品状态，1-正常，2-下架，3-删除
		item.setStatus((byte) 1);
		Date date = new Date();
		item.setCreated(date);
		item.setUpdated(date);
		// 3、向商品表插入数据
		mapper.insert(item);
		// 4、创建一个TbItemDesc对象
		TbItemDesc itemDesc = new TbItemDesc();
		// 5、补全TbItemDesc的属性
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		// 6、向商品描述表插入数据
		itemDescMapper.insert(itemDesc);
		// 7、TaotaoResult.ok()
		
		// 添加发送消息的业务逻辑
				jmstemplate.send(destination, new MessageCreator() {
					
					@Override
					public Message createMessage(Session session) throws JMSException {
						//发送的消息
						return session.createTextMessage(itemId+"");
					}
				});
				// 5.返回taotaoresult
				return TaotaoResult.ok();
				
	}
	@Override
	public TaotaoResult updateItem(TbItem item, String desc) {
		// 1、根据商品id，更新商品表，条件更新
		TbItemExample itemExample = new TbItemExample();
		Criteria criteria = itemExample.createCriteria();
		criteria.andIdEqualTo(item.getId());
		item.setStatus((byte) 1);
		mapper.updateByExampleSelective(item, itemExample);
		
		// 2、根据商品id，更新商品描述表，条件更新
		TbItemDesc itemDesc = new TbItemDesc();
		itemDesc.setItemDesc(desc);
		TbItemDescExample itemDescExample = new TbItemDescExample();
		com.taotao.pojo.TbItemDescExample.Criteria createCriteria =itemDescExample.createCriteria();
		createCriteria.andItemIdEqualTo(item.getId());
		itemDescMapper.updateByExampleSelective(itemDesc, itemDescExample);
		
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult getItemDesc(Long itemId) {
		TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		return TaotaoResult.ok(itemDesc);
	}
	

	/*
	 * 根据id，更新商品状态1-正常，2-下架，3-删除
	 */
	@Override
	public TaotaoResult updateItemStatus(List<Long> ids, String method) {
		TbItem item = new TbItem();
		if (method.equals("reshelf")) {
			// 正常，更新status=3即可
			item.setStatus((byte) 1);
		} else if (method.equals("instock")) {
			// 下架，更新status=3即可
			item.setStatus((byte) 2);
		} else if (method.equals("delete")) {
			// 删除，更新status=3即可
			item.setStatus((byte) 3);
		}
		
		for (Long id : ids) {
			// 创建查询条件，根据id更新
			TbItemExample tbItemExample = new TbItemExample();
			Criteria criteria = tbItemExample.createCriteria();
			criteria.andIdEqualTo(id);
			// 第一个参数 是要修改的部分值组成的对象，其中有些属性为null则表示该项不修改。
			// 第二个参数 是一个对应的查询条件的类， 通过这个类可以实现 order by 和一部分的where 条件。
			mapper.updateByExampleSelective(item, tbItemExample);
		}
		return TaotaoResult.ok();
	}
	/**
	 * 根据商品id查询商品详情
	 */
	@Override
	public TbItem getItemById(Long itemId) {
		
		//1添加缓存
		
		//2从缓存中获取数据，如果有直接返回
		try {
			String jsonstr = client.get(ITEM_INFO_KEY+":"+itemId+":BASE");
			if (StringUtils.isNoneBlank(jsonstr)) {
				//重新设置有效期
				client.expire(ITEM_INFO_KEY+	":"+itemId+":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItem.class);
				
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//3如果没有
		// 4注入mapper
		//调用dao方法
		TbItem tbItem = mapper.selectByPrimaryKey(itemId);
		//返回tbitem
		
		//3添加缓存到redis数据库中
			//注入jedisclient
		//ITEM_INFO:123456:BASE
		//ITEM_INFO:123456:DESC
		try {
			client.set(ITEM_INFO_KEY+":"+itemId+":BASE", JsonUtils.objectToJson(tbItem));
			//设置缓存的有效期
			client.expire(ITEM_INFO_KEY+	":"+itemId+":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return tbItem;
	}
	/**
	 * 根据商品id查询商品描述
	 */
	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		
		//1添加缓存
		
				//2从缓存中获取数据，如果有直接返回
				try {
					String jsonstr = client.get(ITEM_INFO_KEY+":"+itemId+":DESC");
					if (StringUtils.isNoneBlank(jsonstr)) {
						//重新设置有效期
						client.expire(ITEM_INFO_KEY+":"+itemId+":DESC", ITEM_INFO_KEY_EXPIRE);
						return JsonUtils.jsonToPojo(jsonstr, TbItemDesc.class);
						
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				//如果没有查到数据，从数据库中查询
				TbItemDesc itemDesc = itemDescMapper.selectByPrimaryKey(itemId);
				//3添加缓存到redis数据库中
				//注入jedisclient
			//ITEM_INFO:123456:BASE
			//ITEM_INFO:123456:DESC
			try {
				client.set(ITEM_INFO_KEY+":"+itemId+":DESC", JsonUtils.objectToJson(itemDesc));
				//设置缓存的有效期
				client.expire(ITEM_INFO_KEY+":"+itemId+":DESC", ITEM_INFO_KEY_EXPIRE);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			return itemDesc;
	}

}
