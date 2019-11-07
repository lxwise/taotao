package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.ExceptionUtil;
import com.taotao.common.utils.JsonUtils;
import com.taotao.sso.service.UserLoginService;

@Controller
public class UserLoginController {
	@Autowired
	private UserLoginService loginservice;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	/**
	 * url:/user/login
	 * 参数：username password
	 * 返回值：json
	 * 请求限定的方法：post
	 */
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(HttpServletRequest request,HttpServletResponse response,String username,String password){
		//1.引入服务
		//2.注入服务
		//3.调用服务
		TaotaoResult result = loginservice.login(username, password);
		//4.需要设置token到cookie中 可以使用 工具类  cookie需要跨域
		if(result.getStatus()==200){
			CookieUtils.setCookie(request, response,TT_TOKEN_KEY, result.getData().toString());
		}
		return result;
	}
	
	/**
	 * url:/user/token/{token}
	 * 参数：token
	 * 返回值：json 
	 * 请求限定的方法：get
	 */
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		
		//判断是否是Jsonp请求
		if(StringUtils.isNotBlank(callback)){
			//如果是jsonp 需要拼接 类似于fun({id:1});
			TaotaoResult result = loginservice.getUserByToken(token);
			String jsonpstr = callback+"("+JsonUtils.objectToJson(result)+")";
			return jsonpstr;
		}
		//如果不是jsonp
		//1.调用服务
		TaotaoResult result = loginservice.getUserByToken(token);
		return JsonUtils.objectToJson(result);
	}

	/**
	 * url:/user/logout/{token}
	 * 参数：token
	 * 返回值json
	 * 
	 */
	/*
	    * 用户登出
	    * */
	    @RequestMapping("/user/logout/{token}")
	    @ResponseBody
	    public Object logout(@PathVariable String token,String callback){
	        try {
	            TaotaoResult result = loginservice.logout(token);
	            //这里采用了MappingJacksonValue解决jsonp跨域调用问题 springMVC 4.1+
	            if (StringUtils.isNotBlank(callback)) {
	                //创建MappingJacksonValue对象进行result对象包装
	                MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(result);
	                //配置回调函数名
	                mappingJacksonValue.setJsonpFunction(callback);
	                return mappingJacksonValue;
	            }
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return TaotaoResult.build(500,ExceptionUtil.getStackTrace(e));
	        }
	    }
}
