package com.controller.before;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.po.Buser;
import com.service.before.CartService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.util.MyUtil;

@Controller
public class CartController extends BaseBeforeController{
	@Autowired
	private CartService cartService;

	//关注
	@RequestMapping("/cart/focus")
	public String focus(Model model,Integer id, HttpSession session){
		return cartService.focus(model, id, session);
	}

	//加入购物车
	@RequestMapping("/cart/putCart")
	public String putCart(HttpServletRequest request,HttpServletResponse response,Model model,Integer shoppingnum, Integer id,HttpSession session) {
		return cartService.putCart(request, response,model,  shoppingnum,id,session);
	}

	//查看购物车
	@RequestMapping("/cart/selectCart")
	public String selectCart(Model model, HttpSession session) {
		return cartService.selectCart(model, session);
	}
	//删除一个商品
	@RequestMapping("/cart/deleteAgoods")
	public String deleteAgoods(Model model,Integer id,HttpSession session) {
		return cartService.deleteAgoods(model,id, session);
	}
	//清空购物车
	@RequestMapping("/cart/clear")
	public String clear(HttpSession session) {
		return cartService.clear(session);
	}
	//确认订单
	@RequestMapping("/cart/orderConfirm")
	public String orderConfirm(Model model, HttpSession session) {
		return cartService.orderConfirm(model, session);
	}
	
	//修改数量
	@RequestMapping("/cart/changeNum")
	public String changeNum(Model model,Integer id , Integer num, HttpSession session) {
		return cartService.changeNum(model, id, num, session);
	}
	
}
