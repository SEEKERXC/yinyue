package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Pair;
import cn.jdfo.tool.Three;
import org.apache.http.message.BasicNameValuePair;
import org.omg.CORBA.NameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.annotation.SessionScope;

import cn.jdfo.domain.*;


import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.HttpConstant;

/**
 * 
* @ClassName: XiamiProcessor 
* @Description: 虾米音乐的爬虫
* @author 肖飞  13319202082@163.com
* @date 2017年12月15日 上午7:52:15
 */
@Component("xiami")
@SessionScope
public class XiamiProcessor implements SongSpider {
	
	private static final String searchSongUrl="http://www\\.xiami\\.com/search/song/.*";
	private static final String searchSonglistUrl="http://www\\.xiami\\.com/search/collect/.*";
	private static final String searchSingerUrl="http://www\\.xiami\\.com/search/artist/.*";
	private static final String searchAlbumUrl="http://www\\.xiami\\.com/search/album/.*";
	private static final String fetchCommentUrl="http://www.xiami.com/commentlist/turnpage/id/.*";
	private static final String songInfoUrl="http://www.xiami.com/song/.*";
	private SearchResult searchResult;
	private Semaphore searchMutex;//搜索互斥量
	private CommentResult commentResult;
	private int commentFinished;//评论获取完成标识
	private Site site=Site.me()
			.setDomain("http://www.xiami.com")
			.setRetryTimes(1)
			.setSleepTime(1000)
			.addCookie("login_method","mobilelogin")
			.addCookie("user","336160367%22%E4%B8%8D%E5%A6%82%E5%90%83%E8%8C%B6%E5%8E%BB%22images%2Fdefault%2Fxiami_7%2Favatar_new.png%220%22405%22%3Ca+href%3D%27http%3A%2F%2Fwww.xiami.com%2Fwebsitehelp%23help9_3%27+%3ELv4%3C%2Fa%3E%220%220%22878%225d00b0c0b1%221522816846")
			.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
			.setCharset("utf-8")
			.setTimeOut(10000);
	private final SongTemplate songTemplate;
	private String url;
	private String commentUrl;
	private String hotCommentUrl;

	@Autowired
	public XiamiProcessor(SongTemplate songTemplate) {
		super();
		this.url="";
		this.commentUrl="";
		this.hotCommentUrl="";
		this.searchMutex=new Semaphore(1);
		this.commentFinished=0;
		this.songTemplate = songTemplate;
	}
	
