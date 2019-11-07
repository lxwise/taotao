package com.taotao.item.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Controller
public class Genhtml {

	@Autowired
	private FreeMarkerConfigurer config;
	
	@RequestMapping("/genHtml")
	@ResponseBody
	public String gen() throws Exception{
		//生成静态页面
		//1.根据config 获取configuration对象
		Configuration configuration = config.createConfiguration();
		//2设置模板文件，加载模板文件
		Template template = configuration.getTemplate("template.ftl");
		//3创建数据集 》》从数据库中获取
		Map model =	new HashMap<>();
		model.put("springtestkey", "hello");
		//4.创建writer
		Writer writer = new FileWriter(new File("E:/freemarker/springtest.html"));		
		//5调用方法输出
		template.process(model, writer);
		//6关闭流
		writer.close();
		
		return "ok";
	}
}
