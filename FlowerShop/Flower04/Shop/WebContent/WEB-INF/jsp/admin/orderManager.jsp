<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>订单管理</title>
	<link href="css/admin/common.css" type="text/css" rel="stylesheet">
	<script language="javascript" type="text/javascript" src="/Shop/js/admin/WdatePicker.js"></script>
	<script type="text/javascript" src="<%=basePath %>js/before/jquery-3.1.1.min.js"></script>
	<script type="text/javascript">
		function changeColor(obj){
			obj.className="bgcolor";
		}
		function changeColor1(obj){
			obj.className="";
		}
		function check(){
			var status=document.getElementById("status").value;
			var payway=document.getElementById("payway").value;
			var time1=document.getElementById("time1").value;
			var time2=document.getElementById("time2").value;
			window.location.href = "/Shop/adminOrder/orderFactor?status="+status+"&payway="+payway+"&time1="+time1+"&time2="+time2;
		}
	</script>
	<style type="text/css">
		table{
			border: 1px solid black;
			text-align: center;
			border-collapse: collapse
		}
		.bgcolor{
		  	background-color: #3d7878;
		}
	</style>
</head>
<body>
	<c:if test="${orderList.size() == 0 }">
		您还没有订单。
	</c:if>
	<c:if test="${orderList.size() != 0 }">
      <span style="font-size: 13.3333px;">订单状态：</span>
      <select id="status" style="width: 65px;height: 25px">
        <option value="123456" <c:if test="${status==123456}">selected="selected"</c:if>>请选择</option>
        <option value="1" <c:if test="${status==1}">selected="selected"</c:if>>已付款</option>
        <option value="0" <c:if test="${status==0}">selected="selected"</c:if>>未付款</option>       
      </select>
      &nbsp&nbsp
      <span style="font-size: 13.3333px;">付款方式：</span>
      <select id="payway" style="width: 65px;height: 25px">
        <option value="123456" <c:if test="${payway=='123456'}">selected="selected"</c:if>>请选择</option>
        <option value="支付宝" <c:if test="${payway=='支付宝'}">selected="selected"</c:if>>支付宝</option>
        <option value="微信" <c:if test="${payway=='微信'}">selected="selected"</c:if>>微信</option> 
      </select>
      &nbsp&nbsp
      <span style="font-size: 13.3333px;">订单日期从：</span>
	  <input id="time1" value="${time1}" style="height: 22px;" class="Wdate" type="text" onclick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',readOnly:true})">
      <span style="font-size: 13.3333px;">至：</span>
      <input id="time2" value="${time2}" style="height: 22px;" class="Wdate" type="text" onclick="WdatePicker({el:this,dateFmt:'yyyy-MM-dd',readOnly:true})">
      <!--
      出错  
      <input style="width: 55px;height: 25px;" type="button" value="查询" onclick="check()"/>
      -->
      <button style="width: 55px;height: 25px;" onclick="check()">查询</button>
    <br>
    <br>
	<table border="1" bordercolor="DarkSalmon">
		<tr>
			<th width="80px">订单编号</th>
			<th width="150px">用户名</th>
			<th width="100px">订单金额</th>
			<th width="80px">订单状态</th>
			<th width="150px">订单日期</th>
			<th width="100px">付款方式</th>
			<th width="320px">收货地址</th>
			<th width="110px">用户是否删除此订单</th>
		</tr>
		<c:forEach var="n" items="${orderList}">
		<tr onmousemove="changeColor(this)" onmouseout="changeColor1(this)">
			<td>${n.id}</td>
			<td>${n.bphone}</td>
			<td>${n.amount}</td>
			<td><c:if test="${n.status == 0}" >未付款</c:if><c:if test="${n.status == 1}" >已付款</c:if></td>
			<td>${n.orderdate}</td>
			<td>${n.payway}</td>
			<td>${n.address}</td>
			<td><c:if test="${n.deletestatus == 0}" >已删除</c:if><c:if test="${n.deletestatus == 1}" >未删除</c:if></td>
		</tr>
		</c:forEach>
			<tr>
				<td colspan="8" align="right">
					&nbsp;&nbsp;&nbsp;
					&nbsp;&nbsp;&nbsp;共${totalCount}条记录&nbsp;&nbsp;共${totalPage}页&nbsp;&nbsp;
					第${pageCur}页&nbsp;&nbsp;
					<c:url var="url_pre" value="adminOrder/orderInfo">
						<c:param name="pageCur" value="${pageCur - 1 }"/>
					</c:url>
					<c:url var="url_next" value="adminOrder/orderInfo">
						<c:param name="pageCur" value="${pageCur + 1 }"/>
					</c:url>
					<!-- 第一页没有上一页 -->
					<c:if test="${pageCur != 1 }">
						<a href="${url_pre}">上一页</a>&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
					<!-- 最后一页，没有下一页 -->
					<c:if test="${pageCur != totalPage && totalPage != 0}">
						<a href="${url_next}">下一页</a>
					</c:if>
				</td>
			</tr> 
	</table>
	</c:if>
</body>
</html>