	@Override
	public void process(Page page) {
		Html html=page.getHtml();
		if(page.getUrl().regex(searchSongUrl).match()){//将传来的搜索结果url上的所有符合条件的歌曲添加到传来的List<Song>中
			List<String> names=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div[1]/div[1]/table/tbody/tr/td[2]/a[1]/@title").all();
			List<String> urls=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div[1]/div[1]/table/tbody/tr/td[2]/a[1]/@href").all();
			List<String> ids=new ArrayList<>();
			for(String url: urls){
				if(url.contains("song")){
					int index=url.indexOf("song")+5;
					ids.add(url.substring(index));
				}else ids.add("");
			}
			List<String> albumUrls=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div[1]/div[1]/table/tbody/tr/td[4]/a/@href").all();
			List<String> albumNames=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div[1]/div[1]/table/tbody/tr/td[4]/a/@title").all();
			List<List<String>> singerlist=new ArrayList<>();
			List<String> tds=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[1]/table/tbody/tr/td[3]").all();
			for(int i=0;i<tds.size();i++){
				List<String> singers=new ArrayList<>();
				List<String> as=new ArrayList<>();
				int index=tds.get(i).indexOf("<a target=");
				while(index<tds.get(i).length()&&index>0){
					String a=tds.get(i).substring(index, tds.get(i).indexOf("</a>",index)+4);
					as.add(a);
					index=tds.get(i).indexOf("<a",index+1);
				}
				if(as.size()==1){
					index=as.get(0).indexOf("title=");
					String singer=as.get(0).substring(index+7, as.get(0).indexOf("\"", index+7));
					singers.add(singer);
				}else if(as.size()>1){
					for(String a:as){
						if(a.startsWith("<a href=")){
							index=a.indexOf(">");
							String singer=a.substring(index+1,a.indexOf("<",index+1));
							singers.add(singer);
						}
					}
				}
				singerlist.add(singers);
			}
			String sss=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div[1]/p/b[1]/text()").get();
			int total=Integer.parseInt(sss);
			this.searchResult.getTotals().add(total);
			try {
				this.songTemplate.addSong(this.searchResult.getResult(), ids, names, urls, singerlist, albumUrls, albumNames, "虾米");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else if(page.getUrl().regex(searchSonglistUrl).match()){//搜索歌单
			List<String> imgUrls=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/div/a/img/@src").all();
			List<String> urls=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/div/a/@href").all();
			List<String> names=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/h3/a/@title").all();
			List<String> authors=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/p[1]/span[1]/a[1]/@title").all();
			List<String> updateTime=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/p[1]/span[2]/text()").all();
			List<String> collects=html.xpath("//*[@id='wrapper']/div[1]/div/div[2]/div[2]/ul/li/div/p[2]/span/text()").all();
			List<Integer> collectCount=new ArrayList<>();
			for(String s:collects){
				collectCount.add(Integer.parseInt(s));
			}
			this.songTemplate.addSonglist(this.searchResult.getResult(), urls, names, authors, imgUrls, collectCount,"虾米");
		}else if(page.getUrl().regex(searchSingerUrl).match()){
			List<String> ids=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[2]/a/@href").all();//这个id是详情页面
			List<String> names=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[2]/a/@title").all();
			List<String> imgUrls=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[1]/a/img/@src").all();
			this.songTemplate.addSinger(this.searchResult.getResult(),ids,names,imgUrls);
		}else if(page.getUrl().regex(searchAlbumUrl).match()){
			//List<String> intro=new ArrayList<>();//没有简介
			List<String> urls=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[2]/a[1]/@href").all();
			List<String> ids=new ArrayList<>();
			for(String url:urls){
				ids.add(url.substring(27));
			}
			List<String> albumNames=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[2]/a[1]/@title").all();
			List<String> imgUrls=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[1]/a/img/@src").all();
			List<String> publishtime=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[4]/text()").all();
			List<String> singername=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[2]/a[2]/@title").all();
			List<String> songcount=new ArrayList<>();
			List<String> scores=html.xpath("//*[@id='wrapper']/div[2]/div[2]/div/div[2]/div/div[2]/div[1]/ul/li/div/p[3]/em/text()").all();
			songTemplate.addAlbum(this.searchResult.getResult(),ids,urls,albumNames,imgUrls,publishtime,singername,songcount,scores,"虾米");
		}else if(page.getUrl().regex(fetchCommentUrl).match()){
			List<String> authors=html.xpath("/html/body/ul/li/div/div[1]/span[1]/a/@title").all();
			List<String> authorImgs=html.xpath("/html/body/ul/li/div/p/a/img/@src").all();
			List<String> times=html.xpath("/html/body/ul/li/div/div[1]/span[2]/text()").all();
			List<Long> time=new ArrayList<>();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for(String s:times){
				Date date=new Date();
				try {
					date=dateFormat.parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				time.add(date.getTime());
			}
			List<String> strLike=html.xpath("/html/body/ul/li/div/div[1]/span[3]/a/@rel").all();
			List<Integer> like=new ArrayList<>();
			for(String s: strLike){
				like.add(Integer.parseInt(s));
			}
			List<String> contents=html.xpath("/html/body/ul/li/div/div[2]/div/text()").all();
			songTemplate.addComments(commentResult.getRecComments(),authors,contents,time,authorImgs,like,"虾米",null);
			String oid=page.getUrl().get().substring(page.getUrl().get().indexOf("id/")+3,page.getUrl().get().indexOf("/page/"));
			commentResult.getIdOutcome().add(new Three<>("虾米",oid,contents.size()));
			this.commentFinished--;return;
		}else if(page.getUrl().regex(songInfoUrl).match()){
			List<String> authors=html.xpath("//div[@class='hotComment']/ul/li/div/div[1]/span[1]/a/@title").all();
			List<String> imgurls=html.xpath("//div[@class='hotComment']/ul/li/div/p/a/img/@src").all();
			List<String> times=html.xpath("//div[@class='hotComment']/ul/li/div/div[1]/span[2]/text()").all();
			List<Long> time=new ArrayList<>();
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			for(String s:times){
				Date date=new Date();
				try {
					date=dateFormat.parse(s);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				time.add(date.getTime());
			}
			List<String> strLike=html.xpath("//div[@class='hotComment']/ul/li/div/div[1]/span[3]/a/@rel").all();
			List<Integer> like=new ArrayList<>();
			for(String s: strLike){
				like.add(Integer.parseInt(s));
			}
			List<String> contents=html.xpath("//div[@class='hotComment']/ul/li/div/div[2]/div/text()").all();
			int total=Integer.parseInt(html.xpath("//*[@id='wall_list']/p/span/text()").get());
			commentResult.getTotals().add(total);
			songTemplate.addComments(commentResult.getHotComments(),authors,contents,time,imgurls,like,"虾米",null);
			String oid=page.getUrl().get().substring(page.getUrl().get().indexOf("song/")+5);
			commentResult.getIdTotals().add(new Three<>("虾米",oid,total));
			this.commentFinished--;return;
		}
		this.searchMutex.release();
	}

	@Override
	public String getPlatformName() {
		return "虾米";
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
				this.url=this.site.getDomain()+"/search/song/page/"+page+"?key="+keyword;
				break;
			case Constant.SEARCH_SONGLIST:
				this.url="http://www.xiami.com/search/collect/page/"+page+"?key="+keyword+"&order=favorites";
				break;
			case Constant.SEARCH_SINGER:
				this.url="http://www.xiami.com/search/artist/page/"+page+"?key="+keyword;
				break;
			case Constant.SEARCH_ALBUM:
				this.url="http://www.xiami.com/search/album/page/"+page+"?key="+keyword;
				break;
		}
		new Thread(this).start();
	}

	@Override
	public void fetchComments(String id, int page, int type, CommentResult result) throws InterruptedException {
		if(this.commentFinished<=0)this.commentResult=result;
		this.commentFinished+=2;
		while(!this.commentUrl.equals(""))Thread.sleep(20);
		switch (type){
			case Constant.SONG_COMMENT:
				this.commentUrl="http://www.xiami.com/commentlist/turnpage/id/"+id+"/page/"+page+"/ajax/1?type=4";
				if(page<=1)this.hotCommentUrl="http://www.xiami.com/song/"+id;
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
//
//	public static void main(String[] args){
//		String url="http://www.xiami.com/search/song/?key=%E5%91%8A%E7%99%BD%E6%B0%94%E7%90%83";
//		Request request=new Request(url);
//		request.setMethod(HttpConstant.Method.GET);
//		Spider.create(new XiamiProcessor(new SongTemplate()))
//			.addRequest(request)
//			.run();
//	}

	@Override
	public void run() {
		if(!this.url.equals("")){
			Spider.create(this)
					.addUrl(this.url)
					.run();
			this.url="";
		}else if(!this.commentUrl.equals("")){
			if (this.hotCommentUrl.equals("")) {
				Spider.create(this)
						.addUrl(this.commentUrl)
						.run();
			} else {
				Spider.create(this)
						.addUrl(this.commentUrl,this.hotCommentUrl)
						.thread(2)
						.run();
			}
			this.commentUrl="";
			this.hotCommentUrl="";
		}
	}
	
}
