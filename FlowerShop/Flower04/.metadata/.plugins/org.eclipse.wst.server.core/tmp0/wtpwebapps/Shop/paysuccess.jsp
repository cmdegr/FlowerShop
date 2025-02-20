<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="java.util.*"%>
<%@ page import="java.util.Map"%>
<%@ page import="com.util.*"%> 
<%@ page import="com.alipay.api.*"%>
<%@ page import="com.alipay.api.internal.util.*"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>首页</title>
<script type="text/javascript" src="<%=basePath %>js/before/jquery-3.1.1.min.js"></script>
<script type="text/javascript" src="<%=basePath %>js/before/bootstrap.min.js"></script>
<script type="text/javascript" src="<%=basePath %>js/before/index.js"></script>
<link rel="stylesheet" type="text/css" href="<%=basePath %>css/before/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="<%=basePath %>css/before/index.css">
<title>支付成功界面</title>
<script>
$(document).ready(function(){
	var success = document.getElementById("success") ;
	var orderId = success.getAttribute("class");
	console.log(orderId);
	$.ajax({
	url : "/Shop/changePay",
	type : "post",
	data : JSON.stringify({"orderId":orderId}),
	contentType : "application/json;charset=utf-8",
	dataType : "json",
  	success: function (data) {
      console.log(data.responseText);
  	},
  	error: function (xhr) {
  		   console.log(xhr.responseText);
      	   console.log("出现错误"+xhr.status);
  		}
	})
})
</script>
</head>
<body>
<%
	Map<String,String> params = new HashMap<String,String>();
	Map<String,String[]> requestParams = request.getParameterMap();
	for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
		String name = (String) iter.next();
		String[] values = (String[]) requestParams.get(name);
		String valueStr = "";
		for (int i = 0; i < values.length; i++) {
			valueStr = (i == values.length - 1) ? valueStr + values[i]
					: valueStr + values[i] + ",";
		}
		valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
		params.put(name, valueStr);
	}
	boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
	if(signVerified) {
		String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");
		String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");
		request.setAttribute("orderId", out_trade_no);	
	}
%>
	<!-- 用户尚未登录的用户下拉框 -->
	<ul class="user-box active">
		<li>
			<c:if test="${bruser!=null}"> ${bruser.bphone }</c:if>
			<c:if test="${bruser==null}"><a href="<%=basePath %>user/log"><img src="<%=basePath %>images/before/login.png" width="20px" height="20px;"><span class="user-box-name">登录</span></a></c:if>
		</li>
		<li>
		<c:if test="${bruser!=null}"><a href="<%=basePath %>user/exit">退出</a></c:if>
		<c:if test="${bruser==null}"><a href="<%=basePath %>user/regi"><img src="<%=basePath %>images/before/register.png" width="25px" height="25px;"><span class="user-box-name">注册</span></a>
		</c:if>
		</li>
		<li>
		<c:if test="${bruser!=null}"><a href="<%=basePath %>useC">用户中心</a></c:if>
		<c:if test="${bruser==null}"><a href="<%=basePath %>useC"><img src="<%=basePath %>images/before/usercontent.png" width="20px" height="20px;"><span class="user-box-name">用户中心</span></a>
		</c:if>
		
		</li>
	</ul>
	
	<!-- 头部 -->
	<div class="head">
		<img class="logo" src="<%=basePath %>images/before/mylogo.png">
		<div class="head-title">
			<a href="index2"><span>首页</span></a>
			<a href="<%=basePath %>search2/1"><span>所有花</span></a>
		</div>
		<div class="row headd-row">
			<div class="col-lg-3">
			    <div class="input-group" id="btn">
			     <input type="hidden" id="url" value="<%=basePath %>"/>
			      <input type="text" class="form-control" id="btn-content" placeholder="请输入您要查询的内容（花的名称或类型）">
			      <span class="input-group-btn">
			        <button class="btn btn-default" id="btn1" style="top: 0px;" type="button"><span>搜索</span></button>
			      </span>
			    </div>
			 </div>
		</div>
		<!-- 购物车 -->
		<a href="<%=basePath %>cart/selectCart"><img class="cart" src="<%=basePath %>images/before/cart.png" width="50px" height="50px"></a>
		<!-- 用户图标 -->
		<a href="javascript:;"><img src="<%=basePath %>images/before/user.png" class="user" ></a>

	</div>
	
	<div class="pay-main row "> 
		<ol class="breadcrumb col-md-10 col-md-offset-1">
		  <li><a href="<%=basePath %>index2">首页</a></li>
		  <li class="active">成功支付</li>
		</ol>
		<br>
		<div class="col-md-8 col-md-offset-2">
			<h4 id="success" class="${orderId}">恭喜您！支付成功！</h4>
		</div>
	</div>
	
</body>
</html>
