package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

import cn.jdfo.domain.CommentResult;
import cn.jdfo.domain.SearchResult;
import cn.jdfo.tool.Constant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Json;

@Component("kugou")
@SessionScope
public class KugouProcessor implements SongSpider {
	
	//酷狗API详见http://blog.csdn.net/gikieng/article/details/38750971
	//还有https://segmentfault.com/a/1190000010222913
	//酷狗好像没有歌曲详情页面，所以这个两个url都是歌曲文件

    private SearchResult searchResult;//保存搜索结果
	private Semaphore searchMutex;//搜索互斥量
	private CommentResult commentResult;
	private static final String searchSongUrl="http://mobilecdn\\.kugou\\.com/api/v3/search/song\\?.*";
	private static final String searchSonglistUrl="http://mobilecdn\\.kugou\\.com/api/v3/search/special\\?.*";
	private static final String searchSingerUrl="http://mobilecdn\\.kugou\\.com/api/v3/search/singer\\?.*";
    private static final String searchAlbumUrl="http://mobilecdn\\.kugou\\.com/api/v3/search/album\\?.*";
	private final SongTemplate songTemplate;
	private Site site=Site.me()
			.setDomain("http://www.kugou.com")
			.setRetryTimes(1)
			.setSleepTime(1000)
			.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
			.setCharset("utf-8")
			.setTimeOut(10000);
	private String url;

	@Autowired
	public KugouProcessor(SongTemplate songTemplate) {
		super();
		searchMutex=new Semaphore(1);
		this.songTemplate = songTemplate;
	}

