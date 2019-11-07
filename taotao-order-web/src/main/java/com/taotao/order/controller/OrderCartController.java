package com.taotao.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.utils.CookieUtils;
import com.taotao.common.utils.JsonUtils;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

/**
 * 订单确认页面处理controller
 * @author liuxin
 *
 */
@Controller
public class OrderCartController {
	@Value("${CART_KEY}")
	private String CART_KEY;
	@Value("${CART_EXPIER}")
	private Integer CART_EXPIER;
	@Autowired
	private OrderService orderService;
	/**
	 * 展示订单确认页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request) {
		//用户必须是登录状态
		//取用户id从cookie中获取用户的token,调用sso服务获取用户信息
		 TbUser user = (TbUser) request.getAttribute("user");
		 System.out.println(user.getUsername());
		//根据用户信息取收获地址列表，使用静态页面
		//把收获地址列表取出来传递给页面
		//从cookie中取购物车列表展示到页面
		List<TbItem> cartList = getCartItemList(request);
		//传递页面
		request.setAttribute("cartList", cartList);
		//返回逻辑视图
		return "order-cart";
	}
	private List<TbItem> getCartItemList(HttpServletRequest request) {
		//从cookie中取购物车商品列表
		String json = CookieUtils.getCookieValue(request, CART_KEY, true);
		if (StringUtils.isBlank(json)) {
			//如果没有内容，返回一个空的列表
			return new ArrayList<>();
		}
		List<TbItem> list = JsonUtils.jsonToList(json, TbItem.class);
		return list;
	}
	/**
	 * 生成订单处理
	 * @param orderInfo
	 * @return
	 */
	@RequestMapping(value="/order/create",method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo,Model model) {
		//引入服务
		//注入服务
		//生成订单
		TaotaoResult result = orderService.createOrder(orderInfo);
		//返回逻辑视图 1.订单id2.支付方式 3.送货到达时间
		model.addAttribute("orderId",result.getData().toString());
		model.addAttribute("payment",orderInfo.getPayment());
		//预计送达时间预计三天后到达
		//使用时间操作组件joda-time 当前日期加3天
		DateTime dateTime = new DateTime();//当前日期
		dateTime = dateTime.plusDays(3);//三天后日期
		model.addAttribute("date", dateTime.toString("yyyy-MM-dd"));
		
		return "success";
	}
}
