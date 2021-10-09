
var timer;
var logging=false;
var registering=false;
var no_repeat=false;
//避免重复发送请求

function login(){
    var email=$("#email").val();
    var password=$("#password").val();
    if(email===""||password===""){
        DJMask.msg("邮箱或密码为空");
        return;
    }
	logging=true;
	$.ajax({
		type:"get",
		url:"login",
		data:{
			"email":email,
			"password":password
		},
		success:function(data){
			if(data.name){
				$('.dialog-box').replaceWith("<div id='login_dialog'></div>");
				$('#dialog-box-mask').remove();
				DJMask.msg("登录成功！");
				innerSonglist.getList();
				$("#login").remove();
				$("#register").remove();
				var content="<li id='user'><a href='javascript:void(0)'>"+data.name+"</a></li>";
				$('#menu').find('ul').append(content);
                setResumeUser();//五分钟发送一次空请求，防止session失效(session失效时间设定为10分钟)
			}else{
				DJMask.msg("登录失败，邮箱或密码错啦...");
			}
			logging=false;
		},
		error:function(XMLHttpRequest, textStatus,errorThrown){
			logging=false;
			DJMask.msg("网络好像出了点问题0.0");
		}
	});
}

function register(){
	registering=true;
	var name=$("#nickname").val();
	var email=$("#remail").val();
	var password=$("#rpassword").val();
	var confirm=$("#confirm").val();
	var email_regex=/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
	var name_regex=/^.{1,12}$/;
	var password_regex=/^[a-zA-Z]\w{5,17}$/;
	if(!email_regex.test(email)){
		border_flash("#remail");
		registering=false;
		return;
	}
	if(!name_regex.test(name)){
		border_flash("#nickname");
		registering=false;
		return;
	}
	if(!password_regex.test(password)){
		border_flash("#rpassword");
		registering=false;
		return;
	}
	if(password!=confirm){
		border_flash("#confirm");
		registering=false;
		return;
	}
	if(no_repeat){
		$.ajax({
			type:"post",
			url:"register",
			contentType: "application/x-www-form-urlencoded; charset=utf-8", 
			data:{
				email:email,
				name:name,
				password:password
			},
			success:function(data){
				if(data){
					$('.dialog-box').replaceWith("<div id='register_dialog'></div>");
					$('#dialog-box-mask').remove();
					DJMask.msg("注册成功！");
					$("#login").remove();
					$("#register").remove();
					var content="<li id='user'><a href='javascript:void(0)'>"+name+"</a></li>";
					$("#menu").find("ul").append(content);
                    setResumeUser();
				}else{
					DJMask.msg("抱歉，注册过程中出现了一些错误，请稍后重试...");
				}
				registering=false;
			},
			error:function(XMLHttpRequest, textStatus,errorThrown){
				registering=false;
				DJMask.msg("网络好像出了点问题0.0");
			}
		});
	}else{
		border_flash("#remail");
	}
}

$(document).on('click','#login',function(){
	$('#login_dialog').dialogBox({
		hasClose: true,
		hasBtn: true,
		width:420,
		height:250,
		confirmValue: '登录',
		hasMask:true,
		confirm: function(){
			if(!logging)login();
		},
		cancelValue: '取消',
		title: '登录',
		content: "<div class='elegant-aero'>"+
				"<label>"+
				"<span>邮箱：</span>"+
				"<input id='email' type='email' placeholder='邮箱地址' />"+
				"</label>"+
				"<label style='margin-top:22px'>"+
				"<span>密码：</span>"+
				"<input id='password' type='password' placeholder='密码' />"+
				"</label>"+
				"</div>"
	});
});

