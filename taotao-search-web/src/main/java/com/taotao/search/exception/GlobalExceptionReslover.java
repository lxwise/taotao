package com.taotao.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
/**
 * 全局异常处理器
 * @author liuxin
 *
 */
public class GlobalExceptionReslover implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception ex) {
		//日志写入到日志文件中，打印
		System.out.println(ex.getMessage());
		ex.printStackTrace();
		//及时通知开发人员
		System.out.println("发短信");
		//给用户一个友好的体验
		ModelAndView modelAndView = new ModelAndView();
		//设置视图信息
		//设置模型数据
		modelAndView.setViewName("error/exception");//不需要带后缀有视图解析器
		modelAndView.addObject("message","您的网络有异常请重试");
		return modelAndView;
	}

}
