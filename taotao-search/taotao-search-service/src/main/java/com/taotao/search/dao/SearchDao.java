package com.taotao.search.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.taotao.common.pojo.SearchItem;
import com.taotao.common.pojo.SearchResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;

/**
 * 从索引库中搜索商品的dao
 * @author liuxin
 *
 */
/*@Repository该注解的作用不只是将类识别为Bean，
 * 同时它还能将所标注的类中抛出的数据访问异常封装为 Spring 的数据访问异常类型。
 */
@Repository
public class SearchDao {
	@Autowired
	private SolrServer solrServer;
	
	@Autowired
	private SearchItemMapper mapper;
	/**
	 * //根基查询条件查询结果集
	 * @param query
	 * @return
	 * @throws Exception
	 */
	public SearchResult search(SolrQuery query) throws Exception{
		SearchResult searchResult = new SearchResult();
		//创建solrserver对象 由spring管理注入
		//直接查询
		QueryResponse response = solrServer.query(query);
		//获取结果集
		SolrDocumentList results = response.getResults();
		//设置searchresuilt的总记录数
		searchResult.setRecordCount(results.getNumFound());
		//遍历结果集
		//定义一个集合
		List<SearchItem> itemlist = new ArrayList<>();
		
		//取高亮
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		
		for (SolrDocument solrDocument : results) {
			//将solrDocument中的属性一个个的设置到searchItem中
			SearchItem item =new SearchItem();
			item.setCategory_name(solrDocument.get("item_category_name").toString());
			item.setId(Long.parseLong(solrDocument.get("id").toString()));
			item.setImage(solrDocument.get("item_image").toString());
			//item.setItem_desc(item_desc)
			item.setPrice((Long)solrDocument.get("item_price"));
			item.setSell_point(solrDocument.get("item_sell_point").toString());
			//取高亮
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			//判断是否为空
			String gaoliangstr = "";
			if (list!=null && list.size()>0) {
				//有高亮
				gaoliangstr=list.get(0);
			}else {
				gaoliangstr = solrDocument.get("item_title").toString();
			}
			item.setTitle(gaoliangstr);
			//searchItem 封装到searchResult的itemlist属性中
			itemlist.add(item);
		}
		//设置searchresult的属性
		searchResult.setItemList(itemlist);
		return searchResult;
	}
	/**
	 * 更新索引库
	 * @param itemId
	 * @return
	 * @throws Exception
	 */
	public TaotaoResult updateSearchItemById(Long itemId) throws Exception{
		//注入mapper
		//查询到记录
		SearchItem item = mapper.getSearchItemById(itemId);
		//把记录更新到索引库
			//创建solrserver 注入进来
			//创建solrinputdocument对象
			SolrInputDocument document = new SolrInputDocument();
			//向文档对象中添加域 
			document.addField("id", item.getId().toString());//这里是字符串需要转换
			document.addField("item_title", item.getTitle());
			document.addField("item_sell_point", item.getSell_point());
			document.addField("item_price", item.getPrice());
			document.addField("item_image", item.getImage());
			document.addField("item_category_name", item.getCategory_name());
			document.addField("item_desc", item.getItem_desc());
			//向索引库中添加文档
			solrServer.add(document);
			//提交	
			solrServer.commit();
		return TaotaoResult.ok();
	}
}

