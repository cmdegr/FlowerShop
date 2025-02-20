package com.service.before;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;

public interface CartService {
	public String focus(Model model,Integer id, HttpSession session);//关注商品后回到详情页面
	
	public String putCart(HttpServletRequest request,HttpServletResponse response,Model model,Integer shoppingnum, Integer id, HttpSession session);//加入购物车后回到购物车页面
	public String selectCart(Model model, HttpSession session);//查询购物车
	public String deleteAgoods(Model model,Integer id, HttpSession session);//删除购物车中一个商品后回到购物车页面
	public String clear(HttpSession session);//清除购物车后回到购物车页面
	public String orderConfirm(Model model, HttpSession session);//订单信息页面
	
	public String changeNum(Model model, Integer id, Integer num, HttpSession session); //修改商品的数量
	public String toPay(Model model,HttpServletRequest request, HttpSession session,Integer id);
}
