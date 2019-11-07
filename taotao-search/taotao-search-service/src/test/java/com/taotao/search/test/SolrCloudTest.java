/*package com.taotao.search.test;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrCloudTest {
	@Test
	public void testAdd() throws Exception{
		//创建soloserver 集群的实现类
		//指定zookeeper集群的节点列表字符串
		CloudSolrServer cloudSolrServer = new CloudSolrServer("192.168.25.128:2181,192.168.25.128:2182,192.168.25.128:2183");
		// 第三步：需要设置DefaultCollection属性。
				cloudSolrServer.setDefaultCollection("collection2");
				// 第四步：创建一SolrInputDocument对象。
				SolrInputDocument document = new SolrInputDocument();
				// 第五步：向文档对象中添加域
				document.addField("item_title", "测试商品");
				document.addField("item_price", "100");
				document.addField("id", "test001");
				// 第六步：把文档对象写入索引库。
				cloudSolrServer.add(document);
				// 第七步：提交。
				cloudSolrServer.commit();

	}
}
*/