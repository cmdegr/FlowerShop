  function rec(goodId,pageCur){
    var mymessage=confirm("您确定要取消关注吗？");
    if(mymessage==true)
    {
    	window.location.href = "/Shop/deleteFocus?goodId=" + goodId + "&pageCur="+ pageCur;
		return true;
    }else
    {
        return false;
    }
    }
  
  function url_pre(pageCur){
	 if( pageCur == 1){
		 //$("#startpage").attr("disabled",ture); 
		 return;
	 }else{
		 pageCur=pageCur-1;
		 window.location.href = "/Shop/userCenter?pageCur="+pageCur;
	 }
  }
  function url_next(pageCur,totalPage){
		 if( pageCur == totalPage){
			 //$("#endpage").attr("disabled",ture);
			 return;
		 }else{
			 pageCur=pageCur+1;
			 window.location.href = "/Shop/userCenter?pageCur="+pageCur;
		 }
	  }

  function update(){
		if(window.confirm("是否修改密码？")){
			var oldPassword = document.getElementById("oldPassword").value;
			var newPassword = document.getElementById("newPassword").value;
			$.ajax({
				url : "/Shop/updatepassword",
				type : "post",
				data : JSON.stringify({"oldPassword":oldPassword,"newPassword":newPassword}),
				contentType : "application/json;charset=utf-8",
				dataType : "json",
              success: function (data) {
                  var msgPassword = document.getElementById("msgPassword");
                  msgPassword.innerHTML=data.msgPassword;
                  //var str = xhr.responseText;
                  //console.log(str);
                  //var obj = JSON.parse(str);
                  //console.log(obj);
                  //console.log(obj.msg);
              },
              error: function (xhr) {
                  console.log("出现错误"+xhr.status);
              }
			});
			return true;
		}
		return false;
	}
  function pay(pageCur,deleteId){
	  $.ajax({
			url : "/Shop/showPayOrder",
			type : "post",
			data : JSON.stringify({"pageCur":pageCur,"deleteId":deleteId}),
			contentType : "application/json;charset=utf-8",
			dataType : "json",
			success: function (data) {
				//console.log(data.msg);
				var html="";
				var html1="";
				var html2="";
				if(data.msg==null){
					for(var i=0;i<data.orders.length;i++){
						html1="<li>"+"<img src='images/before/order.png' width='45px' height='48px;'>" +
						"<span class='goodname'>订单号</span><span class='goodname'>"+data.orders[i].id+"</span>"+
						"<button type='button' class='btn btn-success'onclick='pay("+data.pageCur+","+data.orders[i].id+")'>删除订单</button>"+
						"<a href='/Shop/orderD/"+data.orders[i].id+"'><button type='button' class='btn btn-primary'>查看订单详情</button></a>"+"</li>";
						html=html + html1;
					}
					if(data.pageCur==1){
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur+1)+")'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur-1)+")' disabled='true'>上一页</button>"+
						"</div>";
					}else if(data.pageCur==data.totalPage){
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur+1)+")' disabled='true'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur-1)+")'>上一页</button>"+
						"</div>";
					}else{
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur+1)+")'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='pay("+(data.pageCur-1)+")'>上一页</button>"+
						"</div>";
					}
					html= html + html2
				}else{
					html="<p>"+data.msg+"</p>";
				}
				var paid=document.getElementById("paid");
				paid.innerHTML = html;
			},
			error: function (xhr) {
                console.log("出现错误"+xhr.status);
            }
		});
}
  function noPay(pageCur,deleteId){
	  $.ajax({
			url : "/Shop/showNoPayOrder",
			type : "post",
			data : JSON.stringify({"pageCur":pageCur,"deleteId":deleteId}),
			contentType : "application/json;charset=utf-8",
			dataType : "json",
			success: function (data) {
				//console.log(data.msg);
				var html="";
				var html1="";
				var html2="";
				if(data.msg==null){
					for(var i=0;i<data.orders.length;i++){
						html1="<li>"+"<img src='images/before/order.png' width='45px' height='48px;'>" +
						"<span class='goodname'>订单号</span><span class='goodname'>"+data.orders[i].id+"</span>"+
						"<button type='button' class='btn btn-success'onclick='noPay("+data.pageCur+","+data.orders[i].id+")'>删除订单</button>"+
						"<a href='/Shop/order/pay/"+data.orders[i].id+"'><button type='button' class='btn btn-info'>去支付</button></a>"+
						"<a href='/Shop/orderD/"+data.orders[i].id+"'><button type='button' class='btn btn-primary'>查看订单详情</button></a>"+"</li>";
						html=html + html1;
					}
					if(data.pageCur==1){
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur+1)+")'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur-1)+")' disabled='true'>上一页</button>"+
						"</div>";
					}else if(data.pageCur==data.totalPage){
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur+1)+")' disabled='true'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur-1)+")'>上一页</button>"+
						"</div>";
					}else{
						html2="<div class='btn-group' role='group' aria-label='...'>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur+1)+")'>下一页</button>"+
						"<span class='btn'>第<span class='currentIndex'>"+data.pageCur+"</span>页</span>"+
						"<button type='button' class='btn btn-default' onclick='noPay("+(data.pageCur-1)+")'>上一页</button>"+
						"</div>";
					}
					html= html + html2
				}else{
					html="<p>"+data.msg+"</p>";
				}
				var unpaid=document.getElementById("unpaid");
				unpaid.innerHTML = html;
			},
			error: function (xhr) {
                console.log("出现错误"+xhr.status);
            }
		});
	  
	  
	 
}
  
$(function(){
	 //地址设置，当点击修改修改的时候弹出弹框
	  $('.address-main').delegate('.update-address','click',function(){
		  $('#shoujianren').val($(this).parent().find('.Addressee-name').children().html()) ;//设置输入框中的名字
		 $('#dianhua').val($(this).parent().find('.Addressee-phone').children().html());//地址
		 $('#dizhi').val($(this).parent().find('.Addressee-address').children().html());//手机号
	  });
})
  