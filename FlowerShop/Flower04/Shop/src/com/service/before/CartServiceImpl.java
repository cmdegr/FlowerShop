package com.service.before;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import com.dao.CartDao;
import com.dao.IndexDao;
import com.po.Goods;
import com.po.Order;
import com.util.MyUtil;

@Service("cartService")
@Transactional
public class CartServiceImpl implements CartService {
	@Autowired
	private CartDao cartDao;

	@Autowired
	private IndexDao indexDao;

	/**
	 * 关注商品后回到详情页面
	 */
	@Override
	public String focus(Model model, Integer id, HttpSession session) {
		// public String focus(Integer id, HttpSession session) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("uid", MyUtil.getUserId(session));
		map.put("gid", id);
		// 查询是否关注,通过用户ID
		String focuString = cartDao.isFocus(MyUtil.getUserId(session));
		// 返回所有的关注, 把String 转换为list
		String[] strings = focuString.split("\\|");

		List<String> list = Arrays.asList(strings);

		Iterator iterator = list.iterator();
		while (iterator.hasNext()) {
			System.out.println("这是关注的list: " + iterator.next());
		}

		// 如果 list中已经有这个商品ID
		if (list.contains(id.toString())) {
			model.addAttribute("msg", "已关注该商品！");
		} else {
			int n = cartDao.focus(map);
			if (n > 0) {
				int result = cartDao.focusNum(id);
				if (result < 0) {
					System.out.println("出问题了啊啊啊啊, 这个商品" + id + "关注人数没有加1");
				}
				model.addAttribute("msg", "成功关注该商品！");
			} else {
				model.addAttribute("msg", "关注失败！");
			}

		}
		return "forward:/goodsDetail?id=" + id;
	}

	/**
	 * 加入购物车后回到购物车页面, id为商品id
	 */
	@Override
	public String putCart(HttpServletRequest request, HttpServletResponse response, Model model, Integer shoppingnum,
			Integer id, HttpSession session) {
		Map<String, Integer> map = (Map<String, Integer>) request.getSession().getAttribute("cart");
		String idString = id.toString();
		System.out.println("这是添加到购物车的商品id: " + idString);
		// 判断map是否为空
		if (map == null) {// 第一次购物
			// 创建购物车
			map = new HashMap<String, Integer>();
			// 把商品名称和数量放入到map中（购物车中）
			map.put(idString, shoppingnum);
			session.setAttribute("cartStatus", 1);
		} else {
			// 不是第一次购物
			// 说明session 中具有相同名称的 attribute
			// 检查是否有相同的key ,相当于检查是否有相同的商品
			if (map.containsKey(idString)) {
				// 有相同名称的key
				// 得到商品的数量，+shoppingnum
				int num = map.get(idString);
				// 把num+1，放到购物车
				map.put(idString, num + shoppingnum);
				session.setAttribute("cartStatus", 1);
			} else {
				// 没有相同的key
				// 直接把商品名和数量添加到购物车, 这里数量由用户输入
				map.put(idString, shoppingnum);
				session.setAttribute("cartStatus", 1);
			}
		}

		// 把购物车放到session里面
		request.getSession().setAttribute("cart", map);

		System.out.println("这是gid:" + id + "  数目:" + shoppingnum + "  用户ID: " + MyUtil.getUserId(session));
		System.out.println("这是购物车商品数量" + map.get(idString));
		model.addAttribute("msg", "购物车添加成功");
		return "forward:/goodsDetail?id=" + id;
	}

