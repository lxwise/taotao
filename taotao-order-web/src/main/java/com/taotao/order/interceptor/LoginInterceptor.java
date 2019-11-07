package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.pojo.TbUser;
import com.taotao.sso.service.UserLoginService;
/**
 * 判断用户是否登录的拦截器
 * @author liuxin
 *
 */
public class LoginInterceptor implements HandlerInterceptor {

	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	@Value("${SSO_URL}")
	private String SSO_URL;
	@Autowired
	private UserLoginService userLoginService;
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 执行handler之前先执行此方法  handler是方法controller是类
		//判断用户是否登录
		//1从cookie中取token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//2.如果取不到token
		if (StringUtils.isBlank(token)) {//response.sendRedirect重定向跨域
		//取当前请求的URL
			String requestURL = request.getRequestURL().toString();
			//跳转到sso登录页面，需要把当前请求的URL作为参数传递给sso，sso登录成功之后跳转回请求页面
		response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			//拦截
			return false;
		}
		//3.取到token，调用sso系统的服务判断用户是否登录
		TaotaoResult taotaoResult = userLoginService.getUserByToken(token);
		//taotaoresult里面判断是否登录成功  200成功  400没有取到
		if (taotaoResult.getStatus() !=200) {
			//4.如果用户未登录，即没取到用户信息，跳转到sso登录页面，需要把当前请求的URL作为参数传递给sso，sso登录成功之后跳转回请求页面
			//取当前请求的URL
			String requestURL = request.getRequestURL().toString();
			//跳转到sso登录页面，需要把当前请求的URL作为参数传递给sso，sso登录成功之后跳转回请求页面
		response.sendRedirect(SSO_URL + "/page/login?url=" + requestURL);
			//拦截
			return false;
		}
		//5.取到用户信息。放行
		//把用户信息放到request中 对象转成字符串
		TbUser user = (TbUser) taotaoResult.getData();
		request.setAttribute("user", user);
		//返回值 true 放行，返回false拦截
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// handler执行之后 modelAndview返回之后

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// 在modelAndviw返回之后，异常处理

	}

}
