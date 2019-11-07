package com.taotao.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import com.taotao.item.pojo.Item;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

import freemarker.template.Configuration;
import freemarker.template.Template;
/**
 * 监听器
 * 获取消息
 * 执行生成静态页面的业务逻辑
 * @author liuxin
 *
 */
public class ItemChangeGenHtmlMessageListener implements MessageListener {
	@Autowired
	private ItemService itemservice;
	
	@Autowired 
	private FreeMarkerConfigurer config;
	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			//1获取消息，商品的id
			TextMessage message2 = (TextMessage) message;
			try {
				Long itemId = Long.valueOf(message2.getText());
				//2从数据库中获取数据，可以调用manager中的服务，获取到了数据集
					//引入服务
					//注入服务
					//调用
				TbItem tbItem = itemservice.getItemById(itemId);
				Item item = new Item(tbItem);//转成在页面显示时的pojo
				TbItemDesc tbItemDesc = itemservice.getItemDescById(itemId);
				//3生成静态页面，准备好模板和数据集
				genHtmlFreemarker(item,tbItemDesc);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}

	}
	//生成静态页面
	private void genHtmlFreemarker(Item item, TbItemDesc tbItemDesc) throws Exception{
		//1获取configuration对象
		Configuration configuration = config.createConfiguration();
		
		//2.创建模板 获取模板文件对象
		Template template = configuration.getTemplate("item.ftl");
		//3创建数据集
		Map model = new HashMap<>();
		model.put("item", item);
		model.put("itemdesc", tbItemDesc);
		
		//输出
		Writer writer = new FileWriter(new File("E:\\study\\freemarker\\item"+"\\"+item.getId()+".html"));
		template.process(model, writer);
		
		//关闭流
		writer.close();
	}

}
