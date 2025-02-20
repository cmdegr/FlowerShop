package com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import com.service.admin.AdminUserService;
@Controller
@RequestMapping("/adminUser")
public class AdminUserController extends BaseController{
	@Autowired
	private AdminUserService adminUserService;
	@RequestMapping("/userInfo")
	public String userInfo(Model model, Integer pageCur) {
		return adminUserService.userInfo(model,pageCur); 
	}
	@RequestMapping("/updateuserManager")
	public String updateuserManager(Integer id, Model model) {
		return adminUserService.updateuserManager(id, model);
	}
}
