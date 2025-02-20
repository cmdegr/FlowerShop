package com.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.po.Order;

@Repository("cartDao")
@Mapper
public interface CartDao {
	public int focus(Map<String, Integer> map);//关注商品
	public String isFocus(int uid);//是否关注
	public int focusNum(int uid); //关注的人数上升一个
	public int orderConfirm(Order order); //下单,但是还没有支付
	public int orderDetail(Map<String, Object> map); //下单,但是还没有支付
	public List<Integer> getBaseId(int uid);
	public int updateInventory(Map<String, Object>map) ;
}