	@Override
	public void process(Page page) {
		Json json=page.getJson();
		JSONObject jsonObject= JSON.parseObject(json.toString());
		if(page.getUrl().regex(searchSongUrl).match()){
			JSONObject object=jsonObject.getJSONObject("data");
		    if(object.getJSONArray("info").size()<1)return;
			List<String> names=json.jsonPath("$.data.info[*].songname").all();
			List<String> singers=json.jsonPath("$.data.info[*].singername").all();
			List<String> hashs=json.jsonPath("$.data.info[*].hash").all();
			List<String> urls=new ArrayList<>();
			for(String hash: hashs){
				String request="http://m.kugou.com/app/i/getSongInfo.php?hash="+hash+"&cmd=playInfo";
				urls.add(request);//这个URL是用来获取歌曲文件的
			}
			List<String> albumids=json.jsonPath("$.data.info[*].album_id").all();
			List<String> albumUrls=new ArrayList<>();
			for(String id:albumids){
				String url="http://www.kugou.com/yy/album/single/"+id+".html";
				albumUrls.add(url);
			}
			List<List<String>> singerlist=new ArrayList<>();
			for(String s:singers){
				String[] sl=s.split("、");
				List<String> ls=new ArrayList<>();
				ls.addAll(Arrays.asList(sl));
				singerlist.add(ls);
			}
			int total=Integer.parseInt(json.jsonPath("$.data.total").get());
			searchResult.getTotals().add(total);
			List<String> hash=json.jsonPath("$.data.info[*].hash").all();//hash应该就是歌曲的ID
			List<String> albumNames=json.jsonPath("$.data.info[*].album_name").all();
			try {
				this.songTemplate.addSong(this.searchResult.getResult(), hash, names, urls, singerlist, albumUrls, albumNames, "酷狗");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}else if(page.getUrl().regex(searchSonglistUrl).match()){
			JSONObject object=jsonObject.getJSONObject("data");
            if(object.getJSONArray("info").toString().length()<=2)return;
			List<String> ids=json.jsonPath("$.data.info[*].specialid").all();
			List<String> updateTime=json.jsonPath("$.data.info[*].publishtime").all();
			List<String> names=json.jsonPath("$.data.info[*].specialname").all();
            List<String> authors=json.jsonPath("$.data.info[*].nickname").all();
			List<String> imgUrls=json.jsonPath("$.data.info[*].imgurl").all();
			List<String> imgUrlsWithParam=new ArrayList<>();
			for(String imgUrl:imgUrls){
				imgUrl=imgUrl.replace("{size}", "150");
				imgUrlsWithParam.add(imgUrl);
			}
			List<String> collectCount=json.jsonPath("$.data.info[*].collectcount").all();
			List<String> playCount=json.jsonPath("$.data.info[*].playcount").all();
			List<String> songCount=json.jsonPath("$.data.info[*].songcount").all();
			List<Integer> collectCount_int=new ArrayList<>();
			List<String> urls=new ArrayList<>();
			for(int i=0;i<collectCount.size();i++){
				collectCount_int.add(Integer.parseInt(collectCount.get(i)));
			}
			for(String id:ids){
				String url="http://www.kugou.com/yy/special/single/"+id+".html";
				urls.add(url);
			}
			this.songTemplate.addSonglist(this.searchResult.getResult(), urls, names, authors, imgUrlsWithParam, collectCount_int,"酷狗");
		}else if(page.getUrl().regex(searchSingerUrl).match()){
			List<String> singerIds=json.jsonPath("$.data[*].singerid").all();
			List<String> singerNames=json.jsonPath("$.data[*].singername").all();
			List<String> imgUrls=new ArrayList<>();
			songTemplate.addSinger(this.searchResult.getResult(), singerIds, singerNames, imgUrls);
		}else if(page.getUrl().regex(searchAlbumUrl).match()){
			JSONObject object=jsonObject.getJSONObject("data");
            if(object.getJSONArray("info").toString().length()<=2)return;
            List<String> albumIds=json.jsonPath("$.data.info[*].albumid").all();
            List<String> urls=new ArrayList<>();
            for(String id: albumIds){
            	String url="http://www.kugou.com/yy/album/single/"+id+".html";
            	urls.add(url);
            }
            List<String> albumNames=json.jsonPath("$.data.info[*].albumname").all();
            List<String> imgUrls=json.jsonPath("$.data.info[*].imgurl").all();
			List<String> imgUrlsWithParam=new ArrayList<>();
			for(String imgUrl:imgUrls){
				imgUrl=imgUrl.replace("{size}", "150");
				imgUrlsWithParam.add(imgUrl);
			}
            //List<String> intro=json.jsonPath("$.data.info[*].intro").all();
            List<String> publishtime=json.jsonPath("$.data.info[*].publishtime").all();
            List<String> singername=json.jsonPath("$.data.info[*].singername").all();
            List<String> songcount=json.jsonPath("$.data.info[*].songcount").all();
            songTemplate.addAlbum(this.searchResult.getResult(),albumIds,urls,albumNames,imgUrlsWithParam,publishtime,singername,songcount,new ArrayList<>(),"酷狗");
        }
        this.searchMutex.release();
	}

	@Override
	public String getPlatformName() {
		return "酷狗";
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
				this.url="http://mobilecdn.kugou.com/api/v3/search/song?format=json&keyword="+keyword+"&page="+page+"&pagesize=50&showtype=1";
				break;
			case Constant.SEARCH_SONGLIST:
				this.url="http://mobilecdn.kugou.com/api/v3/search/special?format=json&keyword="+keyword+"&page="+page+"&pagesize=30&showtype=1";
				break;
			case Constant.SEARCH_SINGER:
				this.url="http://mobilecdn.kugou.com/api/v3/search/singer?format=json&keyword="+keyword+"&page="+page+"&pagesize=20&showtype=1";
				break;
			case Constant.SEARCH_ALBUM:
				this.url="http://mobilecdn.kugou.com/api/v3/search/album?format=json&keyword="+keyword+"&page="+page+"&pagesize=20&showtype=1";
				break;
		}
		new Thread(this).start();
    }

	@Override
	public void fetchComments(String outerId, int page, int type, CommentResult result) {
	}

	@Override
	public boolean searchFinished() {
		return this.searchMutex.availablePermits()>0;
	}

	@Override
	public boolean gettingCommentsFinished() {
		return true;
	}

//	public static void main(String[] args){
//		String url="http://mobilecdn.kugou.com/api/v3/search/special?format=json&keyword=古风&page=1&pagesize=30&showtype=1";
//		Spider.create(new KugouProcessor())
//			.addUrl(url)
//			.run();
//	}

	@Override
	public void run() {
		Spider.create(this)
				.addUrl(this.url)
				.run();
	}


}
