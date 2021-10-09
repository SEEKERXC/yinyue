var songPage={
    songToShow:{},
    commentReulst:{},
    goPage:1,
    hotpage:1,
    recpage:1,
    hotend:10000,
    recend:99999999
};
var hotSongs=[];
songPage.bindEvent=function () {
    $(document).on("click",".songname",function () {
        $(document).scrollTop($(".mid_right").offset().top);
        songPage.init();
        showDiv(4);
        DJMask.allowAjaxAnni=false;
        var tableid=$(this).parent().parent().parent().parent().attr("id");
        var index=$(this).parent().parent().parent().index();
        switch (tableid){
            case "searchSongResult":
                index+=(Search.showPage[Search.type]-1)*Search.pageCount[Search.type];
                songPage.songToShow=Search["songList"][index];
                break;
            case "songListList":
                index+=30*(innerSonglist.page-1);
                songPage.songToShow=innerSonglist.songs[innerSonglist.idShowing][index];
                break;
            case "hot_rank":
                songPage.songToShow=hotSongs[index-1];
                break;
        }
        /*展示歌曲*/
        //获取评论
        $.ajax({
            type:"get",
            url:"comment",
            data:{
                key:songPage.songToShow.key,
                songOuterids:JSON.stringify(songPage.songToShow.songOuterids),
                page:songPage.goPage,
                type:11
            },
            success:function (commentReulst) {
                songPage.commentReulst=commentReulst;
                if (songPage.commentReulst.hotComments.length >= 1) {
                    var len=songPage.commentReulst.hotComments.length;
                    songPage.hotend=Math.ceil(len/10);
                    songPage.showComments(songPage.commentReulst.hotComments,0,len<10?len:10,"hot_comments");
                    $("#hot_num").text(songPage.commentReulst.hotComments.length);
                    $("#hc_now_page").text(songPage.hotpage+"/"+songPage.hotend);
                }
                else {
                    $("#hot_comments").remove();
                }
                var l=songPage.commentReulst.recComments.length;
                songPage.recend=Math.ceil(songPage.commentReulst.count/20);
                songPage.showComments(songPage.commentReulst.recComments,0,l<20?l:20,"recent_comments");
                $(".comment_num").text(songPage.commentReulst.count);
                $("#rc_now_page").text(songPage.recpage+"/"+songPage.recend);
                DJMask.allowAjaxAnni=true;
                $(".c_loading").css("display","none");
                for(var i=0;i<songPage.songToShow.songOuterids.length;i++){
                    for(var j=0;j<songPage.commentReulst.idOutcome.length;j++){
                        if(songPage.songToShow.songOuterids[i].b===songPage.commentReulst.idOutcome[j].b){
                            var id=songPage.songToShow.songOuterids[i].b;
                            var ts=songPage.commentReulst.idTotals;
                            var t=0;
                            for(var k=0;k<ts.length;k++){
                                if(ts[k].b===id)t=ts[k].c;
                            }
                            if(songPage.commentReulst.idOutcome[j].c>=t){
                                songPage.songToShow.songOuterids.splice(i,1);
                                i--;
                            }
                        }
                    }
                }
            },
            error:function () {
                DJMask.msg("网络好像出了点问题0.0");
                DJMask.allowAjaxAnni=true;
                $(".c_loading").css("display","none")
            }
        });
        //展示排行
        $("#hotrankchart").empty();
        $.ajax({
            type:"get",
            url:"hasRank/"+songPage.songToShow.key,
            success:function (data) {
                if(data){
                    $.ajax({
                        type:"get",
                        url:"songRank/"+songPage.songToShow.key,
                        success:function (data) {
                            var ranks=[];
                            var minRank=99999, maxRank=0;
                            var preDay=data[data.length-1].updateTime-1000*60*60*24;
                            var sufDay=data[0].updateTime+1000*60*60*24;
                            console.log(data[0].updateTime);
                            console.log(msToDate(preDay)+msToDate(sufDay));
                            $.each(data,function (i,item) {
                                ranks.push([msToDate(item.updateTime),item.rnum]);
                                minRank=item.rnum<minRank?item.rnum:minRank;
                                maxRank=item.rnum>maxRank?item.rnum:maxRank;
                            });
                            var range=maxRank-minRank;
                            range=range<8?8:range;
                            $.jqplot('hotrankchart', [ranks], {
                                title: '近期全网人气排行',
                                seriesDefaults: {
                                    showMarker:true,
                                    pointLabels: { show:true }
                                },
                                axes: {
                                    xaxis: {
                                        renderer: $.jqplot.DateAxisRenderer,
                                        tickInterval: '1 day',
                                        min:msToDate(preDay),
                                        max:msToDate(sufDay),
                                        tickOptions:{formatString:'%#m月%#d日'}
                                    },
                                    yaxis:{
                                        label:'排行',
                                        labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                        labelOptions: {
                                            fontSize: '12pt'
                                        },
                                        min:maxRank+range*0.2,
                                        max:minRank-range*0.2
                                    }
                                }
                            });
                        },
                        error:function (data) {
                            DJMask.msg(data.message);
                        }
                    });
                }
            }
        });
    });

    $(document).on("click","#hc_left",function () {
        if(songPage.hotpage<=1||songPage.commentReulst.hotComments.length<1){}
        else {
            songPage.hotpage--;
            songPage.showComments(songPage.commentReulst.hotComments,10*(songPage.hotpage-1),10*songPage.hotpage,"hot_comments");
            var h_h=$("#hot_comments").offset().top;
            $(document).scrollTop(h_h);
            $("#hc_now_page").text(songPage.hotpage+"/"+songPage.hotend);
        }
    });
    $(document).on("click","#hc_right",function () {
        if(songPage.hotpage>=songPage.hotend||songPage.commentReulst.hotComments.length<1){}
        else{
            songPage.hotpage++;
            songPage.showComments(songPage.commentReulst.hotComments,10*(songPage.hotpage-1),songPage.hotpage===songPage.hotend?songPage.commentReulst.hotComments.length:10*songPage.hotpage,"hot_comments");
            var h_h=$("#hot_comments").offset().top;
            $(document).scrollTop(h_h);
            $("#hc_now_page").text(songPage.hotpage+"/"+songPage.hotend);
        }
    });
    $(document).on("click","#rc_left",function () {
        if(songPage.recpage<=1||songPage.commentReulst.recComments.length<1){}
        else {
            songPage.recpage--;
            songPage.showComments(songPage.commentReulst.recComments,20*(songPage.recpage-1),20*songPage.recpage,"recent_comments");
            var rc_h=$("#recent_comments").offset().top;
            $(document).scrollTop(rc_h);
            $("#rc_now_page").text(songPage.recpage+"/"+songPage.recend);
        }
    });
    $(document).on("click","#rc_right",function () {
        if(songPage.recpage>=songPage.recend||songPage.commentReulst.recComments.length<1){}
        else {
            songPage.recpage++;
            $("#rc_now_page").text(songPage.recpage+"/"+songPage.recend);
            var rc_h=$("#recent_comments").offset().top;
            $(document).scrollTop(rc_h);
            var page=songPage.recpage;
            var len=songPage.commentReulst.recComments.length;
            if(len===songPage.commentReulst.count||len>=page*20){
                songPage.showComments(songPage.commentReulst.recComments,20*(page-1),20*page,"recent_comments");
            }else if (len<page*20){
                $("#recent_comments").find("ul:first-child").empty();
                $("#rc_loading").css("display","inline");
                songPage.goPage++;
                DJMask.allowAjaxAnni=false;
                $.ajax({
                    type:"get",
                    url:"comment",
                    data:{
                        uniKey:songPage.songToShow.UniqueKey,
                        outerIdObjectStr:JSON.stringify(songPage.songToShow.outerIds),
                        page:songPage.goPage,
                        type:11
                    },
                    success:function (commentResult) {
                        songPage.commentReulst.recComments=songPage.commentReulst.recComments.concat(commentResult.recComments);
                        for(var p in songPage.commentReulst.idOutcome){
                            for(var q in commentResult.idOutcome){
                                if(songPage.commentReulst.idOutcome[p].b===commentResult.idOutcome[q].b)
                                    songPage.commentReulst.idOutcome[p].c+=commentResult.idOutcome[q].c;
                            }
                        }
                        songPage.showComments(songPage.commentReulst.recComments,(songPage.recpage-1)*20,songPage.recpage*20,"recent_comments");
                        DJMask.allowAjaxAnni=true;
                        $(".c_loading").css("display","none");
                        for(var i=0;i<songPage.songToShow.outerIds.length;i++){
                            for(var j=0;j<songPage.commentReulst.idOutcome.length;j++){
                                if(songPage.songToShow.outerIds[i].b===songPage.commentReulst.idOutcome[j].b){
                                    var id=songPage.songToShow.outerIds[i].b;
                                    var ts=songPage.commentReulst.idTotals;
                                    var t=0;
                                    for(var k=0;k<ts.length;k++){
                                        if(ts[k].b===id)t=ts[k].c;
                                    }
                                    if(songPage.commentReulst.idOutcome[j].c>=t){
                                        songPage.songToShow.outerIds.splice(i,1);
                                        i--;
                                    }
                                }
                            }
                        }
                    },
                    error:function () {
                        DJMask.msg("网络好像出了点问题0.0");
                        DJMask.allowAjaxAnni=true;
                        $(".c_loading").css("display","none");
                    }
                });
            }
        }
    });
};
songPage.showComments=function (list,start,end,divId) {
    $("#"+divId+" ul:first-child").empty();
    for(var i=start;i<end;i++)appendComment(list[i],divId);
};
songPage.init=function () {
    this.songToShow={};
    this.commentReulst={};
    this.goPage=1;
    this.hotpage=1;
    this.recpage=1;
    this.hotend=10000;
    this.recend=99999999;
};