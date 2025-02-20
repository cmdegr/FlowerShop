package com.service.before;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.dao.AddressDao;
import com.dao.UserCenterDao;
import com.po.Address;
import com.po.Buser;
import com.po.Order;
import com.po.pojo.CenterOrder;
import com.po.pojo.Password;
import com.util.MyUtil;

@Service("userCenterService")
@Transactional
public class UserCenterServiceImpl implements UserCenterService{
	@Autowired
	private UserCenterDao userCenterDao;
	@Autowired
	private AddressDao addressDao;
	
	//显示关注
	@Override
	public String showFocus(Model model, HttpSession session,Integer pageCur) {
		Buser buser=(Buser) session.getAttribute("bruser");
		Integer userId=buser.getId();
		String focus=userCenterDao.myFocus(userId);
		//System.out.println("hehe"+focus);
		if(!focus.equals("")) {
			List<Map<String, Object>> focuslist=new ArrayList<Map<String, Object>>();
			String[] str = focus.split("\\|"); // “|”为特殊字符，需转义后截取
			//分页
			int totalCount=str.length;//关注商品的总数量
			//System.out.println("我的商品总数:"+totalCount);
			int totalPage = 0;//总页数
			if (totalCount == 0) {
				totalPage = 0;
			} else {
				//返回大于或者等于指定表达式的最小整数(改)
				totalPage = (int) Math.ceil((double) totalCount / 6);
			}
			if (pageCur == null) {
				pageCur = 1;//当前页
			}
			if ((pageCur - 1) * 6 > totalCount) {
				pageCur = pageCur - 1;
			}
			int startIndex=(pageCur - 1) * 6;////起始位置，起始索引0
			int perPageSize;
			if(totalCount-startIndex>=6) {
				perPageSize=startIndex+6;//每页6个
			}else {
				perPageSize=totalCount;
			}
			//查商品
			for(int i=startIndex;i<perPageSize;i++) {
				//System.out.println("ha:"+str[i]);
				Integer goodId=Integer.parseInt(str[i]);
				focuslist.add(userCenterDao.myFocusMessage(goodId));
			}
			model.addAttribute("pageCur", pageCur);
			model.addAttribute("totalPage", totalPage);
			model.addAttribute("focuslist", focuslist);
		}else {
			model.addAttribute("message", "您还没有关注过商品！");
		}
		//在加载的关注时候同时加载地址
		session.setAttribute("addressList", addressList(session));
		return "before/userCenter";
	}
	
	//删除关注
	@Override
	public String deleteFocus(Integer goodId,Model model, HttpSession session,Integer pageCur) {
		Buser buser=(Buser) session.getAttribute("bruser");
		Integer userId=buser.getId();
		String focus=userCenterDao.myFocus(userId);
		//没有关注的话，不会显示删除按钮，此处不加判断
		List<Map<String, Object>> focuslist=new ArrayList<Map<String, Object>>();
		String[] str = focus.split("\\|"); // “|”为特殊字符，需转义后截取
		String[] string=new String[str.length-1];
		int index=0;
		String result = "";
		for(int i=0;i<str.length;i++) {
			Integer gid=Integer.parseInt(str[i]);
			if(gid==goodId) {
				userCenterDao.deleteFocusNum(gid);
				System.out.println("跳过！");
			}else {
				string[index++]=str[i];
				//focuslist.add(userCenterDao.myFocusMessage(gid));
				result=result+str[i]+"|";
			}
		}
		Map<String, Object> map=new HashMap<String, Object>();
		map.put("id",userId);
		map.put("focus", result);
		userCenterDao.deleteFocus(map);
	
		for(int i=0;i<string.length;i++)
		{
			System.out.println("hehe"+string[i]);
		}
		//分页
		int totalCount=string.length;//关注商品的总数量
		//System.out.println("我的商品总数:"+totalCount);
		int totalPage = 0;//总页数
		if (totalCount == 0) {
			totalPage = 0;
		} else {
			//返回大于或者等于指定表达式的最小整数(改)
			totalPage = (int) Math.ceil((double) totalCount / 6);
		}
		if (pageCur == null) {
			pageCur = 1;//当前页
		}
		if ((pageCur - 1) * 6 > totalCount) {
			System.out.println("起始位置比商品总数还多了");
			pageCur = pageCur - 1;
		}
		int startIndex=(pageCur - 1) * 6;////起始位置，起始索引0
		int perPageSize;
		if(totalCount-startIndex>=6) {
			perPageSize=startIndex+6;//每页6个
		}else {
			perPageSize=totalCount;
		}
		//查商品
		for(int i=startIndex;i<perPageSize;i++) {
			Integer gid1=Integer.parseInt(string[i]);
			focuslist.add(userCenterDao.myFocusMessage(gid1));
		}
		
		if(focuslist.isEmpty()) {
			model.addAttribute("message", "您还没有关注过商品！");
		}else {
			model.addAttribute("pageCur", pageCur);
			model.addAttribute("totalPage", totalPage);
			model.addAttribute("focuslist", focuslist);
		}
		return "before/userCenter";
	}
	
