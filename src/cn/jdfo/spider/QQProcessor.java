package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import cn.jdfo.domain.*;
import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Pair;
import cn.jdfo.tool.Three;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Json;

/**
 * 
* @ClassName: QQProcessor 
* @Description: QQ音乐的爬虫
* @author 肖飞  13319202082@163.com
* @date 2017年12月17日 下午1:28:53
 */
@Component("qq")
@SessionScope
public class QQProcessor implements SongSpider {
//	QQ音乐所有的搜索数据都通过json生成，下面是关于链接和参数的解释
//	基本链接：https://c.y.qq.com/soso/fcgi-bin/client_search_cp
//	参数：
//	p：页数；    w：关键词；   t：搜索类型，各种数字表示如下类型
//	0：音乐    7：歌词   8：专辑     9：歌手    12：MV
//	n：一页返回的歌曲数目

	private SearchResult searchResult;
	private Semaphore searchMutex;//搜索互斥量
	private CommentResult commentResult;
	private int commentFinished;
	private static final String searchSongUrl="https://c\\.y\\.qq\\.com/soso/fcgi-bin/client_search_cp\\?t=0.*";
	private static final String searchSingerUrl="https://c\\.y\\.qq\\.com/soso/fcgi-bin/client_search_cp\\?t=9.*";
	private static final String searchAlbumUrl="https://c\\.y\\.qq\\.com/soso/fcgi-bin/client_search_cp\\?t=8.*";
	private static final String fetchCommentUrl="https://c\\.y\\.qq\\.com/base/fcgi-bin/fcg_global_comment_h5\\.fcg.*";
	private final SongTemplate songTemplate;
	private Site site=Site.me()
			.setDomain("https://y.qq.com/")
			.setRetryTimes(1)
			.setSleepTime(1000)
			.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
			.setCharset("utf-8")
			.setTimeOut(10000);
	private String url;
	private String commentUrl;

	@Autowired
	public QQProcessor(SongTemplate songTemplate) {
		super();
		this.url="";
		this.searchMutex=new Semaphore(1);
		this.commentFinished=0;
		this.commentUrl="";
		this.songTemplate = songTemplate;
	}
	
