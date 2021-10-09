
//搜索功能对象
var Search={
	keyword:"",
	searchPage:{
		song:1,
		songlist:1,
		singer:1,
		album:1
	},
    showPage:{
	    song:1,
        songlist:1,
        singer:1,
        album:1
    },
    pageCount:{
	    song:30,
        songlist:20,
        singer:20,
        album:20
    },
	endPage:{
		song:10000,
		songlist:10000,
		singer:10000,
		album:10000
	},
    total:{
	    song:0,
        songlist:0,
        singer:0,
        album:0
    },
	/*搜索类型 :
	歌曲：song
	歌手：singer
	歌单：songlist
	专辑：album*/
	type:"song",
    //用来保存搜索歌曲的结果
    songList:[],
	//搜索歌单的结果
	songlistList:[],
	//搜索歌手的结果
	singerList:[],
	//搜索专辑的结果
	albumList:[],
    //搜索结果总数
    //展开搜索，完成后保存并展示结果
	start:function () {
	    var inttype=0;
	    switch (Search.type){//关于搜索类型，在常量类中有定义
            case "song":inttype=1;break;
            case "singer":inttype=2;break;
            case "songlist":inttype=3;break;
            case "album":inttype=4;break;
        }
        $.ajax({
            contentType: "application/x-www-form-urlencoded; charset=utf-8",
            data:{
                "keyword":encodeURIComponent(Search.keyword),
                //这里用encodeURIComponent来解决中文乱码问题，不添加的话会乱码
                "page":Search.searchPage[Search.type],
				"type":inttype
            },
            url:"search",
            success:function(data){
                if(data.result.length>0){
                    switch (Search.type){
						case "song":
							Search.songList=Search.songList.concat(data.result);
							break;
						case "songlist":
							Search.songlistList=Search.songlistList.concat(data.result);
							break;
						case "singer":
							Search.singerList=Search.singerList.concat(data.result);
							break;
						case "album":
							Search.albumList=Search.albumList.concat(data.result);
							break;
					}
					Search.total[Search.type]=data.total;
					Search.showResult();
                }
            },
            error:function(){
                DJMask.msg("网络错误了，请稍后重试");
            }
        });
    },
    //展示搜索结果
	showResult:function () {
	    var data=[];
	    var page=this.showPage[this.type];
	    var pagecount=this.pageCount[this.type];
	    var len=this[this.type+"List"].length;
	    var start=pagecount*(page-1);
	    var end=0;
	    if(len-pagecount*(page-1)<pagecount){
	        this.endPage[this.type]=page;
	        end=len;
        }else end=pagecount*page;
        $("#keyword").text(this.keyword);
        $("#page,#now_page").text(this.showPage[this.type]);
        $("#number").text(this[this.type+"List"].length);
        $("#search_total").text(this.total[this.type]);
        $("#searchTypeDiv").find("li").each(function () {
            $(this).css("border-bottom","1px solid #e7b7b7").css("border-top","2px solid #b5b5b5").css("background","#f8f8f8");
            if ($(this).index() < 5) $(this).css("border-right", "0px");
            if ($(this).index() > 0) $(this).css("border-left", "0px");
        })
        $("#searchTypeDiv").find("li").each(function () {
            if ($(this).attr("tag") === Search.type) {
                $(this).css("border-bottom","0px").css("border-top","2px solid red").css("background","#fbfbfb");
                if($(this).index()<5)$(this).css("border-right","1px solid #e7b7b7");
                if($(this).index()>0)$(this).css("border-left","1px solid #e7b7b7");
            }
        });
        sessionStorage.setItem("search",JSON.stringify(Search));
		switch (this.type){
            case "song":
                this.songList.forEach(function (value) {
                    data.push(getItemOfSong(value));
				});
                new Table(data.slice(start,end),{
                    id:"searchSongResult",
                    itemClass:"songListItem"
                }).show();
                $(".num_type").text("首歌曲");
                return;
            case "songlist":
                $.each(this.songlistList,function (i,value) {
                    var item=[];
                    if(value.collectCount>9999){
                        var wan=value.collectCount/=10000;
                        value.collectCount=wan+"万";
                    }
                    item.push("<a target='_blank' title='"+value.name+"' href='"+value.url+"'><img class='cover' src='"+value.img+"' onerror=\"this.src='img/lose.jpg'\"></a>");
                    item.push("<a target='_blank' title='"+value.name+"' href='"+value.url+"'>"+value.name+"</a>");
                    item.push("");
                    item.push("<a href='javascript:void(0)' title='"+value.authorName+"'>"+"by:"+value.platform+"-"+value.authorName+"</a>");
                    item.push(value.collectCount+"收藏");
                    data.push(item);
                });
                new Table(data.slice(start,end),{
                    id:"searchSonglistResult",
                    itemClass:"songlistListItem",
                }).show();
                $(".num_type").text("张歌单");
                return;
            case "album":
                this.albumList.forEach(function (value) {
                    var item=[];
                    item.push("<a title='"+value.singer.name+"-"+value.name+"'><img class='cover' src='"+value.img+"' onerror=\"this.src='img/lose.jpg'\"></a>");
                    item.push("<a title='"+value.name+"'>"+value.name+"</a>");
                    item.push("");
                    item.push("<a title='"+value.singer.name+"'>"+value.singer.name+"</a>");
                    if(value.time.length>10)value.time=value.time.substring(0,10);
                    if(value.time.indexOf("0000")>=0||value.time==="")value.time="发布时间未知";
                    item.push(value.time);
                    if(value.score<0.1)value.score="暂无评分";
                    if(value.score>10)value.score=value.score/10;
                    item.push(value.score);
                    data.push(item);
                });
                new Table(data.slice(start,end),{
                    id:"searchAlbumResult",
                    itemClass:"albumListItem",
                }).show();
                $(".num_type").text("张专辑");
                return;
			case "singer":
			    var tb=$("#searchSingerResult");
			    $(tb).empty();
				this.singerList.slice(start,end).forEach(function (value) {
					var item=[];
                    var res="<li class='singerListItem'><div><img src='"+value.img+"' onerror=\"this.src='img/lose.jpg'\">";
                    res+="</img></div>";
                    res+="<p>"+value.name+"</p></li>";
                    tb.append(res);
				});
                $(".num_type").text("位歌手");
				return;
        }
        $("#return_search").empty();
    },
	//新开搜索，初始化所有数据，如果关键词没变，返回true，变了返回false
	init:function () {
        $("#return_search").empty();
        if(this.keyword===$.trim($("#search_ipt").val()))return true;
        this.keyword = $("#search_ipt").val();
        this.keyword = $.trim(this.keyword);
        this.type="song";
        for(var sp in this.showPage){
        	this.showPage[sp]=1;
		}
		for(var p in this.searchPage){
        	this.searchPage[p]=1;
		}
		this.songList=[];
        this.songlistList=[];
        this.singerList=[];
        this.albumList=[];
        return false;
    },
	bindEvent:function () {
	    //IE只有keyCode属性，FireFox中有which和charCode属性，Opera中有keyCode和which属性，Chrome中有which属性
        $("input[id=search_ipt]").keypress(function(e){
            var eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
            if (eCode === 13){
                var b = Search.init();
                if(Search.keyword.length<1)return;
                $("#result").empty();
                $("#result").append("<ul id='search"+Search.type.substring(0,1).toUpperCase()+Search.type.substring(1)+"Result' class='"+Search.type+"ListStyle'></ul>");
                showDiv(1);
                if(b)Search.showResult();
                else Search.start();
            }
        });
        $(document).on('click','#prev',function(){
            if(Search.showPage[Search.type]===1)return;
			Search.showPage[Search.type]--;
			Search.showResult();
		});
        //原本85行的代码，将搜索功能集中之后，缩减到了14行（主要是因为设置了Search.type这个变量）
        $(document).on('click','#next',function(){
        	var tp=Search.type;
        	if(++Search.showPage[tp]===Search.endPage[tp])Search.showResult();
			else if(Search.showPage[tp]<Search.endPage[tp]){
				var pg=Search.showPage[tp];
				var list=Search[tp+"List"];
				var edpg=Search.endPage[tp];
				var pgcnt=Search.pageCount[tp];
				if(list.length-(pgcnt*(pg-1))<pgcnt){
					$("#search"+Search.type.substring(0,1).toUpperCase()+Search.type.substring(1)+"Result").empty();
					Search.searchPage[tp]++;
					Search.start();
				}else Search.showResult();
				window.location.href="#";
			}else {
                --Search.showPage[tp];
			    DJMask.msg("没有更多了");
            }
		});
        $(document).on('mousedown',function () {
            //hide menu
            var search_song_menu=$(".search_song_menu");
            var search_songlist_menu=$(".search_songlist_menu");
            if($(search_song_menu).css("display")!=="none"){
                $(search_song_menu).css("display","none");//关闭菜单
                $(document).off('mousedown','.search_song_menu li:nth-child(6)');//解除菜单事件绑定
            }
            if($(search_songlist_menu).css("display")!=="none")$(search_songlist_menu).css("display","none");
        });
        //switch search type
        $(document).on('click','#searchTypeDiv li',function () {
            var tag=$(this).attr("tag");
            if (Search.type === tag) {
                return;
            }
            var li=null;
            $("#searchTypeDiv").find("li").each(function () {
                if ($(this).attr("tag") === Search.type) {
                    li = this;
                }
            });
            $(li).css("border-bottom","1px solid #e7b7b7").css("border-top","2px solid #b5b5b5").css("background","#f8f8f8");
            if ($(li).index() < 5) $(li).css("border-right", "0px");
            if ($(li).index() > 0) $(li).css("border-left", "0px");
            Search.type=tag;
            var result=$("#result");
            $(result).empty();
            $(result).append("<ul id='search"+Search.type.substring(0,1).toUpperCase()+Search.type.substring(1)+"Result' class='"+Search.type+"ListStyle'></ul>");
            for(var tp in Search){
                if(tp===Search.type+"List"){
                    if(Search[tp].length>0)Search.showResult();
                    else Search.start();
                }
            }
        });
        $(document).on('mouseenter mouseleave','#searchTypeDiv li',function(event){
            if(event.type === "mouseenter"){
                $(this).css("border-top","2px solid red");
            }else if(event.type === "mouseleave"){
                var a=$(this).css("background-color");
                if(a!=="rgb(251, 251, 251)")$(this).css("border-top","2px solid #b5b5b5");
            }
        });
        $(document).on('mouseenter','.songListItem',function(){
            var html="<a title='播放歌曲' class='playsong'></a><a title='收藏' class='plussong'></a>";
            $(this).find("li:nth-child(2)").html(html);
        });
        $(document).on('mouseleave','.songListItem',function(){
            $(this).find("li:nth-child(2)").text("");
        });
        $(document).on('mouseenter','.songlistListItem',function () {
            var html="<a title='收藏' class='collectSonglist'></a>";
            $(this).find("li:nth-child(3)").html(html);
        });
        $(document).on('mouseleave','.songlistListItem',function () {
            $(this).find("li:nth-child(3)").text("");
        });
        //right click to show menu for song operation
        $(document).on('mouseup','.songListItem',function (e) {
            var eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
            if(eCode===3){
                $(".search_song_menu").css('display','block').css('left',e.pageX).css('top',e.pageY);
                var index=$(this).index();
                var tableid=$(this).parent().attr("id");
                $(document).on('mousedown','.search_song_menu li:nth-child(6)',function () {
                    innerSonglist.collectSong(index,tableid);
                });
            }
        });
        //show menu for songlist operation
        $(document).on('mouseup','.songlistListItem',function (e) {
            var eCode = e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
            if(eCode===3){
                $(".search_songlist_menu").css('display','block').css('left',e.pageX).css('top',e.pageY);
                var index=$(this).index()+(Search.showPage["songlist"]-1)*Search.pageCount["songlist"];
                $(document).on("mousedown",".search_songlist_menu li:nth-child(4)",function (e2) {
                    var ec = e2.keyCode ? e2.keyCode : e2.which ? e2.which : e2.charCode;
                    if(ec===1){
                        var url=Search.songlistList[index].url;
                        window.open(url);
                    }
                    $(document).off("mousedown",".search_songlist_menu li:nth-child(4)");
                });
            }
        });
        //show menu for song operation
        $(document).on('mouseup','.songmore',function (e) {
            $(".search_song_menu").css('display','block').css('left',e.pageX).css('top',e.pageY);
            var index=$(this).parent().parent().parent().index();
            var tableid=$(this).parent().parent().parent().parent().attr("id");
            $(document).on('mousedown','.search_song_menu li:nth-child(6)',function () {
                innerSonglist.collectSong(index,tableid);
            });
        });
    }
};

                        ////////////////////////////////////////////////////////////////////
                        //                          _ooOoo_                               //
                        //                         o8888888o                              //
                        //                         88" . "88                              //
                        //                         (| ^_^ |)                              //
                        //                         O\  =  /O                              //
                        //                      ____/`---'\____                           //
                        //                    .'  \\|     |//  `.                         //
                        //                   /  \\|||  :  |||//  \                        //
                        //                  /  _||||| -:- |||||-  \                       //
                        //                  |   | \\\  -  /// |   |                       //
                        //                  | \_|  ''\---/''  |   |                       //
                        //                  \  .-\__  `-`  ___/-. /                       //
                        //                ___`. .'  /--.--\  `. . ___                     //
                        //              ."" '<  `.___\_<|>_/___.'  >'"".                  //
                        //            | | :  `- \`.;`\ _ /`;.`/ - ` : | |                 //
                        //            \  \ `-.   \_ __\ /__ _/   .-` /  /                 //
                        //      ========`-.____`-.___\_____/___.-`____.-'========         //
                        //                           `=---='                              //
                        //      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^        //
                        //               佛祖保佑       永无BUG     永不修改              //
                        ////////////////////////////////////////////////////////////////////