$(document).on('click','#register',function(){
	$("#register_dialog").dialogBox({
		hasClose: true,
		hasBtn: true,
		width:420,
		height:370,
		confirmValue: '注册',
		hasMask:true,
		confirm: function(){
			if(!registering)register();
		},
		cancelValue: '取消',
		title: '注册',
		content: "<div class='elegant-aero'>"+
				"<label style=''>"+
				"<span>邮箱：</span>"+
				"<input id='remail' type='email' placeholder='正确的邮箱地址' onpropertychange='checkRepeat()' oninput='checkRepeat()'/>"+
				"</label>"+
				"<span id='check_repeat' style='display:none; color:red; margin:5px 0px 0px 85px;'></span>"+
				"<label id='name_label' style='margin-top:22px'>"+
				"<span>昵称：</span>"+
				"<input id='nickname' type='text' placeholder='昵称，1-12个字符' />"+
				"</label>"+
				"<label style='margin-top:22px'>"+
				"<span>密码：</span>"+
				"<input id='rpassword' type='password' placeholder='密码，以字母开头，6-18位' />"+
				"</label>"+
				"<label style='margin-top:22px'>"+
				"<span>确认密码：</span>"+
				"<input id='confirm' type='password' placeholder='确认密码' />"+
				"</label>"+
				"</div>"
	});
})

$(document).on('click','#signout',function(){
	$.ajax({
		type:"get",
		url:"signout",
		success:function(data){
			if(data){
				DJMask.msg("注销成功");
				//回到首页
				showDiv(2);
				//清空收藏列表
				$(".added").remove();
				//还原登录注册
				$("#menu").find("li:last-child").remove();
				var content="<li id='login'><a href='javascript:void(0)'>登录</a></li>"+
				"<li id='register'><a href='javascript:void(0)'>注册</a></li>";
				$("#menu").find("ul").append(content);
				window.clearInterval(timer);
			}else{
				DJMask.msg("网络好像出了点问题0.0");
			}
		},
		error:function(){
			DJMask.msg("网络好像出了点问题0.0");
		}
	});
});

//刷新页面之后判断是否已经登录
$.ajax({
	type:"get",
	url:"getuser",
	success:function(data){
		$("#login").remove();
		$("#register").remove();
		if(data.name){
			var content="<li id='user'><a href='javascript:void(0)'>"+data.name+"</a></li>";
			innerSonglist.getList();//刷新个人歌单
            setResumeUser();
		}else{
			var content="<li id='login'><a href=\"javascript:void(0)\">登录</a></li>"+
			"<li id='register'><a href=\"javascript:void(0)\">注册</a></li>";
		}
		$("#menu").find("> ul").append(content);
	}
});

function checkRepeat(){
	DJMask.allowAjaxAnni=false;
	$.ajax({
		type:"get",
		url:"hasUser",
		data:{
			email:$("#remail").val()
		},
		success:function(data){
			if(data){
				no_repeat=false;
				$("#check_repeat").text("此邮箱已注册");
                $("#check_repeat").css("display",'block');
				$("#name_label").attr("style","margin-top:6px");
			}else{
				no_repeat=true;
				$("#check_repeat").text("");
                $("#check_repeat").css("display",'block');
				$("#name_label").attr("style","margin-top:22px");
			}
            DJMask.allowAjaxAnni=true;
		},
		error:function(){
			console.log("出错");
            DJMask.allowAjaxAnni=true;
		}
	});
}

$(document).on('mouseenter','#user,.user_menu',function(){
	var user=$("#user"), menu=$(".user_menu");
	$(menu).css("width",$(user).attr("width"));
	$(menu).css("left",user.offset().left);
	$(menu).css("top",user.offset().top+39.2);
	$(menu).css("display","block");
});
$(document).on('mouseleave click','#user,.user_menu',function(){
	$(".user_menu").css("display","none");
});

$(document).on("keypress","#email,#password",function(e){
    var eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
    if (eCode === 13){
        if(!logging)login();
    }
});
function setResumeUser(){
	DJMask.allowAjaxAnni=false;
    timer=window.setInterval(function () {
        $.ajax({
            type:"get",
            url:"nothing",
            success:function (data) {
                if(data)console.log("已继续激活用户");
                DJMask.allowAjaxAnni=true;
            },
			error:function () {
                DJMask.allowAjaxAnni=true;
            },
			complete:function () {
                DJMask.allowAjaxAnni=true;
            }
        });
    },300000)
}

