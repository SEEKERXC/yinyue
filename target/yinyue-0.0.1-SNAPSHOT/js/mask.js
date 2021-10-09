
DJMask={
    allowAjaxAnni:true,
			common:{
				windowW:$(window).width(),
				windowH:$(window).height(),
				closeCssAnimate:function(){
					$("[class*=-close]").css({
							"transition":"all 0.4s ease-in-out",
							"-webkit-transition":"all 0.4s ease-in-out",
							"-ms-transition":"all 0.4s ease-in-out"
					}).hover(function(){
						$(this).css({
							"color":"#ff0000",
							"transform":"rotate(360deg)",
							"-webkit-transform":"rotate(360deg)",
							"-ms-transform":"rotate(360deg)"
						});
					},function(){
						$(this).css({
							"color":"#ededed",
							"transform":"rotate(0deg)",
							"-webkit-transform":"rotate(0deg)",
							"-ms-transform":"rotate(0deg)"
						});
					})
				}
			},
			maskBackgroundCss:function(){
				var css={
						"background":"#000000",						
						"opacity":"0",
						"-moz-opacity":"0",
						"-webkit-opacity":"0",
						"filter":"alpha(opacity=0)",
						"width":"100%",
						"height":document.body.scrollHeight,
						"position": "absolute",
						"top": "0px",
						"left": "0px",
						"z-index": "100",
						"display":"none"
					};
				return css;
			},
			init:function(){
								var maskHtml='<div id="dj-mask"></div>';								
								$(maskHtml).appendTo("body");
								djMaskDomLoaded=$("#dj-mask");
								djMaskDomLoaded.css(this.maskBackgroundCss());
				},
			show:function(){
				this.init();
				djMaskDomLoaded.show();
			},
			hide:function(){
				djMaskDomLoaded.remove();
			},	
			loadingCss:function(){
				this.show();
				var loadingBoxCenterCss={
					"position":"absolute",
					"left":"50%",
					"top":"40%",
					"height":"20px",
					"width":"100px",
					"margin-top":"-10px",
					"margin-left":"-50px",
					"z-index":"999999999"
				};
				var objectClass={
					"width":"20px",
					"height":"20px",
					"background-color":"#ffff00",
					"-moz-border-radius":"50% 50% 50% 50%",
					"-webkit-border-radius":"50% 50% 50% 50%",
					"border-radius":"50% 50% 50% 50%",
					"margin-right":"20px",
					"margin-bottom":"20px",
					"position":"absolute"
				};
				var objectOneCss={
					"-webkit-animation":"object 2s linear infinite",
					"-ms-animation":"object 2s linear infinite",
		 			"animation":"object 2s linear infinite"
				};
				var objectTwoCss={
					"-webkit-animation": "object 2s linear infinite -.4s",
					"-ms-animation":"object 2s linear infinite -.4s",
					"animation":"object 2s linear infinite -.4s"
				};
				var objectThreeCss={
					"-webkit-animation": "object 2s linear infinite -.8s",
					"-ms-animation": "object 2s linear infinite -.8s",
					"animation": "object 2s linear infinite -.8s"
				};
				var objectFourCss={
					"-webkit-animation": "object 2s linear infinite -1.2s",
					"-ms-animation": "object 2s linear infinite -1.2s",
					"animation": "object 2s linear infinite -1.2s",
				};
				var objectFiveCss={
					"-webkit-animation": "object 2s linear infinite -1.6s",
					"-ms-animation": "object 2s linear infinite -1.6s",
					"animation":"object 2s linear infinite -1.6s"
				};
				var keyframesObj='0% { left: 100px; top:0}'+
							  	 '80% { left: 0; top:0;}'+
							  	 '85% { left: 0; top: -20px; width: 20px; height: 20px;}'+
							  	 '90% { width: 40px; height: 15px; }'+
							  	 '95% { left: 100px; top: -20px; width: 20px; height: 20px;}'+
							  	 '100% { left: 100px; top:0; }';
				document.styleSheets.item(0).insertRule('@-webkit-keyframes object{0% { left: 100px; top:0}80% { left: 0; top:0;}85% { left: 0; top: -20px; width: 20px; height: 20px;}90% { width: 40px; height: 15px; }95% { left: 100px; top: -20px; width: 20px; height: 20px;}100% { left: 100px; top:0; }}',0); 
				document.styleSheets.item(0).insertRule('@keyframes object{0% { left: 100px; top:0}80% { left: 0; top:0;}85% { left: 0; top: -20px; width: 20px; height: 20px;}90% { width: 40px; height: 15px; }95% { left: 100px; top: -20px; width: 20px; height: 20px;}100% { left: 100px; top:0; }	}',0); 
				var html='<div id="dj-mask-loadingBoxCenter">'+
						 '<div class="object" id="objectOne"></div>'+
						 '<div class="object" id="objectTwo" style="left:20px;"></div>'+
						 '<div class="object" id="objectThree" style="left:40px;"></div>'+
						 '<div class="object" id="objectFour" style="left:60px;"></div>'+
						 '<div class="object" id="objectFive" style="left:80px;"></div>'
						 '</div>';
				$(html).css(loadingBoxCenterCss).appendTo("body").children(".object").css(objectClass);
				$("#objectOne").css(objectOneCss);
				$("#objectTwo").css(objectTwoCss);
				$("#objectThree").css(objectThreeCss);
				$("#objectFour").css(objectFourCss);
				$("#objectFive").css(objectFiveCss);
			},
			loading:function(option){
				if(option=="close"){
					this.hide();
					$("#dj-mask-loadingBoxCenter").remove();
				}
				else{										
					this.loadingCss();
				}
			},
			closeAll:function(){
					$("[class^=dj-mask-]").remove();
					$("[id^=dj-mask]").remove();			
			},
			msg:function(message){
				if(message==undefined||message=="") return false;
				var msgDom='<div class="dj-msg-number-'+Math.floor(Math.random()*1000000)+'"></div>';					
				$(msgDom).css({
					"overflow":"hidden",
					"background":"rgba(0,0,0,.5)",
					"border-radius":"4px",
					"position": "fixed",
					"top":"40%",
					"left":"50%",	
					"padding":"15px",
					"color":"#fff",
					"z-index":"999999",
					"display":"none",
					"max-width":"200px",
					"word-break":"break-all"
				}).appendTo("body").html(message);
				var msgDomLoaded=$("."+$(msgDom).attr("class"));
				msgDomLoaded.css({
					"margin-left":"-"+msgDomLoaded.width()/2+"px",
					"margin-top":"-"+msgDomLoaded.height()/2+"px",
				}).fadeIn();
				setTimeout(function(){
					$("."+$(msgDom).attr("class")).remove();
				},1500);
			},
			alertMask:function(){
				var alertMaskHtml='<div id="dj-mask-alert"></div>';								
				$(alertMaskHtml).appendTo("body");
				alertMaskDomLoaded=$("#dj-mask-alert");
				alertMaskDomLoaded.css($.extend(this.maskBackgroundCss(),{"z-index":"200"}));
			},
			alert:function(message,callback){	
				if(message==""||message==undefined) return false;
				this.alertMask();
				alertMaskDomLoaded.show(); 
				var alertDom='<div class="dj-mask-alert-'+Math.floor(Math.random()*1000000)+'"></div>';
				$(alertDom).css({
					"width":"200px",
					"overflow":"hidden",
					"border":"1px solid #ddd",					
					"z-index":"300",
					"background":"#fff",
					"border-radius":"4px",
					"position": "fixed",
					"top":$(window).height()/2.5+"px",
					"left":$(window).width()/2+"px",	
				}).appendTo("body");
				var alertDomLoaded=$("."+$(alertDom).attr("class"));
				alertDomLoaded.css({
					"margin-left":"-"+alertDomLoaded.width()/2+"px",
					"margin-top":"-"+alertDomLoaded.height()/2+"px",
				})
				.append('<div style="height:30px;border-bottom:1px solid #ddd;line-height:30px;padding-left:10px;font-size:14px;color:#666;background:#ebebeb">鎻愮ず<a style="float:right;margin-right:10px;cursor:pointer;color:#666;font-size:15px;" class="dj-alert-close">X</a></div>')
				.append('<div style="font-size:13px;color:#333;padding:10px;overflow:hidden;word-break:break-all" class="dj-alert-content"></div>')
				.append('<a style="display:block;width:50px;height:25px;line-height:25px;text-align:center;border-radius:4px;background:#428bca;color:#fff;cursor:pointer;float:right;margin:5px;padding:0" class="dj-alert-ok">纭畾</a>');
				this.common.closeCssAnimate();
				$(".dj-alert-content").html(message);
				$(".dj-alert-close").click(function(){
					alertDomLoaded.remove();
					alertMaskDomLoaded.remove();
				});
				$(".dj-alert-ok").click(function(){
					if(callback)  callback();
					alertDomLoaded.remove();
					alertMaskDomLoaded.remove();
				});
			},
			openMask:function(){
				var openMaskHtml='<div id="dj-mask-open"></div>';								
				$(openMaskHtml).appendTo("body");
				openMaskDomLoaded=$("#dj-mask-open");
				openMaskDomLoaded.css($.extend(this.maskBackgroundCss(),{"z-index":"150"}));
			},
			open:function(options,callback){
				var config={
					width:"400px",
					height:"300px",
					title:"0",
					content:"<div>loading.......</div>"
				},
				me=$.extend(config, options);
				this.openMask();
				openMaskDomLoaded.show();
				var contentDom='<div class="dj-mask-content-'+Math.floor(Math.random()*1000000)+'"></div>';
				$(contentDom).css({
					"width":me.width,
					"height":me.height,
					"overflow":"hidden",
					"border":"1px solid #ddd",					
					"z-index":"150",
					"background":"#fff",
					"border-radius":"4px",
					"position": "fixed",
					"top":$(window).height()/2.5+"px",
					"left":$(window).width()/2+"px",		
				}).appendTo("body");
				var contentDomLoaded=$("."+$(contentDom).attr("class"));
				contentDomLoaded.css({
					"margin-left":"-"+contentDomLoaded.width()/2+"px",
					"margin-top":"-"+contentDomLoaded.height()/2+"px",
				})
				.append('<div style="height:30px;border-bottom:1px solid #ddd;line-height:30px;padding-left:10px;font-size:14px;color:#666;background:#ebebeb">'+me.title+'<a style="float:right;margin-right:10px;cursor:pointer;color:#666;font-size:15px;" class="dj-content-close">X</a></div>')
				.append('<div style="font-size:13px;color:#333;padding:10px;overflow:hidden;word-break:break-all" class="dj-content-content"></div>');
				this.common.closeCssAnimate();
				$(".dj-content-content").html(me.content);
				$(".dj-content-close").click(function(){
					if(callback) callback();
					contentDomLoaded.remove();
					openMaskDomLoaded.remove();					
				});
			},
			doAjax:(function(){
				var mss = {};
				var ajaxStart_default = mss.ajaxStart_default = function() {						
					if(DJMask.allowAjaxAnni)DJMask.loading();
				};
				var ajaxStop_default = mss.ajaxStop_default = function() {
					DJMask.loading("close");
				};
				$(document).ajaxStart(ajaxStart_default); 
				$(document).ajaxStop(ajaxStop_default); 
			}())
		};