// Dropdown Menu
var dropdown = document.querySelectorAll('.dropdown');
var dropdownArray = Array.prototype.slice.call(dropdown,0);
dropdownArray.forEach(function(el){
	var button = el.querySelector('a[data-toggle="dropdown"]'),
			menu = el.querySelector('.dropdown-menu'),
			arrow = button.querySelector('i.icon-arrow');

	button.onclick = function(event) {
		if(!menu.hasClass('show')) {
			menu.classList.add('show');
			menu.classList.remove('hide');
			arrow.classList.add('open');
			arrow.classList.remove('close');
			event.preventDefault();
		}
		else {
			menu.classList.remove('show');
			menu.classList.add('hide');
			arrow.classList.remove('open');
			arrow.classList.add('close');
			event.preventDefault();
		}
	};
});
Element.prototype.hasClass = function(className) {
    return this.className && new RegExp("(^|\\s)" + className + "(\\s|$)").test(this.className);
};

var innerSonglist={
    //歌单列表
    list:[],
    //歌曲列表,格式:    歌单id: 歌曲array
    songs:{},
    //正在展示的歌单的id
    idShowing:0,
    //正显示的页数
    page:1,
    //将收藏的歌曲
    songToCollect:{},
    bindEvent:function () {
        $(document).on('click','#own_list_name',function(){
            //点击左侧歌单列表的名字，打开歌单页面
            showDiv(3);
            var index=$(this).parent().parent().parent().parent().index()-1;
            var songlist=innerSonglist.list[index];
            innerSonglist.idShowing=songlist.id;
            $.ajax({
                type:"get",
                url:"songlist/"+innerSonglist.idShowing+"/song",
                success:function(songs){
                    innerSonglist.songs[innerSonglist.idShowing.toString()]=songs;
                    innerSonglist.showSongs(innerSonglist.idShowing,innerSonglist.page);
                },
                error:function(){
                    DJMask.msg("网络出了点问题，请刷新试试");
                }
            });
            $("#m3").find("> div.top > div.img_container > img").attr("src",songlist.img);
            $("#song_list_name").text(songlist.name);
            $("#update_time").text(msTimeStampToDate(songlist.updateTime));
            $("#brief").find("> p > span").text(songlist.brief);
            $("#songcount").text(songlist.songCount);
        });
        //点击新建歌单函数
        $(document).on('click','#new_songlist',function(){
            DJMask.allowAjaxAnni=false;
            $.ajax({
                type:"get",
                url:"getuser",
                success:function(data){
                    if(data.name){//如果已经登录则打开对话框
                        $('#btn-dialogBox').dialogBox({
                            hasClose: true,
                            hasBtn: true,
                            hasMask:true,
                            width:420,
                            height:200,
                            confirmValue: '确定',
                            confirm: function(){
                                //确定新建歌单
                                var name=$("#new_name").val();
                                name=$.trim(name);//消除前后的空格
                                var regex=/^.{1,25}$/;
                                if(regex.test(name)){
                                    $(".alert").css("color","#599BDC");
                                    $("#new_name").val("");
                                    $.ajax({
                                        type:"post",
                                        url:"songlist",
                                        contentType: "application/x-www-form-urlencoded; charset=utf-8",
                                        data:{
                                            name:name
                                        },
                                        success:function(data){
                                            if(data){//data是一个boolean变量
                                                innerSonglist.showAList(data);
                                                innerSonglist.list.push(data);
                                                $('.dialog-box').replaceWith("<div id='btn-dialogBox'></div>");
                                                $('#dialog-box-mask').remove();//去除遮罩
                                                DJMask.msg('添加成功！');
                                                DJMask.allowAjaxAnni=false;
                                            }else{
                                                DJMask.msg('添加失败了');
                                            }
                                        },
                                        error:function(){
                                            DJMask.msg('网络错误');
                                        }
                                    });
                                }else{
                                    $(".alert").css("color","red");
                                    DJMask.msg('字数不合适');
                                }
                            },
                            cancelValue: '取消',
                            title: '新建歌单',
                            content: "<div class='elegant-aero'>"+
                            "<label>"+
                            "<span>歌单名：</span>"+
                            "<input id='new_name' type='text' placeholder='1-25个字' />"+
                            "</label>"+
                            "</div>"
                        });
                    }else{//没有登录，提示登录或注册
                        DJMask.msg('您还没登录哦！');
                    }
                    DJMask.allowAjaxAnni=true;
                },
                error:function(){
                    DJMask.allowAjaxAnni=true;
                    DJMask.msg('网络出了点问题...');
                }
            });
        });
        //更改歌单名字
        $(document).on('click','#change_gedan',function(){
            var index=$(this).parent().parent().parent().parent().index()-1;
            var ori_name=innerSonglist.list[index].name;
            $("#change_own_list_name_dialog").dialogBox({
                hasClose: true,
                hasBtn: true,
                hasMask:true,
                width:420,
                height:200,
                confirmValue: '确定',
                confirm: function(){
                    var regex=/^.{1,25}$/;
                    if(!regex.test($("#new_name").val())){
                        DJMask.msg('字数不合适');
                        return;
                    }
                    innerSonglist.list[index].name=$("#new_name").val();
                    $.ajax({
                        type:"post",
                        url:"songlist/"+innerSonglist.list[index].id,
                        data:{
                            name:innerSonglist.list[index].name
                        },
                        success:function(data){
                            if(data){
                                DJMask.msg("修改成功！");
                                var a=$('#created li:nth-child('+(index+2)+')'+' a#own_list_name');
                                $(a).text(innerSonglist.list[index].name);
                                $("#song_list_name").text(innerSonglist.list[index].name);
                            }else{
                                DJMask.msg("抱歉，改名失败了，等会再试试吧");
                                innerSonglist.list[index].name=ori_name;
                            }
                        },
                        error:function(){
                            DJMask.msg("网络好像出了点问题...");
                            innerSonglist.list[index].name=ori_name;
                        }
                    });
                    $('.dialog-box').replaceWith("<div id='change_own_list_name_dialog'></div>");
                    $('#dialog-box-mask').remove();
                },
                cancelValue: '取消',
                title: '更改歌单名字',
                content: "<div class='elegant-aero'>"+
                "<label>"+
                "<span>歌单名：</span>"+
                "<input id='new_name' type='text' value='"+ori_name+"'/>"+
                "<br><p class='alert'>1-25个字</p>"+
                "</label>"+
                "</div>"
            });
        });
        //删除歌单
        $(document).on('click','#delete_own_gedan',function () {
            var index=$(this).parent().parent().parent().parent().index()-1;
            var songlist=innerSonglist.list[index];
            $("#delete_songlist_box").dialogBox({
                hasClose: true,
                hasBtn: true,
                hasMask:true,
                width:350,
                height:160,
                confirmValue: '确定',
                cancelValue: '取消',
                title: '删除歌单',
                content: "<span style='margin-left: 15px'>确定删除歌单“"+songlist.name+"”？</span>",
                confirm:function(){
                    innerSonglist.list.splice(index,1);
                    deleteSonglist(songlist.id);
                    $(".added:nth-child("+(index+2)+")").remove();
                }
            });
        });
        //收藏歌曲按钮
        $(document).on('click','.plussong',function(){
            var index=$(this).parent().parent().parent().index();
            var tableid=$(this).parent().parent().parent().parent().attr("id");
            innerSonglist.collectSong(index,tableid);
        });

        //点击收藏歌曲到指定歌单
        $(document).on('click','.ownList',function(){//“收藏歌曲到歌单”弹出界面的点击事件，用来收藏到指定歌单
            var index=$(this).index();
            var span=$('#created').find('li' +
                ':nth-child('+(index+2)+')'+' p.gedan_count span');
            $.ajax({
                type:"post",
                url:"songlist/"+innerSonglist.list[index].id+"/song/"+innerSonglist.songToCollect.key,
                success:function(data){
                    if(data){
                        DJMask.msg("收藏成功");
                        innerSonglist.list[index].songCount++;
                        $(span).text(innerSonglist.list[index].songCount);
                    }else{
                        DJMask.msg("歌单里面已经有这首歌了哦");
                    }
                },
                error:function(XMLHttpRequest){
                    DJMask.msg(XMLHttpRequest.responseJSON.message);
                    console.log(XMLHttpRequest.status);
                }
            });
            $('.dialog-box').replaceWith("<div id='collectSong'></div>");
            $('#dialog-box-mask').remove();
        });

        $(document).on('mouseenter mouseleave','.added',function(event){
            if(event.type === "mouseenter"){
                var content="<a id='delete_own_gedan' class='edit_gedan' href='javascript:void(0)'>删除</a>"+
                    "<a id='change_gedan' class='edit_gedan' href='javascript:void(0)'>修改</a>";
                $(this).find("p.gedan_count").append(content);
            }else if(event.type === "mouseleave"){
                $("a.edit_gedan").remove();
            }
        });
        $(document).on('mouseenter mouseleave','.dli',function(event){
            if(event.type === "mouseenter"){
                $(this).css('background','#f2f2f2');
                $(this).find("a").css('background','#f2f2f2');
            }else if(event.type === "mouseleave"){
                $(this).css('background','#ffffff');
                $(this).find("a").css('background','#ffffff');
            }
        });
        $(document).on('mouseenter mouseleave','.dli1',function(event){
            if(event.type === "mouseenter"){
                var content="<a id='delete_outer_gedan' class='edit_gedan' href='javascript:void(0)'>删除</a>";
                $(this).find("p.gedan_count").append(content);
                $(this).css('background','#f2f2f2');
                $(this).find("a").css('background','#f2f2f2');
            }else if(event.type === "mouseleave"){
                $("a.edit_gedan").remove();
                $(this).css('background','#ffffff');
                $(this).find("a").css('background','#ffffff');
            }
        });
    },
    //获取并展示自创歌单
    getList:function () {
        $.ajax({
            type:"get",
            url:"songlist",
            success:function(list){
                innerSonglist.list=list;
                innerSonglist.showAll();
                sessionStorage.setItem("innerlist",JSON.stringify(innerSonglist));
                DJMask.allowAjaxAnni=true;
            },
            error:function(){
                DJMask.msg("网络好像出了点问题0.0");
            }
        });
    },
    //显示一项内部歌单
    showAList:function (l) {
        var ul=$('#created');
        var li="<li class='dli added'>"+
            "<div>"+
            "<div class='gedan_img'>"+
            "<img src='"+l.img+"'>"+
            "</div>"+
            "<div class='dlite'>"+
            "<p class='gedan_name'><a id='own_list_name' href='javascript:void(0)'>"+l.name+"</a></p>"+
            "<p class='gedan_count'><span>"+l.songCount+"</span>首"+
            "</p>"+
            "</div>"+
            "</div>"+
            "</li>";
        $(ul).append(li);
    },
    showAll:function () {
        $("li.added").remove();
        $.each(this.list,function (i,songlist) {
            innerSonglist.showAList(songlist);
        });
    },
    //展示歌单详细信息，以及包含的歌曲，参数 id：歌单的id；page：将展示的歌曲的页数
    showSongs:function (id,page) {
        $("#songListList").empty();
        var length=0;
        if(this.songs[id.toString()])length=this.songs[id.toString()].length;
        if(length<1)DJMask.msg("这个歌单还没有歌曲");
        else {
            var start=30*(page-1);
            var end=0;
            if(length<page*30)end=length-1;
            else end=page*30-1;
            var data=[];
            for(var i=start;i<=end;i++){
                data.push(getItemOfSong(this.songs[id.toString()][i]));
            }
            new Table(data,{
                id:"songListList",
                itemClass:"songListItem"
            }).show();
        }
        sessionStorage.setItem("innerlist",JSON.stringify(innerSonglist));
    },
    collectSong:function (index,tableid) {
        DJMask.allowAjaxAnni=false;//关闭AJAX动画
        var indexToCollect=index;
        switch (tableid){
            case "searchSongResult":
                indexToCollect+=(Search.showPage[Search.type]-1)*Search.pageCount[Search.type];
                innerSonglist.songToCollect=Search["songList"][indexToCollect];
                break;
            case "songListList":
                indexToCollect+=30*(innerSonglist.page-1);
                innerSonglist.songToCollect=innerSonglist.songs[innerSonglist.idShowing][indexToCollect];
                break;
        }
        $.ajax({
            type:"get",
            url:"getuser",
            success:function(data){
                if(data.name){//如果登录了，显示窗口
                    $("#collectSong").dialogBox({
                        title:"收藏歌曲 "+innerSonglist.songToCollect.name+" 到歌单",
                        hasMask:true,
                        width:350,
                        content:"<ul id='songList2'>"+
                        "</ul>"
                    });
                    $(".dialog-box").css("top","15%");//调整位置
                    $("#songList2").empty();
                    $.each(innerSonglist.list,function(i,songlist){
                        var ul=$("#songList2");
                        var content="<li class='ownList'>"+
                            "<div>"+
                            "<div class='gedan_img'>"+
                            "<img style='margin-left:5px;margin-top:5px' src='"+songlist.img+"'>"+
                            "</div>"+
                            "<div>"+
                            "<p>"+songlist.name+"</p>"+
                            "<p><span>"+songlist.songCount+"</span>首"+
                            "</p>"+
                            "</div>"+
                            "</div>"+
                            "</li>";
                        ul.append(content);
                    });//获取歌单
                }else{
                    DJMask.msg("您还没登录哦");
                }
                DJMask.allowAjaxAnni=true;
            },
            error:function(){
                DJMask.msg("网络好像出了点问题0.0");
                DJMask.allowAjaxAnni=true;
            }
        });
    }
};
var outerSonglist={
    list:[]
};

function deleteSonglist(id){//删除歌单，所有歌单都适用
    $.ajax({
        type:"get",
        url:"deleteSonglist",
        data:{
            id:id
        },
        success:function (data) {
            if(data){
                DJMask.msg("删除成功！");
            }
            else DJMask.msg("失败了，请稍后再试试");
        },
        error:function () {
            DJMask.msg("网络好像出了点问题");
        }
    });
    $('.dialog-box').replaceWith("<div id='delete_songlist_box'></div>");
    $('#dialog-box-mask').remove();
}


