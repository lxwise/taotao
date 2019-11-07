package com.taotao.search.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.dao.SearchDao;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

/**
 * 商品数据导入索引库
 * <p>Title: SearchItemServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class SearchItemServiceImpl implements SearchItemService {

	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	@Autowired
	private SearchDao searchDao;
	
	@Override
	public TaotaoResult importItemsToIndex() throws Exception{
		
			//1、先查询所有商品数据
			List<SearchItem> itemList = searchItemMapper.getItemList();
			//2、遍历商品数据添加到索引库
			for (SearchItem searchItem : itemList) {
				//创建文档对象
				SolrInputDocument document = new SolrInputDocument();
				//向文档中添加域
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				document.addField("item_desc", searchItem.getItem_desc());
				//把文档写入索引库
				solrServer.add(document);
			}
			//3、提交
			solrServer.commit();
		
		//4、返回添加成功
		return TaotaoResult.ok();
	}

	@Override
	public SearchResult search(String queryString, Integer page, Integer rows) throws Exception {
		//创建一个solrquery对象
		SolrQuery query = new SolrQuery();
		//设置主查询条件
		if (StringUtils.isNoneBlank(queryString)) {
			query.setQuery(queryString);
		}else {
			query.setQuery("*:*");
		}
		//设置过滤条件设置分页
		if(page==null)page=1;
		if(rows==null)rows=10;
		query.setStart((page-1)*rows);//page-1 * rows
		query.setRows(rows);
		//设置默认搜索域
		query.set("df", "item_keywords");
		//设置高亮
			//1开启高亮
		query.setHighlight(true);
			//2前缀
		query.setHighlightSimplePre("<em style=\"color:red\">");
			//3后缀
		query.setHighlightSimplePost("</em>");
		//设置高亮的域
		query.addHighlightField("item_title");

		//调用dao方法 返回searchresult 包含总记录数和商品列表
		SearchResult search = searchDao.search(query);
		//设置searchresult的总页数=总记录数/每页显示行数
		long pageCount = 01;
		pageCount = search.getRecordCount()/rows;
		if(search.getRecordCount()%rows>0) {
			pageCount++;
		}
		search.setPageCount(pageCount);
		//返回
		return search;
	}

	@Override
	public TaotaoResult updateSearchItemById(Long itemId) throws Exception {
		return searchDao.updateSearchItemById(itemId);
	}
}
