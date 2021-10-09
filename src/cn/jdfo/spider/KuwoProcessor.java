package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Semaphore;

import cn.jdfo.domain.*;
import cn.jdfo.tool.Constant;
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
 * @author 肖飞  13319202082@163.com
 * @ClassName: KuwoProcessor
 * @Description: 酷我音乐的爬虫
 * @date 2017年12月15日 上午7:51:35
 */
@Component("kuwo")
@SessionScope
public class KuwoProcessor implements SongSpider {
    private SearchResult searchResult;
    private Semaphore searchMutex;//搜索互斥量
    private CommentResult commentResult;//评论获取结果
    private int commentFinished;//评论获取完成标识
    private static final String searchSongUrl = "http://search\\.kuwo\\.cn/r\\.s\\?ft=music.*";
    private static final String searchSonglistUrl = "http://search\\.kuwo\\.cn/r\\.s\\?ft=playlist.*";
    private static final String searchSingerUrl = "http://search\\.kuwo\\.cn/r\\.s\\?ft=artist.*";
    private static final String searchAlbumUrl = "http://search\\.kuwo\\.cn/r\\.s\\?ft=album.*";
    private static final String fetchCommentUrlReg = "http://comment\\.kuwo\\.cn/com.*";
    private final SongTemplate songTemplate;
    private Site site = Site.me()
            .setDomain("http://www.kuwo.cn")
            .setRetryTimes(1)
            .setSleepTime(1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
            .setCharset("utf-8")
            .setTimeOut(10000);
    private String searchUrl;
    private String[] fetchCommentUrl;

    @Autowired
    public KuwoProcessor(SongTemplate songTemplate) {
        super();
        this.searchMutex = new Semaphore(1);
        this.searchUrl = "";
        this.fetchCommentUrl = new String[2];
        this.commentFinished = 0;
        this.songTemplate = songTemplate;
    }

    @Override
    public String getPlatformName() {
        return "酷我";
    }

    @Override
    public void process(Page page) {
        Json json = page.getJson();
        if (page.getUrl().regex(this.searchSongUrl).match()) {
            List<String> names = json.jsonPath("$.abslist[*].NAME").all();
            List<String> oriid = json.jsonPath("$.abslist[*].MUSICRID").all();
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < oriid.size(); i++) {
                String id = oriid.get(i).substring(6);
                ids.add(id);
            }
            List<String> urls = new ArrayList<>();
            for (String id : ids) {
                String url = "http://www.kuwo.cn/yinyue/" + id + "?catalog=yueku2016";
                urls.add(url);
            }
            List<String> albumids = json.jsonPath("$.abslist[*].ALBUMID").all();
            List<String> albumUrls = new ArrayList<>();
            for (String id : albumids) {
                String albumurl = "http://www.kuwo.cn/album/" + id;
                albumUrls.add(albumurl);
            }
            List<String> albumNames = json.jsonPath("$.abslist[*].ALBUM").all();
            List<List<String>> singerlist = new ArrayList<>();
            List<String> artists = json.jsonPath("$.abslist[*].ARTIST").all();
            for (String artist : artists) {
                List<String> singers = new ArrayList<>();
                if (artist.contains("&")) {
                    singers = Arrays.asList(artist.split("&"));
                } else {
                    singers.add(artist);
                }
                singerlist.add(singers);
            }
            int total = Integer.parseInt(json.jsonPath("$.TOTAL").get());
            searchResult.getTotals().add(total);
            try {
                this.songTemplate.addSong(this.searchResult.getResult(), ids, names, urls, singerlist, albumUrls, albumNames, "酷我");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (page.getUrl().regex(this.searchSonglistUrl).match()) {//酷我的歌单没有author
            List<String> ids = json.jsonPath("$.abslist[*].playlistid").all();
            List<String> names = json.jsonPath("$.abslist[*].name").all();
            List<String> imgUrls = json.jsonPath("$.abslist[*].pic").all();
            List<String> imgUrlsWithParam = new ArrayList<>();
            for (String imgUrl : imgUrls) {
                if (imgUrl.contains("_700.jpg")) imgUrl = imgUrl.replace("_700.jpg", "_150.jpg");
                imgUrlsWithParam.add(imgUrl);
            }
            List<String> playCount = json.jsonPath("$.abslist[*].playcnt").all();
            List<String> songCount = json.jsonPath("$.abslist[*].songnum").all();
            List<Integer> collectCount = new ArrayList<>();
            List<String> urls = new ArrayList<>();
            List<String> authors = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                authors.add("未知作者");
            }
            for (int i = 0; i < playCount.size(); i++) {
                //经过各种计算，网络歌单的播放收藏比在300左右，因此可以大致确定收藏数量
                int cc = Integer.parseInt(playCount.get(i)) / 300;
                collectCount.add(cc);
            }
            for (String id : ids) {
                String url = "http://www.kuwo.cn/playlist/index?pid=" + id;
                urls.add(url);
            }
            this.songTemplate.addSonglist(this.searchResult.getResult(), urls, names, authors, imgUrlsWithParam, collectCount, "酷我");
        } else if (page.getUrl().regex(this.searchSingerUrl).match()) {
            List<String> ids = json.jsonPath("$.abslist[*].ARTISTID").all();
            List<String> names = json.jsonPath("$.abslist[*].ARTIST").all();
            List<String> PICPATHs = json.jsonPath("$.abslist[*].PICPATH").all();
            List<String> imgUrls = new ArrayList<>();
            for (String picpath : PICPATHs) {
                String imgUrl = "http://star.kuwo.cn/star/starheads/" + picpath;
                imgUrls.add(imgUrl);
            }
            songTemplate.addSinger(this.searchResult.getResult(), ids, names, imgUrls);
        } else if (page.getUrl().regex(this.searchAlbumUrl).match()) {
            List<String> albumIds = json.jsonPath("$.albumlist[*].albumid").all();
            List<String> urls = new ArrayList<>();
            for (String id : albumIds) {
                urls.add("http://www.kuwo.cn/album/" + id + "?catalog=yueku2016");
            }
            List<String> albumNames = json.jsonPath("$.albumlist[*].name").all();
            List<String> PICPATHS = json.jsonPath("$.albumlist[*].pic").all();
            List<String> imgUrls = new ArrayList<>();
            for (String picpath : PICPATHS) {
                String imgUrl = "http://star.kuwo.cn/star/albumcover/" + picpath;
                imgUrls.add(imgUrl);
            }
            //List<String> intro=json.jsonPath("$.albumlist[*].info").all();
            List<String> publishtime = json.jsonPath("$.albumlist[*].pub").all();
            List<String> singername = json.jsonPath("$.albumlist[*].artist").all();
            List<String> companys = json.jsonPath("$.albumlist[*].company").all();
            List<String> scores = json.jsonPath("$.albumlist[*].score").all();
            songTemplate.addAlbum(this.searchResult.getResult(), albumIds, urls, albumNames, imgUrls, publishtime, singername, new ArrayList<>(), scores, "酷我");
        } else if (page.getUrl().regex(fetchCommentUrlReg).match()) {
            JSONObject j = JSON.parseObject(json.toString());
            if (!j.containsKey("rows")) {
                this.commentFinished--;
                return;
            }
            String url = page.getUrl().get();
            List<String> likeNum = json.jsonPath("$.rows[*].like_num").all();
            List<Integer> like = new ArrayList<>();
            for (int i = 0; i < likeNum.size(); i++) {
                like.add(Integer.parseInt(likeNum.get(i)));
            }
            List<String> message = json.jsonPath("$.rows[*].msg").all();
            List<String> strtime = json.jsonPath("$.rows[*].time").all();
            List<Long> time = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String t : strtime) {
                Date date = new Date();
                try {
                    date = dateFormat.parse(t);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                time.add(date.getTime());
            }
            List<String> username_encoded = json.jsonPath("$.rows[*].u_name").all();//昵称都是urlencode编码过的
            List<String> username = new ArrayList<>();
            for (String anUsername_encoded : username_encoded) {
                String name = "";
                try {
                    name = URLDecoder.decode(anUsername_encoded, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                username.add(name);
            }
            List<String> userPic = json.jsonPath("$.rows[*].u_pic").all();
            SongComment[] bereplied = new SongComment[username.size()];
            JSONObject jsonObject = JSON.parseObject(json.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("rows");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (jsonObject.containsKey("reply")) {
                    JSONObject replay = object.getJSONObject("reply");
                    bereplied[i] = new SongComment();
                    bereplied[i].setContent(replay.getString("msg"));
                    try {
                        bereplied[i].setAuthor(URLDecoder.decode(replay.get("u_name").toString(), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (url.contains("get_comment")) {
                songTemplate.addComments(commentResult.getRecComments(), username, message, time, userPic, like, "酷我", bereplied);
                int total = Integer.parseInt(json.jsonPath("$.total").get());
                this.commentResult.getTotals().add(total);
                String oid = url.substring(url.indexOf("&sid=") + 5, url.indexOf("&page="));
                this.commentResult.getIdTotals().add(new Three<>("酷我", oid, total));
                this.commentResult.getIdOutcome().add(new Three<>("酷我", oid, message.size()));
                this.commentFinished--;
                return;
            } else if (url.contains("get_rec_comment")) {
                songTemplate.addComments(commentResult.getHotComments(), username, message, time, userPic, like, "酷我", bereplied);
                this.commentFinished--;
                return;
            }
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
        this.searchResult = result;
        switch (type) {
            case Constant.SEARCH_SONG:
                this.searchUrl = "http://search.kuwo.cn/r.s?ft=music&all=" + keyword + "&client=kt&pn=" + (page - 1) + "&rn=50&rformat=json&encoding=utf8";
                break;
            case Constant.SEARCH_SONGLIST:
                this.searchUrl = "http://search.kuwo.cn/r.s?ft=playlist&all=" + keyword + "&client=kt&pn=" + (page - 1) + "&rn=30&rformat=json&encoding=utf8";
                break;
            case Constant.SEARCH_SINGER:
                this.searchUrl = "http://search.kuwo.cn/r.s?ft=artist&all=" + keyword + "&client=kt&pn=" + (page - 1) + "&rn=20&rformat=json&encoding=utf8";
                break;
            case Constant.SEARCH_ALBUM:
                this.searchUrl = "http://search.kuwo.cn/r.s?ft=album&all=" + keyword + "&client=kt&pn=" + (page - 1) + "&rn=20&rformat=json&encoding=utf8";
                break;
        }
        new Thread(this).start();
    }

    @Override
    public void fetchComments(String outerId, int page, int type, CommentResult result) throws InterruptedException {
        if (this.commentFinished <= 0) this.commentResult = result;
        this.commentFinished += 2;
        while (this.fetchCommentUrl[1] != null) Thread.sleep(20);
        switch (type) {
            case Constant.SONG_COMMENT:
                this.fetchCommentUrl[0] = "http://comment.kuwo.cn/com.s?type=get_rec_comment&uid=0&prod=newWeb&digest=15&sid=" + outerId + "&page=" + page + "&rows=10&f=web";
                this.fetchCommentUrl[1] = "http://comment.kuwo.cn/com.s?type=get_comment&uid=0&prod=newWeb&digest=15&sid=" + outerId + "&page=" + page + "&rows=20&f=web";
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
//		String url="http://comment.kuwo.cn/com.s?type=get_comment&uid=0&prod=newWeb&digest=15&sid=15249349&page=12&rows=10&f=web";
//		Spider.create(new KuwoProcessor())
//			.addUrl(url)
//			.run();
//	}

    @Override
    public void run() {
        if (!this.searchUrl.equals("")) {
            Spider.create(this)
                    .addUrl(this.searchUrl)
                    .run();
            this.searchUrl = "";
        } else if (this.fetchCommentUrl[0] != null) {
            Spider.create(this)
                    .addUrl(this.fetchCommentUrl)
                    .thread(6)
                    .run();
            this.fetchCommentUrl[0] = null;
            this.fetchCommentUrl[1] = null;
        }
    }
}
