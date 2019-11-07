package com.taotao.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.json.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.JsonUtils;
import com.taotao.content.jedis.JedisClient;
import com.taotao.content.service.ContentService;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private JedisClient client;
	
	@Autowired
	private TbContentMapper mapper;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	@Override
	public TaotaoResult saveContent(TbContent content) {
		//1注入mapper
		//2补全其他属性
		content.setCreated(new Date());
		content.setUpdated(content.getCreated());
		//3插入内容表
		mapper.insertSelective(content);
		
		//当添加内容的时候，需要清空此内容所属的分类下的所以缓存
		try {
			client.hdel(CONTENT_KEY, content.getCategoryId()+"");
			System.out.println("当插入时清空缓存!!!");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return TaotaoResult.ok();
	}
	@Override
	public EasyUIDataGridResult getContentList(Long categoryId, Integer page, Integer rows) {
		
		//设置分页信息
		if(page==null)page=1;
		if(rows==null)rows=5;
		PageHelper.startPage(page, rows);
		//根据分类Id查询
		TbContentExample example =new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		//执行查询
		List<TbContent> list = mapper.selectByExample(example);
		//取分页信息
		PageInfo<TbContent> info = new PageInfo<>(list);
		//创建返回结果对象
		EasyUIDataGridResult result = new EasyUIDataGridResult();
		result.setTotal((int)info.getTotal());
		result.setRows(info.getList());
		return result;
		
	}
	/**
	 * 内容编辑
	 */
	@Override
	public TaotaoResult updateContent(Long id,TbContent content) {
		//根据商品ID获取内容content
		TbContent tbContent = mapper.selectByPrimaryKey(id);
		
		//更新编辑内容
		content.setCreated(new Date());
		content.setUpdated(content.getCreated());
		
		mapper.updateByPrimaryKey(content);
		//当编辑内容的时候，需要清空此内容所属的分类下的所以缓存
				try {
					client.hdel(CONTENT_KEY, content.getCategoryId()+"");
					System.out.println("当编辑时清空缓存!!!");
				} catch (Exception e) {
					
					e.printStackTrace();
				}
		return TaotaoResult.ok(content);
	}
	@Override
	public TaotaoResult deleteContent(String [] ids) {
		for(String id  :ids) {
			mapper.deleteByPrimaryKey(Long.valueOf(id));
		}
		//当删除内容的时候，需要清空此内容所属的分类下的所以缓存
		try {
			client.hdel(CONTENT_KEY, ids);
			System.out.println("当编辑时清空缓存!!!");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return TaotaoResult.ok();
	}
	@Override
	public List<TbContent> getContentListByCatId(Long categoryId) {
		//添加缓存不能影响正常的业务逻辑
		//判断是否redis中有数据如果有直接从redis中获取数据返回，如果没有则去数据库找中
		try {
			String jsonstr = client.hget(CONTENT_KEY, categoryId+"");//从redis数据库中获取内容分类下的所有的内容。
			//如果存在，说明有缓存
			//StringUtils.isNoneBlank(jsonstr)判断字符串是否为空
			if(StringUtils.isNotBlank(jsonstr)){
			System.out.println("这里有缓存啦！！！！！");
				return JsonUtils.jsonToList(jsonstr, TbContent.class);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		//1.注入mapper
		//2创建example
		TbContentExample example = new TbContentExample();
		//3设置查询条件 =select * from tbcontent where category id=1
		example.createCriteria().andCategoryIdEqualTo(categoryId);
		//4执行查询
		List<TbContent> list = mapper.selectByExample(example);
		//返回
		
		//将数据写入redis数据库
		//注入JedisClient
		//、调用方法写入redis key value
		//categoryId+"",将Long转换成字符串。因为任何数字变量连接一个空字符串，
		//都会自动执行toString方法转换为字符串。
		try {
			System.out.println("没有缓存！！！！！！");
			client.hset(CONTENT_KEY, categoryId+"", JsonUtils.objectToJson(list));
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return list;
	}

}
