package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

import cn.jdfo.domain.*;
import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Three;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.utils.HttpConstant;

//网易云音乐API相关信息：详见http://moonlib.com/606.html

@Component("netease")
@SessionScope
public class NetEaseProcessor implements SongSpider {

    private SearchResult searchResult;
    private CommentResult commentResult;
    private Semaphore searchMutex;//搜索互斥量
    private int commentFinished;//评论获取完成标识
    private static final String searchSongUrl = "http://music\\.163\\.com/api/search/pc\\?type=1.*";
    private static final String searchSonglistUrl = "http://music\\.163\\.com/api/search/pc\\?type=1000.*";
    private static final String searchSingerUrl = "http://music\\.163\\.com/api/search/pc\\?type=100.*";
    private static final String searchAlbumUrl = "http://music\\.163\\.com/api/search/pc\\?type=10.*";
    private static final String fetchCommentUrl = "http://music\\.163\\.com/api/v1/resource/comments/.*";
    private Site site = Site.me()
            .setDomain("http://music.163.com")
            .setRetryTimes(1)
            .setSleepTime(1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
            .setCharset("utf-8")
            .setTimeOut(10000);
    private String searchUrl;
    private String commentUrl;
    private final SongTemplate songTemplate;

    @Autowired
    public NetEaseProcessor(SongTemplate songTemplate) {
        super();
        this.searchUrl = "";
        this.commentUrl = "";
        this.searchMutex = new Semaphore(1);
        this.commentFinished = 0;
        this.songTemplate = songTemplate;
    }

    @Override
    public String getPlatformName() {
        return "网易";
    }

    @Override
    public Site getSite() {
        return this.site;
    }

    @Override
    public void process(Page page) {
        Json json = page.getJson();
        JSONObject jsonObject = JSON.parseObject(json.toString());
        JSONObject jo2 = new JSONObject();
        if (jsonObject.containsKey("result")) {
            jo2 = jsonObject.getJSONObject("result");
        }
        if (page.getUrl().regex(searchSongUrl).match() && jo2.containsKey("songs")) {
            List<String> names = json.jsonPath("$.result.songs[*].name").all();
            List<String> songids = json.jsonPath("$.result.songs[*].id").all();
            List<String> urls = new ArrayList<>();
            for (String id : songids) {
                String url = "http://music.163.com/song?id=" + id;
                urls.add(url);
            }
            List<String> albumids = json.jsonPath("$.result.songs[*].album.id").all();
            List<String> albumUrls = new ArrayList<>();
            for (String id : albumids) {
                String url = "http://music.163.com/album?id=" + id;
                albumUrls.add(url);
            }
            List<String> albumNames = json.jsonPath("$.result.songs[*].album.name").all();
            List<List<String>> singerlist = new ArrayList<>();
            for (int i = 0; i < names.size(); i++) {
                List<String> singers = json.jsonPath("$.result.songs[" + i + "].artists[*].name").all();
                singerlist.add(singers);
            }
            int total = Integer.parseInt(json.jsonPath("$.result.songCount").get());
            this.searchResult.getTotals().add(total);
            try {
                this.songTemplate.addSong(this.searchResult.getResult(), songids, names, urls, singerlist, albumUrls, albumNames, "网易");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (page.getUrl().regex(searchSonglistUrl).match() && jo2.containsKey("playlists")) {//搜索歌单
            List<String> ids = json.jsonPath("$.result.playlists[*].id").all();
            List<String> names = json.jsonPath("$.result.playlists[*].name").all();
            List<String> imgUrls = json.jsonPath("$.result.playlists[*].coverImgUrl").all();
            List<String> imgUrlsWithParam = new ArrayList<>();
            for (String imgUrl : imgUrls) {
                imgUrl += "?param=150y150";
                imgUrlsWithParam.add(imgUrl);
            }
            List<String> authors = json.jsonPath("$.result.playlists[*].creator.nickname").all();
            List<String> bookCount = json.jsonPath("$.result.playlists[*].bookCount").all();//收藏总数
            List<String> playCount = json.jsonPath("$.result.playlists[*].playCount").all();//播放总数
            List<String> authorIds = json.jsonPath("$.result.playlists[*].userId").all();
            List<Integer> collectCount = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            for (String s : bookCount) {
                collectCount.add(Integer.parseInt(s));
            }
            for (String id : ids) {
                String url = "http://music.163.com/#/playlist?id=" + id;
                urls.add(url);
            }
            this.songTemplate.addSonglist(this.searchResult.getResult(), urls, names, authors, imgUrlsWithParam, collectCount, "网易");
        } else if (page.getUrl().regex(searchSingerUrl).match() && jo2.containsKey("artists")) {
            List<String> ids = json.jsonPath("$.result.artists[*].id").all();
            List<String> names = json.jsonPath("$.result.artists[*].name").all();
            List<String> imgUrls = json.jsonPath("$.result.artists[*].img1v1Url").all();
            songTemplate.addSinger(this.searchResult.getResult(), ids, names, imgUrls);
        } else if (page.getUrl().regex(searchAlbumUrl).match() && jo2.containsKey("albums")) {
            List<String> albumIds = json.jsonPath("$.result.albums[*].id").all();
            List<String> urls = new ArrayList<>();
            for (String id : albumIds) {
                urls.add("http://music.163.com/album?id=" + id);
            }
            List<String> albumNames = json.jsonPath("$.result.albums[*].name").all();
            List<String> imgUrls = json.jsonPath("$.result.albums[*].picUrl").all();
            //List<String> intro=json.jsonPath("$.result.albums[*].briefDesc").all();
            List<String> timestamp = json.jsonPath("$.result.albums[*].publishTime").all();//注意：这是时间戳
            List<String> publishtime = new ArrayList<>();
            for (String ts : timestamp) {
                long l = Long.parseLong(ts);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date d = new Date(l);
                publishtime.add(dateFormat.format(d));
            }
            List<String> singername = json.jsonPath("$.result.albums[*].artist.name").all();
            List<String> songcount = json.jsonPath("$.result.albums[*].size").all();
            songTemplate.addAlbum(this.searchResult.getResult(), albumIds, urls, albumNames, imgUrls, publishtime, singername, songcount, new ArrayList<>(), "网易");
        } else if (page.getUrl().regex(fetchCommentUrl).match()) {
            JSONObject object1 = JSONObject.parseObject(json.toString());
            for (int a = 0; a <= 1; a++) {
                String b = a == 1 ? "hotC" : "c";
                if (a == 1 && !object1.containsKey("hotComments")) break;
                List<String> content = json.jsonPath("$." + b + "omments[*].content").all();
                List<String> timestamp = json.jsonPath("$." + b + "omments[*].time").all();
                List<Long> time = new ArrayList<>();
                for (String ts : timestamp) {
                    long l = Long.parseLong(ts);
                    time.add(l);
                }
                List<String> username = json.jsonPath("$." + b + "omments[*].user.nickname").all();
                List<String> userimg = json.jsonPath("$." + b + "omments[*].user.avatarUrl").all();
                List<Integer> likeCount = new ArrayList<>();
                List<String> strLike = json.jsonPath("$." + b + "omments[*].likedCount").all();
                for (String strl : strLike) {
                    likeCount.add(Integer.parseInt(strl));
                }
                SongComment[] bereplied = new SongComment[content.size()];
                for (int i = 0; i < username.size(); i++) {
                    if (!object1.getJSONArray(b + "omments").getJSONObject(i).getJSONArray("beReplied").isEmpty()) {
                        JSONObject reply = object1.getJSONArray(b + "omments").getJSONObject(i).getJSONArray("beReplied").getJSONObject(0);
                        bereplied[i] = new SongComment();
                        bereplied[i].setContent(reply.getString("content"));
                        bereplied[i].setAuthor(reply.getJSONObject("user").getString("nickname"));
                    }
                }
                if (a == 0)
                    songTemplate.addComments(this.commentResult.getRecComments(), username, content, time, userimg, likeCount, "网易", bereplied);
                if (a == 1)
                    songTemplate.addComments(this.commentResult.getHotComments(), username, content, time, userimg, likeCount, "网易", bereplied);
            }
            int total = Integer.parseInt(json.jsonPath("$.total").get());
            String oid = page.getUrl().get().substring(page.getUrl().get().indexOf("/R_SO_4_") + 8, page.getUrl().get().indexOf("?offset="));
            commentResult.getIdTotals().add(new Three<>("网易", oid, total));
            commentResult.getIdOutcome().add(new Three<>("网易", oid, 20));
            this.commentResult.getTotals().add(total);
            this.commentFinished--;
            return;
        }
        this.searchMutex.release();
    }

    @Override
    public void search(String keyword, int page, int type, SearchResult result) throws InterruptedException {
        this.searchMutex.acquire();
        this.searchResult = result;
        switch (type) {
            case Constant.SEARCH_SONG:
                this.searchUrl = "http://music.163.com/api/search/pc?type=1&s=" + keyword + "&offset=" + ((page - 1) * 50) + "&limit=50";
                break;
            case Constant.SEARCH_SONGLIST:
                this.searchUrl = "http://music.163.com/api/search/pc?type=1000&s=" + keyword + "&offset=" + ((page - 1) * 30) + "&limit=30";
                break;
            case Constant.SEARCH_SINGER:
                this.searchUrl = "http://music.163.com/api/search/pc?type=100&s=" + keyword + "&offset=" + ((page - 1) * 20) + "&limit=20";
                break;
            case Constant.SEARCH_ALBUM:
                this.searchUrl = "http://music.163.com/api/search/pc?type=10&s=" + keyword + "&offset=" + ((page - 1) * 20) + "&limit=20";
                break;
        }
        new Thread(this).start();
    }

    @Override
    public void fetchComments(String id, int page, int type, CommentResult result) throws InterruptedException {
        if (this.commentFinished <= 0) this.commentResult = result;
        this.commentFinished++;
        while (!this.commentUrl.equals("")) Thread.sleep(20);
        switch (type) {
            case Constant.SONG_COMMENT:
                this.commentUrl = "http://music.163.com/api/v1/resource/comments/R_SO_4_" + id + "?offset=" + (page - 1) * 20 + "&limit=20";
                break;
        }

        new Thread(this).start();
    }

    @Override
    public boolean searchFinished() {
        return this.searchMutex.availablePermits() > 0;
    }

    @Override
    public boolean gettingCommentsFinished() {
        return this.commentFinished <= 0;
    }
//
//	public static void main(String[] args){
//		String url="http://music.163.com/api/v1/resource/comments/R_SO_4_539619798?offset=20&limit=20";
//		Request request=new Request(url);
//		request.setMethod(HttpConstant.Method.GET);
//		Spider.create(new NetEaseProcessor())
//			.addRequest(request)
//			.run();
//	}

    @Override
    public void run() {
        if (!this.searchUrl.equals("")) {
            Request request = new Request(this.searchUrl);
            request.setMethod(HttpConstant.Method.POST);
            Spider.create(this)
                    .addRequest(request)
                    .run();
            this.searchUrl = "";
        } else if (!this.commentUrl.equals("")) {
            Request request = new Request(this.commentUrl);
            request.setMethod(HttpConstant.Method.GET);
            Spider.create(this)
                    .addRequest(request)
                    .run();
            this.commentUrl = "";
        }
    }

}