	/**
	 * 查询购物车
	 */
	@Override
	public String selectCart(Model model, HttpSession session) {
		System.out.println("===========");
		// 这个list要存储到model, 和session中
		// list 中每一个结点 都是一个Map
		// 每一个Map中 存储 一个String(里面存Goods 类的属性名称, 例如"id" "gname")作为key, Object(Goods 属性的值
		// 作为value 例如:"1" "桂花")
		List<Map<String, Object>> list = new LinkedList<Map<String, Object>>();

		// 获取购物车(id ,shoppingnum)
		Map<String, Integer> cartMap = (Map<String, Integer>) session.getAttribute("cart");
		//获取购物车状态
		Integer cartStatus = (Integer) session.getAttribute("cartStatus");
		// 如果用户session中没有购物车
		if (cartMap == null) {
			return "before/cart";
		}else if (cartMap!=null && cartStatus==0) {
			//如果用户有购物车,但是购物车内的信息不需要更新
			//从session中获取购物车mapList(形式), 并且把购物车, 放到model中
			list = (List<Map<String, Object>>) session.getAttribute("cartMapList");
			model.addAttribute("cartlist", list);
			return "before/cart";
		}
		
		//最后一种情况, 用户session中有购物车, 同时购物车信息有更新
		//我们更新购物车
		
		// 获取所有的购物车键 , 同样也是商品ID
		Set<String> cartMapSet = cartMap.keySet();
		Iterator<String> cartMapIterator = cartMapSet.iterator();

		// 将session中的 map(String ,num) 这里的键是商品ID --> map(String, Object)
		Map<String, Object> cartObjectMap = getMap(cartMap);
		Set<String> objectMapSet = cartObjectMap.keySet();
		Iterator<String> objectMapIterator = objectMapSet.iterator();

		// Map<String, Object> listNodeMap=null;
		Double priceInteger;
		Double smallsum;
		Integer shoppingnum;
		Integer idInteger;
		Goods tempGoods;
		Double sumCart = 0.0;

		while (objectMapIterator.hasNext() && cartMapIterator.hasNext()) {
			// 获取商品ID
			idInteger = Integer.parseInt(objectMapIterator.next());
			String idCart = cartMapIterator.next();
			tempGoods = (Goods) cartObjectMap.get(idCart);
			Map<String, Object> listNodeMap = new HashMap<String, Object>();

			// 获取数量, 使用session中map
			shoppingnum = cartMap.get(idInteger.toString());
			// System.out.println("这是找出来的商品name" + tempGoods.getGname());

			// 把生成的对象和购物数目存储在map中
			listNodeMap.put("id", idInteger);
			listNodeMap.put("gpicture", tempGoods.getGpicture());
			listNodeMap.put("gname", tempGoods.getGname());
			listNodeMap.put("gstore", tempGoods.getGstore());
			// 如果gdprice(折扣价) 为空, 则使用goprice
			if (tempGoods.getGdprice() == null) {
				priceInteger = tempGoods.getGoprice();
			} else {
				priceInteger = tempGoods.getGdprice();
			}

			smallsum = (Double) (priceInteger * shoppingnum);
			shoppingnum = cartMap.get(idInteger.toString());

			listNodeMap.put("grprice", priceInteger);
			listNodeMap.put("shoppingnum", shoppingnum);
			listNodeMap.put("smallsum", smallsum);
			//0表示没有被加入到总价中
			//1表示已经加入到总价中
			listNodeMap.put("statusTotalPrice", 0);

			// ${total}
			if ((Integer)listNodeMap.get("statusTotalPrice") == 0) {
				//购物车加上小计
				sumCart += smallsum;
				listNodeMap.put("statusTotalPrice", 1);
			}
			//sumCart += tempTotal;
			list.add(listNodeMap);
		}
		session.setAttribute("total", sumCart);
		//把生成的list放到session 中, 下次再次访问时, 就不需要在生成了.
		session.setAttribute("cartMapList", list);
		
		//将购物车状态改为0 , 意味着购物车状态已经更新完毕
		session.setAttribute("cartStatus", 0);
		
		model.addAttribute("cartlist", list);
		return "before/cart";
	}

	// 删除购物车中一个商品后回到购物车页面
	@Override
	public String deleteAgoods(Model model, Integer id, HttpSession session) {
		// 获取购物车
		Map<String, Integer> cartMap = (Map<String, Integer>) session.getAttribute("cart");
		System.out.println("这是将要删除的ID" + id);
		cartMap.remove(id.toString());
		model.addAttribute("cart", cartMap);
		//购物车状态需要更新了
		session.setAttribute("cartStatus", 1);

		return "forward:/cart/selectCart";
	}

	// 清除购物车后回到购物车页面
	@Override
	public String clear(HttpSession session) {
		Map<String, Integer> cartMap = (Map<String, Integer>) session.getAttribute("cart");
		cartMap.clear();
		//购物车状态需要更新了
		session.setAttribute("cartStatus", 1);
		return "forward:/cart/selectCart";
	}

