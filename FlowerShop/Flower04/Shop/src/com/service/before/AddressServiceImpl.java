package com.service.before;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.dao.AddressDao;
import com.po.Address;
import com.util.MyUtil;

@Service("addressService")
@Transactional
public class AddressServiceImpl implements AddressService{
	@Autowired
	private AddressDao addressDao;
	
	@Override
	public String addAddress(Model model, HttpServletRequest request, HttpSession session,Address address) {
		address.setBusertable_id(MyUtil.getUserId(session));
		Integer result = addressDao.addAddress(address);
		if (result > 0) {
			model.addAttribute("msg", "地址添加成功！");
			System.out.println("添加地址成功");
		}else {
			model.addAttribute("msg", "添加失败！");
			System.out.println("GG, 失败了");
		}
		return "before/userCenter";
	}

	@Override
	public String deleteAddress(Model model, HttpServletRequest request, HttpSession session,Integer id) {
		addressDao.deleteAddress(id);
		return "before/userCenter";
	}

	@Override
	public String updateAddress(Model model, HttpServletRequest request, HttpSession session,Address address) {
		address.setBusertable_id(MyUtil.getUserId(session));
		System.out.println("这是地址"+address.getAddress());
		System.out.println("这是收货人: "+address.getReceiver());
		addressDao.updateAddress(address);
		return "redirect:/userCenter";
	}

	@Override
	public String selectAllAddress(Model model, HttpSession session) {
		List<Address> list = addressDao.selectAllAddress(MyUtil.getUserId(session));
		session.setAttribute("addressList", list);
		
		for (Address address : list) {
			System.out.println("这是遍历地址, 收货人: " + address.getReceiver());
		}
		
		return "before/userCenter";
	}

	@Override
	public String selectAddress(Model model, HttpServletRequest request, HttpSession session) {
		// TODO Auto-generated method stub
		return "before/userCenter";
	}

}
