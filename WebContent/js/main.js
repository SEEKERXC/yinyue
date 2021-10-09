
//禁止浏览器的后退按钮
$(document).ready(function(e) {    
    if (window.history && window.history.pushState) {
        $(window).on('popstate', function () {
            window.history.pushState('forward', null, '#');
            window.history.forward(1);
        });
    }
    if ('pushState' in history) {
            window.history.pushState('forward', null, '#');
            window.history.forward(1);                    
        }else{
            History.pushState('forward', null, '?state=2');
            window.history.forward(1);
    }
    $(window).on('popstate', function () {
        window.history.pushState('forward', null, '#');
        window.history.forward(1);
    });
    window.onhashchange=function(){ 
        History.pushState('forward', null, '?state=1');
        window.history.forward(1);
    };
});

function border_flash(selector){//边框闪烁效果
	var original_border_color=$(selector).css("border-color");
	$(selector).css("border-color","rgb(255,0,0)");
	setTimeout(function(){$(selector).css("border-color",original_border_color);},150);
	setTimeout(function(){$(selector).css("border-color","rgb(255,0,0)");},300);
	setTimeout(function(){$(selector).css("border-color",original_border_color);},450);
}
$(document).ready(function(){
    //屏蔽右键
    document.oncontextmenu = function() {
        return false;
    };
	var strSearch=sessionStorage.getItem("search");
	var strInner=sessionStorage.getItem("innerlist");
	if(strSearch){
        var s=JSON.parse(strSearch);
        for(var p in s){
            Search[p]=s[p];
        }
    }
    if(strInner){
	    var s=JSON.parse(strInner);
	    for(var p in s){
	        innerSonglist[p]=s[p];
        }
    }
    Search.bindEvent();
    innerSonglist.bindEvent();
    songPage.bindEvent();
});
$(document).on('mouseenter','#client_menu,.navi_left li:last-child',function(){
	$("#client_menu").css("display","block");
});
$(document).on('mouseleave click','#client_menu,.navi_left li:last-child',function(){
	$("#client_menu").css("display","none");
});

function Table(data,params){
    //data是一个二维数组，存放表格的内容，有可能是纯文字，有可能含有html
	this.data=data;
	//表格的id
	this.id=params.id;
	//表格li的样式
	this.itemClass=params.itemClass;
}
Table.prototype.show=function () {
	if(this.data.length<1)return;
	var tb=$("#"+this.id);
	$(tb).empty();
	for(var i=0;i<this.data.length;i++){
	    var item=this.data[i];
        var content = "<li class=" + this.itemClass + "><ul>";
        item.forEach(function (value) {
            content += "<li>" + value + "</li>";
        });
        content += "</ul></li>";
        $(tb).append(content);
    }
};
//获取一个歌曲的信息，用于复用
function getItemOfSong(song){
    var item=[];
    item.push("<a class='songname' href='javascript:void(0)' title='"+song.name+"'>"+song.name+"</a>");
    item.push("");
    var singer="";
    for(var i=0;i<song.singers.length;i++){
        singer+="<a href='javascript:void(0)' title='"+song.singers[i].name+"'>"+song.singers[i].name+"</a>";
        if(i<song.singers.length-1)singer+="、";
    }
    item.push(singer);
    if(song.albums.length>0)
    item.push("<a title='专辑："+song.albums[0].name+"' href='javascript:void(0)'>"+song.albums[0].name+"</a>");
    else item.push("未知专辑");
    item.push("<a title='更多' class='songmore' href='javascript:void(0)'></a>");
    return item;
}

//插入一条评论
function appendComment(comment,id) {
    var ul=$("#"+id+" ul:first-child");
    var p=comment.platform;
    var platform = p==="网易"?"网易云音乐":p==="酷我"?"酷我音乐":p==="虾米"?"虾米音乐":p==="QQ"?"QQ音乐":"";
    var time=msTimeStampToDate(comment.time);
    var content="<li class=\"commentItem\">" +
        "            <div class=\"img_con\">" +
        "                <img src=\""+comment.img+"\">" +
        "            </div>" +
        "            <div class=\"comment_con\">" +
        "                <p class=\"c_text\"><span class=\"c_a_name\">"+comment.author+"</span>："+comment.content+"</p>" +
        "                <p class=\"c_info\">" +
        "                    <span class=\"c_time\">"+time+"</span>" +
        "                    <span style=\"float: right\">" +
        "                        <span class=\"c_c\"><span class=\"c_like\">"+comment.like+"</span>赞</span>" +
        "                        <span style=\"margin-left:8px\">来自：<span class=\"c_come\">"+platform+"</span></span>" +
        "                    </span>" +
        "                </p>" +
        "            </div>" +
        "            <div class=\"clear\"></div>" +
        "        </li>";
    $(ul).append(content);
}

function msTimeStampToDate(timeStamp){
    var date=new Date(timeStamp);
    var Y = date.getFullYear() + '年';
    var M = (date.getMonth()+1 < 10 ? '0'+(date.getMonth()+1) : date.getMonth()+1) + '月';
    var D = (date.getDate() < 10 ? '0' + (date.getDate()) : date.getDate()) + '日 ';
    var h = (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':';
    var m = (date.getMinutes() <10 ? '0' + date.getMinutes() : date.getMinutes());
    return Y+M+D+h+m;
}

function msToDate(timeStamp) {
    var date=new Date(timeStamp);
    var Y = date.getFullYear();
    var M = date.getMonth()+1;
    var D = date.getDate();
    return M+"/"+D+"/"+Y;
}

//展示、隐藏div
//1、搜索 2、首页榜单  3、歌单  4、歌曲
function showDiv(index) {
    for(var i=1;i<=4;i++){
        $("#m"+i).css("display","none");
    }
    $("#m"+index).css("display","block");
}