	//修改用户密码
	public Password updatePassword(Password password,HttpSession session) {
		String oldpassword=password.getOldPassword();
		String newpassword=password.getNewPassword();
		Buser buser=(Buser)session.getAttribute("bruser");
		String oldpassword1=userCenterDao.selectPassword(buser.getId());
		if(oldpassword1.equals(oldpassword)) { 
			userCenterDao.updateUserPassword(buser.getId(), newpassword, oldpassword);
			password.setMsgPassword("密码修改成功！");
			return password; 
		}
		else{ 
			password.setMsgPassword("旧密码输入错误！修改失败！");
			return password; 
		}		
	}

	//显示所有已支付订单,删除所有已支付订单
	@Override
	public CenterOrder showPayOrder(CenterOrder centerOrder,HttpSession session) {
		if(centerOrder.getDeleteId()!=null) {
			userCenterDao.deleteOrders(centerOrder.getDeleteId());
		}
		Buser buser=(Buser) session.getAttribute("bruser");
		Integer userId=buser.getId();
		List<Order> orders=userCenterDao.showPayOrder(userId);
		if(!orders.isEmpty()) {
			//分页
			int totalCount=orders.size();
			int totalPage = 0;//总页数
			if (totalCount == 0) {
				totalPage = 0;
			} else {
				//返回大于或者等于指定表达式的最小整数(改)
				totalPage = (int) Math.ceil((double) totalCount / 7);
			}
			if ((centerOrder.getPageCur() - 1) * 7 >= totalCount) {
				centerOrder.setPageCur(centerOrder.getPageCur()-1);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("startIndex", (centerOrder.getPageCur() - 1) * 7);//起始位置
			map.put("perPageSize", 7);//每页7个
			map.put("id", userId);
			orders = userCenterDao.showPayOrderByPage(map);
			centerOrder.setOrders(orders);
			centerOrder.setTotalPage(totalPage);
		}else {
			centerOrder.setMsg("您还没有已支付的订单！");
		}
		return centerOrder;
	}
	
	//显示所有未支付订单,删除所有已支付订单
	@Override
	public CenterOrder showNoPayOrder(CenterOrder centerOrder,HttpSession session) {
		if(centerOrder.getDeleteId()!=null) {
			Integer deleteId=centerOrder.getDeleteId();
			userCenterDao.deleteOrders(deleteId);
			//释放库存
			List<Map<String, Object>> list=userCenterDao.selectGoods(deleteId);
			for(int i=0;i<list.size();i++) {
				userCenterDao.changeGstore(list.get(i));
			}
		}
		Buser buser=(Buser) session.getAttribute("bruser");
		Integer userId=buser.getId();
		List<Order> orders=userCenterDao.showNoPayOrder(userId);
		if(!orders.isEmpty()) {
			//分页
			int totalCount=orders.size();
			int totalPage = 0;//总页数
			if (totalCount == 0) {
				totalPage = 0;
			} else {
				//返回大于或者等于指定表达式的最小整数(改)
				totalPage = (int) Math.ceil((double) totalCount / 7);
			}
			if ((centerOrder.getPageCur() - 1) * 7 >= totalCount) {
				centerOrder.setPageCur(centerOrder.getPageCur()-1);
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("startIndex", (centerOrder.getPageCur() - 1) * 7);//起始位置
			map.put("perPageSize", 7);//每页7个
			map.put("id", userId);
			orders = userCenterDao.showNoPayOrderByPage(map);
			centerOrder.setOrders(orders);
			centerOrder.setTotalPage(totalPage);
		}else {
			centerOrder.setMsg("您还没有未支付的订单！");
		}
		return centerOrder;
	}

	
	public List<Address> addressList(HttpSession session) {
		List<Address> list = null;
		list = addressDao.selectAllAddress(MyUtil.getUserId(session));
		if (list==null || list.size()<=0) {
			System.out.println("地址表为空");
			return list;
		}
		
		String phone =  list.get(0).getReceiverphone();
		System.out.println("这是收货人的phone: "+phone);
		return list;
	}
}