	@Override
	public void process(Page page) {
		Json json=page.getJson();
		if(page.getUrl().regex(searchSongUrl).match()){
			List<String> names=json.jsonPath("$.data.song.list[*].songname").all();
			List<String> songmids=json.jsonPath("$.data.song.list[*].songmid").all();
			List<String> urls=new ArrayList<>();
			for(String id:songmids){
				String url="https://y.qq.com/n/yqq/song/"+id+".html";
				urls.add(url);
			}
			List<String> songIds=json.jsonPath("$.data.song.list[*].songid").all();
			List<String> albummIds=json.jsonPath("$.data.song.list[*].albummid").all();
			List<String> albumIds=json.jsonPath("$.data.song.list[*].albumid").all();
			List<String> albumUrls=new ArrayList<>();
			for(String id:albummIds){
				String url="https://y.qq.com/n/yqq/album/"+id+".html";
				albumUrls.add(url);
			}
			List<List<String>> singerlist=new ArrayList<>();
			for(int i=0;i<names.size();i++){
				List<String> singers=json.jsonPath("$.data.song.list["+i+"].singer[*].name").all();
				singerlist.add(singers);
			}
			List<String> albumNames=json.jsonPath("$.data.song.list[*].albumname").all();
			int total=Integer.parseInt(json.jsonPath("$.data.song.totalnum").get());
			this.searchResult.getTotals().add(total);
			try {
				this.songTemplate.addSong(this.searchResult.getResult(), songIds, names, urls, singerlist, albumUrls, albumNames, "QQ");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else if(page.getUrl().regex(searchSingerUrl).match()){
			List<String> ids=json.jsonPath("$.data.singer.list[*].singerMID").all();
			List<String> names=json.jsonPath("$.data.singer.list[*].singerName").all();
			List<String> imgUrls=json.jsonPath("$.data.singer.list[*].singerPic").all();
			this.songTemplate.addSinger(this.searchResult.getResult(),ids,names,imgUrls);
		}else if(page.getUrl().regex(searchAlbumUrl).match()){
			List<String> albumIds=json.jsonPath("$.data.album.list[*].albumMID").all();
			List<String> urls=new ArrayList<>();
			for(String id: albumIds){
				urls.add("https://y.qq.com/n/yqq/album/"+id+".html");
			}
			List<String> albumNames=json.jsonPath("$.data.album.list[*].albumName").all();
			List<String> imgUrls=json.jsonPath("$.data.album.list[*].albumPic").all();
			//List<String> intro=new ArrayList<>();//没有简介
			List<String> publishtime=json.jsonPath("$.data.album.list[*].publicTime").all();
			List<String> singername=json.jsonPath("$.data.album.list[*].singerName").all();
			List<String> songcount=json.jsonPath("$.data.album.list[*].song_count").all();
			songTemplate.addAlbum(this.searchResult.getResult(),albumIds,urls,albumNames,imgUrls,publishtime,singername,songcount,new ArrayList<>(),"QQ");
		}else if(page.getUrl().regex(fetchCommentUrl).match()){
			JSONObject jsonObject = JSON.parseObject(json.toString());
			for(int a=0;a<=1;a++){
				if(a==0&&!jsonObject.containsKey("comment"))continue;
				if(a==1&&!jsonObject.containsKey("hot_comment"))break;
				String b = a==0?"":"hot_";
				List<String> users=json.jsonPath("$."+b+"comment.commentlist[*].nick").all();
				List<String> content=new ArrayList<>();
				SongComment[] beReplied=new SongComment[users.size()];
				List<String> strTime=json.jsonPath("$."+b+"comment.commentlist[*].time").all();
				List<Long> time=new ArrayList<>();
				for(String s: strTime){
					time.add(Long.parseLong(s)*1000);
				}
				List<String> imgUrl=json.jsonPath("$."+b+"comment.commentlist[*].avatarurl").all();
				List<String> strLike=json.jsonPath("$."+b+"comment.commentlist[*].praisenum").all();
				List<Integer> like=new ArrayList<>();
				for(String s: strLike){
					like.add(Integer.parseInt(s));
				}

				JSONArray jsonArray = jsonObject.getJSONObject(b+"comment").getJSONArray("commentlist");
				for(int i=0,len=jsonArray.size();i<len;i++){
					if(jsonArray.getJSONObject(i).get("middlecommentcontent")!=null){
						JSONObject m = jsonArray.getJSONObject(i).getJSONArray("middlecommentcontent").getJSONObject(0);
						content.add(m.getString("subcommentcontent"));
						beReplied[i]=new SongComment();
						beReplied[i].setAuthor(m.getString("replyednick").substring(1));
						beReplied[i].setContent(jsonArray.getJSONObject(i).getString("rootcommentcontent"));
					}else{
						content.add(jsonArray.getJSONObject(i).getString("rootcommentcontent"));
					}
				}
				if(a<1){
					songTemplate.addComments(commentResult.getRecComments(),users,content,time,imgUrl,like,"QQ",beReplied);
					int total=Integer.parseInt(json.jsonPath("$.comment.commenttotal").get());
					commentResult.getTotals().add(total);
					String oid=page.getUrl().get().substring(page.getUrl().get().indexOf("&topid=")+7,page.getUrl().get().indexOf("&cmd="));
					commentResult.getIdTotals().add(new Three<>("QQ",oid,total));
					commentResult.getIdOutcome().add(new Three<>("QQ",oid,content.size()));
				} else songTemplate.addComments(commentResult.getHotComments(),users,content,time,imgUrl,like,"QQ",beReplied);
			}
			this.commentFinished--;return;
		}
		this.searchMutex.release();
	}

	@Override
	public Site getSite() {
		return this.site;
	}

	@Override
	public void search(String keyword, int page, int type, SearchResult result) throws InterruptedException {
		this.searchMutex.acquire();
		this.searchResult=result;
		switch (type){
			case Constant.SEARCH_SONG:
				this.url="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?t=0&n=50&p="+(page-1)+"&w="+keyword
						+"&format=json&outCharset=utf-8";
				break;
			case Constant.SEARCH_SONGLIST:
				this.searchMutex.release();
				return;
			case Constant.SEARCH_SINGER:
				this.url="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?t=9&n=20&p="+(page-1)+"&w="+keyword
						+"&format=json&outCharset=utf-8";
				break;
			case Constant.SEARCH_ALBUM:
				this.url="https://c.y.qq.com/soso/fcgi-bin/client_search_cp?t=8&n=20&p="+(page-1)+"&w="+keyword
						+"&format=json&outCharset=utf-8";
				break;
		}
		new Thread(this).start();
	}

	@Override
	public void fetchComments(String id, int page, int type, CommentResult result) throws InterruptedException {
		if(this.commentFinished<=0)this.commentResult=result;
		this.commentFinished++;
		while(!this.commentUrl.equals(""))Thread.sleep(20);
		switch (type){
			case Constant.SONG_COMMENT:
				this.commentUrl="https://c.y.qq.com/base/fcgi-bin/fcg_global_comment_h5.fcg?format=json&biztype=1&topid="+id+"&cmd=8&pagenum="+(page-1)+"&pagesize=20";
				break;
		}
		new Thread(this).start();
	}

	@Override
	public boolean searchFinished() {
		return this.searchMutex.availablePermits()>0;
	}

	@Override
	public boolean gettingCommentsFinished() {
		return this.commentFinished<=0;
	}

	@Override
	public String getPlatformName() {
		return "QQ";
	}

	@Override
	public void run() {
		if(!this.url.equals("")){
			Spider.create(this)
					.addUrl(this.url)
					.run();
			this.url="";
		}else if(!this.commentUrl.equals("")){
			Spider.create(this)
					.addUrl(this.commentUrl)
					.run();
			this.commentUrl="";
		}

	}

//	public static void main(String[] args) {
//		String url="https://c.y.qq.com/base/fcgi-bin/fcg_global_comment_h5.fcg?format=json&biztype=1&topid=1531817&cmd=8&pagenum=1&pagesize=20";
//		Spider.create(new QQProcessor())
//				.addUrl(url)
//				.run();
//	}

}