	// 确认订单页面
	@Override
	public String orderConfirm(Model model, HttpSession session) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) session.getAttribute("cartMapList");
		model.addAttribute("cartlist", list);
		return "before/orderconfirm";
	}
	
	/**
	 * 修改商品的数量
	 */
	@Override
	public String changeNum(Model model,Integer id , Integer num, HttpSession session){
		Goods goods = getGoodsById(id);
		if (num>goods.getGstore() ) {
			//如果要修改的数量 大于库存数量, 就不修改了
			return "forward:/cart/selectCart";
		}
		
		//购物车状态需要更新了
		session.setAttribute("cartStatus",1);
		
		Map<String, Integer> cartMap = (Map<String, Integer>) session.getAttribute("cart");
		
		Integer oldNum = cartMap.get(id.toString());
		System.out.println("开始修改数量:  ");
		System.out.println("这是以前的数量:  " + oldNum + "  这是新的数量:" + num);
		
		cartMap.put(id.toString(), num);
		
		return "forward:/cart/selectCart";
	}
	
	//提交订单,并前往支付
	@Override
	public String toPay(Model model, HttpServletRequest request, HttpSession session, Integer id) {
		Order order = new Order();
		order.setBuseraddress_id(id);
		Integer uid = MyUtil.getUserId(session);
		order.setBusertable_id(uid);
		
		Double total= (Double) session.getAttribute("total");
		//如果总价为空, 则不继续操作
		if (total <=0 || total==null ) {
			return "forward:/cart/selectCart";
		}
		order.setAmount(total);
		System.out.println("这是总价: "+total);
		//生成购物车基础表
		cartDao.orderConfirm(order);
		
		//获取订单基础表的ID
		List<Integer>list= cartDao.getBaseId(MyUtil.getUserId(session));
	    Integer	baseId = list.get(0);
	    System.out.println("这是购物车base 表的id"+baseId);
	    
	    //遍历购物车
	    Map<String, Integer> cartMap = (Map<String, Integer>) session.getAttribute("cart");
	    Set<String> set= cartMap.keySet();
	    Iterator<String> iterator = set.iterator();
	    Double sumPrice=0.0;
	    while (iterator.hasNext()) {
	    	//获取商品
	    	String goodID = iterator.next();
	    	Integer num = cartMap.get(goodID);
	    	Goods tempGoods = getGoodsById(Integer.parseInt(goodID));
	    	Double priceInteger;
			// 如果gdprice(折扣价) 为空, 则使用goprice
			if (tempGoods.getGdprice() == null) {
				priceInteger = tempGoods.getGoprice();
			} else {
				priceInteger = tempGoods.getGdprice();
			}
	    	
			Map detailMap=new HashMap<String, Object>();
			detailMap.put("orderbasetable_id", baseId);
			detailMap.put("goodstable_id", goodID);
			detailMap.put("shoppingnum", num);
			detailMap.put("orderprice", priceInteger);
			sumPrice += (num*priceInteger);
			cartDao.orderDetail(detailMap);
			
			//减少库存数量
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", goodID);
			map.put("num", num);
			cartDao.updateInventory(map);
		}
	    System.out.println("这是订单ID: "+baseId+"这是商品总价:  "+ sumPrice);
	    model.addAttribute("orderId", baseId);
	    model.addAttribute("amount",sumPrice);
	    session.removeAttribute("total");
	    session.removeAttribute("cart");
	    session.removeAttribute("cartStatus");
	    session.removeAttribute("cartMapList");
	    
		return "before/pay";
	}
	
	

	/**
	 * 根据传入的id获取商品对象
	 * 
	 * @param id
	 * @return
	 */
	public Goods getGoodsById(Integer id) {
		Goods good = indexDao.selectGoodsById(id);

		return good;
	}

	/**
	 * 传入购物车map , key为 商品Id , value为商品数量; 返回值: map , key为商品Id, value为商品 对象
	 * 
	 * @param set
	 * @param idInteger
	 * @return
	 */
	public Map<String, Object> getMap(Map<String, Integer> cartMap) {
		// 对所有的商品ID遍历 ,并且生成对象
		// 存储商品的 对象
		Goods tempGoods = null;
		// 存储商品的id
		Integer idInteger = null;
		// list 中的一个结点 , 这是返回值
		Map<String, Object> map = new HashMap<String, Object>();
		// 获取购物车 所有键
		Set set = cartMap.keySet();

		Iterator<String> iterator = set.iterator();
		// 遍历所有的商品ID
		while (iterator.hasNext()) {
			idInteger = Integer.parseInt(iterator.next());
			tempGoods = getGoodsById(idInteger);
			System.out.println("这是keyID: " + idInteger.toString());
			//map.put(idInteger.toString(), tempGoods);
		}

//		for (Object value : map.values()) {
//			Goods goods = (Goods) value;
//			System.out.println("Value = " + goods.getGname());
//		}

		return map;
	}
	
}
