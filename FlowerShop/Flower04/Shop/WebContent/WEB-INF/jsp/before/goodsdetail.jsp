<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="<%=basePath%>">
<title>商品详情</title>
<link href="css/before/daohang.css" rel="stylesheet" type="text/css" />
<link href="css/before/common.css" rel="stylesheet" type="text/css" />
<link href="css/before/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
	function goCart() {
		var num= document.getElementById("num").value;
		var r = /^\+?[1-9][0-9]*$/;
		if (r.test(num)){
			//这是一个正整数
			if(${goods.gstore}>= num ){
				document.putcartform.submit();			
			}else{
				alert("请重新输入");
			}			
		}else{
			alert("请重新输入");
		}
	}
	function gofocus(gno) {
		window.location.href = "/Shop/cart/focus?id=" + gno;
	}
</script>
</head>
<body>


	<form action="cart/putCart" name="putcartform" method="post">
		<div class="blank"></div>
		<div class="block clearfix">
			<div class="location ared">
				当前位置：<a href="index2">首页</a> > <a href="goodsDetail?id=${goods.id }">商品详情</a>
			</div>
			<div class="blank"></div>
			<div id="goodsInfo">
				<div class="imgInfo">
					<input type="hidden" name="id" value="${goods.id }" /> <img
						src="logos/${goods.gpicture}" width="230px" height="230px" />
				</div>
			</div>
			<!--商品表述-->
			<div class="goods_desc">
				<div class="bt">${goods.gname }</div>
				<div class="goods_show">
					<ul>
						<li><span>价格:</span> <strong class="yj">${goods.goprice }元</strong></li>
						<c:if test="${goods.gdprice!=null }">
							<li><span>折扣价:</span><strong class="xj">${goods.gdprice }元</strong></li>
						</c:if>

						<li><span>类型:</span> ${goods.typename }</li>
						<li><span>购买数量:</span><input type="text" id="num"
							name="shoppingnum" class="good_txt" value="1" required />
							(库存${goods.gstore }件)</li>
					</ul>
				</div>
				<p class="bottom10 top5">
					<img src="images/before/goods_ann2.gif" style="cursor: pointer"
						onclick="goCart()" />&nbsp;&nbsp; <input type="button"
						style="cursor: pointer" class="sh_bnt2"
						onclick="gofocus('${goods.id }')" value="关注" />
				</p>${msg}
			</div>
			<!--end-->
		</div>
	</form>
</body>
</html>
