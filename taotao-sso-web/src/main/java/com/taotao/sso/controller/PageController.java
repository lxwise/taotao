package com.taotao.sso.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {
	@RequestMapping("/page/{page}")
	public	String showPage(@PathVariable String page,String url,Model model) {
		//接收订单登录判断传递的url
		model.addAttribute("redirect",url);
		return page;
	}
}
