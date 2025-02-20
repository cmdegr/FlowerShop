package com.service.before;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.dao.UserDao;
import com.po.Buser;
import com.util.sendsms;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao userDao;
	
	
	//发送验证码
	public int sendcode(Buser buser, Model model, HttpSession session) {
		sendsms send=new sendsms(); 
		model.addAttribute("buser", buser);
		System.out.println(buser.getBphone());
		int mobile_code=send.sendMessage(buser.getBphone());
		session.setAttribute("mobile_code", mobile_code);
		return mobile_code;
	}

	// 注册
	@Override
	public String register(Buser buser, Model model, HttpSession session) {
		int n = userDao.register(buser);
		if (n > 0) {
			return "1";
		} else {
			return "0";
		}
	}

	// 登录
	@Override
	public String loginin(Buser buser, Model model, HttpSession session) {
		Buser ruser = null;
		List<Buser> list = userDao.loginin(buser);
		if (list.size() > 0) {
			ruser = list.get(0);
		}
		if (ruser != null) {
			System.out.println(ruser.getBcanlogin());
			if(ruser.getBcanlogin()==1) {
				session.setAttribute("bruser", ruser);
				//session.setMaxInactiveInterval(3 * 60);
				//设置session有效时间为一天
				session.setMaxInactiveInterval(24*60*60);
				return "1";//登录成功
			}else {
				return "-1";//用户账号被限制
			}
			
		} else {
			return "0";//密码或用户名错误

		}
	}

	//查询手机号
	@Override
	public String selectuser(Buser buser, Model model, HttpSession session) {
		Buser ruser = null;
		List<Buser> list = userDao.selectuser(buser);
		if (list.size() > 0) {
			ruser = list.get(0);
		}
		if (ruser != null) {
			return "1";
		} else {	
			return "0";

		}
	}

	//找回密码
	@Override
	public String backcode(Buser buser, Model model, HttpSession session) {
		int n = userDao.backcode(buser);
		if (n > 0) {
			return "1";
		} else {
			return "0";
		}
	}
}
