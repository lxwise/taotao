/*package com.taotao.search.test;

import java.util.Collection;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrTest {
	@Test
	public void add() throws Exception{
		//创建solrserver 建立连接需要的地址
		SolrServer	solrServer =new HttpSolrServer("http://192.168.25.128:8080/solr");
		//创建solrinputdocument
		SolrInputDocument document = new SolrInputDocument();
		//向文件中添加域
		document.addField("id", "test01");
		document.addField("item_title", "这是一个测试");
		
		//将文档提交到索引中
		solrServer.add(document);
		//提交
		solrServer.commit();
	}
	@Test
	public void testquery() throws Exception{
		//创建solrserver 建立连接需要的地址
		SolrServer	solrServer =new HttpSolrServer("http://192.168.25.128:8080/solr");
		//创建solrquery对象 设置各种过滤条件 主查询条件 排序
		SolrQuery query = new SolrQuery();
		//设置查询条件
		query.setQuery("阿尔卡特");
		query.addFacetQuery("item_price:[0 TO 3000]");
		//默认搜索域
		query.set("df", "item_title");
		//执行查询
		QueryResponse response = solrServer.query(query);
		//获取结果集
		SolrDocumentList results = response.getResults();
		System.out.println("查询的总记录数"+results.getNumFound());
		//遍历结果集 打印
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("item_title"));
		}
	}
}
*/