package com.service.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.dao.AdminGoodsDao;
import com.po.Goods;
import com.util.MyUtil;

@Service("adminGoodsService")
@Transactional
public class AdminGoodsServiceImpl implements AdminGoodsService {
	@Autowired
	private AdminGoodsDao adminGoodsDao;
	
	//查看所有商品
	@Override
	public String selectGoods(Model model, Integer pageCur, String act) {
		List<Goods> allGoods=adminGoodsDao.selectGoods();
		int temp=allGoods.size();//商品总数量
		model.addAttribute("totalCount", temp);//把商品数量存入model中
		int totalPage = 0;//总页数
		if (temp == 0) {
			totalPage = 0;
		} else {
			//返回大于或者等于指定表达式的最小整数
			totalPage = (int) Math.ceil((double) temp / 20);
		}
		if (pageCur == null) {
			pageCur = 1;//当前页
		}
		if ((pageCur - 1) * 20 > temp) {
			pageCur = pageCur - 1;
		}
		//分页查询
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("startIndex", (pageCur - 1) * 20);//起始位置
		map.put("perPageSize", 20);//每页20个
		allGoods = adminGoodsDao.selectAllGoodsByPage(map);
		model.addAttribute("allGoods", allGoods);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("pageCur", pageCur);
		//删除查询
		if("deleteSelect".equals(act)){
			return "admin/deleteSelectGoods";
		}
		//修改查询
		else if("updateSelect".equals(act)){
			return "admin/updateSelectGoods";
		}else{
			return "admin/selectGoods";
		}
	}
	
	//查询一个商品
	@Override
	public String selectAGoods(Model model, Integer id, String act) {
		Goods agoods = adminGoodsDao.selectGoodsById(id);
		model.addAttribute("goods", agoods);
		//修改页面
		if("updateAgoods".equals(act)){
			return "admin/updateAgoods";
		}
		//详情页面
		return "admin/goodsDetail";	
	}
	
	//添加或修改商品信息
	@Override
	public String addOrUpdateGoods(Goods goods,HttpServletRequest request,Model model,String updateAct) {
		/*上传文件的保存位置"/logos"，该位置是指
		workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps，
		发布后使用*/
		//防止文件名重名
		String newFileName = "";
		String fileName = goods.getLogoImage().getOriginalFilename(); 
		//选择了文件
		if(fileName.length() > 0){
			String realpath = request.getServletContext().getRealPath("logos");
			//实现文件上传
			String fileType = fileName.substring(fileName.lastIndexOf('.'));
			//防止文件名重名
			newFileName = MyUtil.getStringID() + fileType;
			goods.setGpicture(newFileName);
			File targetFile = new File(realpath, newFileName); 
			if(!targetFile.exists()){  
	            targetFile.mkdirs();  
	        } 
			//上传 
	        try {   
	        	goods.getLogoImage().transferTo(targetFile);
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        }  
		}
		
		String gdprice=request.getParameter("gdprice");//获取添加或修改后的折扣价
		double price=Double.parseDouble(gdprice);
		System.out.println(gdprice);
		//修改
		if("update".equals(updateAct)){ //updateAct不能与act重名，因为使用了转发
			//修改到数据库
	       if(adminGoodsDao.updateGoodsById(goods) > 0 && 
	    		   adminGoodsDao.updateGoodsDiscount(goods.getId())>0 &&
	    		   adminGoodsDao.addGoodsDiscount(goods.getId(),price)>0){
	        	return "forward:/adminGoods/selectGoods?act=updateSelect";
	        }else{
	        	return "adminGoods/updateAgoods";
	       }
		}else{
			//保存到数据库
			if(adminGoodsDao.addGoods(goods) > 0 && adminGoodsDao.addGoodsDiscount(goods.getId(),price)>0){
				//转发到查询的controller
				return "forward:/adminGoods/selectGoods";
			}else{
				return "card/addCard";
			}
		}
	}
	
	//删除一个商品
	@Override
	public String deleteAGoods(Integer id,Model model) {
		//商品有关联
		if(adminGoodsDao.selectOrderdetailGoods(id).size() > 0) {
			model.addAttribute("msg", "商品有关联，不允许删除！");
			return "forward:/adminGoods/selectGoods?act=deleteSelect";
		}
		adminGoodsDao.deleteGoodsDiscount(id);
		adminGoodsDao.deleteAGoods(id);
		model.addAttribute("msg", "成功删除商品！");
		return "forward:/adminGoods/selectGoods?act=deleteSelect";
	}
	
	//删除多个商品
	@Override
	public String deleteGoods(Integer ids[],Model model) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < ids.length; i++) {
			//商品有关联
			if(adminGoodsDao.selectOrderdetailGoods(ids[i]).size() > 0) {
				model.addAttribute("msg", "商品有关联，不允许删除！");
				return "forward:/adminGoods/selectGoods?act=deleteSelect";
			}
			list.add(ids[i]);
		}
		adminGoodsDao.deleteGoodsDiscount1(list);
		adminGoodsDao.deleteGoods(list);
		model.addAttribute("msg", "成功删除商品！");
		return "forward:/adminGoods/selectGoods?act=deleteSelect";
	}
}
