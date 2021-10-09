package cn.jdfo.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import cn.jdfo.dao.MyDao;
import cn.jdfo.domain.Rank;
import cn.jdfo.domain.Singer;
import cn.jdfo.domain.Song;
import cn.jdfo.spider.*;
import cn.jdfo.tool.BeanGetter;
import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Pair;
import cn.jdfo.tool.Three;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

/**
 *
 */
@Service
public class TimerService {
    private static final long updateRanksPeriod=24*60*60*1000;//更新排行榜：每6小时一次
    public TimerService(){
        Timer timer = new Timer();
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MySpider.updateRanks();
            }
        },updateRanksPeriod/24  ,updateRanksPeriod);
    }
	private static class MySpider implements PageProcessor{
        private static MyDao myDao;
        static {
            BeanGetter beanGetter=BeanGetter.getBeanGetter();
            myDao=beanGetter.getBean("myDao",MyDao.class);
        }
	    private List<Three<Song,Integer,String>> hot=new ArrayList<>();//分别是歌曲、排名、平台
	    private List<Three<Song,Integer,String>> news=new ArrayList<>();
	    private List<Three<Song,Integer,String>> bs=new ArrayList<>();
	    private Map<String,Integer> hotCount=new HashMap<>();
	    private Map<String,Integer> newCount=new HashMap<>();
	    private static int m=0;
		private Site site=Site.me().setRetryTimes(1)
				.setSleepTime(1000)
				.setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36")
				.setCharset("utf-8")
				.setTimeOut(10000);

		@Override
		public void process(Page page) {//依次解析各个网站的页面
            String url=page.getUrl().get();
            List<Three<Song,Integer,String>> list=new ArrayList<>();
            if(url.contains("kugou")){
                int num=Integer.parseInt(url.substring(url.indexOf("page=")+5,url.indexOf("&",url.indexOf("page="))));
                Json json=page.getJson();
                JSONObject jsonObject= JSON.parseObject(json.toString());
                JSONArray array=jsonObject.getJSONObject("songs").getJSONArray("list");
                for(int i=0;i<array.size();i++){
                    JSONObject object=array.getJSONObject(i);
                    String str_song=object.getString("filename");
                    int index=str_song.indexOf(" - ");
                    String[] str_singer=str_song.substring(0,index).split("、");
                    String name=str_song.substring(index+3);
                    Song song=new Song();
                    song.setName(name);
                    Set<Singer> singers=new HashSet<>();
                    for(String s: str_singer){
                        Singer singer=new Singer();
                        singer.setName(s);
                        singers.add(singer);
                    }
                    song.setSingers(singers);
                    list.add(new Three<>(song,i+1+(num-1)*30,"酷狗"));
                }
            }else if(url.contains("kuwo")){
                Html html=page.getHtml();
                List<String> names=html.xpath("/html/body/ul/li/div[2]/a/text()").all();
                List<String> singers=html.xpath("/html/body/ul/li/div[3]/a/text()").all();
                for(int i=0;i<names.size();i++){
                    String name=names.get(i);
                    Song song=new Song();
                    song.setName(name);
                    String[] str_singer=singers.get(i).split("&");
                    Set<Singer> singerset=new HashSet<>();
                    for(String s: str_singer){
                        Singer singer=new Singer();
                        singer.setName(s);
                        singerset.add(singer);
                    }
                    song.setSingers(singerset);
                    list.add(new Three<>(song,i+1,"酷我"));
                }
            }else if(url.contains("musicapi")){
                Json json=page.getJson();
                JSONObject object=JSON.parseObject(json.toString());
                JSONArray array=object.getJSONObject("result").getJSONArray("tracks");
                for(int i=0;i<array.size();i++){
                    JSONObject jsonObject=array.getJSONObject(i);
                    String name=jsonObject.getString("name");
                    Song song=new Song();
                    song.setName(name);
                    Set<Singer> singers=new HashSet<>();
                    JSONArray jsonArray=jsonObject.getJSONArray("artists");
                    for(int j=0;j<jsonArray.size();j++){
                        String singername=jsonArray.getJSONObject(j).getString("name");
                        Singer singer=new Singer();
                        singer.setName(singername);
                        singers.add(singer);
                    }
                    song.setSingers(singers);
                    list.add(new Three<>(song,i+1,"网易"));
                }
            }else if(url.contains("c.y.qq.com")){
                Json json=page.getJson();
                JSONObject jsonObject=JSON.parseObject(json.toString());
                JSONArray jsonArray=jsonObject.getJSONArray("songlist");
                for(int i=0;i<jsonArray.size();i++){
                    JSONObject object=jsonArray.getJSONObject(i).getJSONObject("data");
                    String name=object.getString("songname");
                    Song song=new Song();
                    song.setName(name);
                    JSONArray array=object.getJSONArray("singer");
                    Set<Singer> singerSet=new HashSet<>();
                    for(int j=0;j<array.size();j++){
                        String singername=array.getJSONObject(j).getString("name");
                        Singer singer=new Singer();
                        singer.setName(singername);
                        singerSet.add(singer);
                    }
                    song.setSingers(singerSet);
                    list.add(new Three<>(song,i+1,"QQ"));
                }
            }else if(url.contains("xiami")){
                Html html=page.getHtml();
                List<String> names=html.xpath("/html/body/div/div[2]/p[1]/strong/a/text()").all();
                for(int i=0;i<names.size();i++){
                    String name=names.get(i);
                    Song song=new Song();
                    song.setName(name);
                    Set<Singer> singers=new HashSet<>();
                    List<String> str_singer=html.xpath("/html/body/div["+(2*i+1)+"]/div[2]/p[2]/a/text()").all();
                    for(int j=0;j<str_singer.size();j++){
                        String s=str_singer.get(j);
                        if(s.contains("(")){
                            str_singer.set(j,s.substring(0,s.indexOf("(")));
                        }
                        Singer singer=new Singer();
                        if(str_singer.get(j).length()>0){
                            singer.setName(str_singer.get(j));
                            singers.add(singer);
                        }
                    }
                    song.setSingers(singers);
                    list.add(new Three<>(song,i+1,"虾米"));
                }
            }
            for(Three<Song,Integer,String> three: list){
                StringBuilder builder=new StringBuilder();
                Singer[] singers=three.getA().getSingers().toArray(new Singer[three.getA().getSingers().size()]);
                Arrays.sort(singers);
                for(Singer singer: singers){
                    try {
                        singer.setKey(URLEncoder.encode(singer.getName(),"utf-8"));
                        if(!myDao.hasObject(singer.getKey(),Singer.class))singer.setId(myDao.save(singer));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    builder.append(singer.getName());
                }
                try {
                    three.getA().setKey(URLEncoder.encode(three.getA().getName(),"utf-8")+URLEncoder.encode(builder.toString(),"utf-8"));
                    if(!myDao.hasObject(three.getA().getKey(),Song.class))myDao.save(three.getA());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if(url.contains("mtype=hot")){
                hot.addAll(list);
                if(list.get(0).getC().equals("酷狗")){
                    hotCount.put("酷狗",list.size()+(hotCount.get("酷狗")==null?0:hotCount.get("酷狗")));
                }else hotCount.put(list.get(0).getC(),list.size());
            }
            if(url.contains("mtype=new")){
                news.addAll(list);
                if(list.get(0).getC().equals("酷狗")){
                    newCount.put("酷狗",list.size()+(newCount.get("酷狗")==null?0:newCount.get("酷狗")));
                }else newCount.put(list.get(0).getC(),list.size());
            }
            if(url.contains("mtype=bs"))bs.addAll(list);
            m--;
            //所有数据取完，开始综合排名
            //权重：QQ:35%; 酷狗：20%; 网易：20%; 酷我：15%; 虾米：10%
            if(m==0){
                Map<String,Float> weight=new HashMap<>();
                weight.put("QQ",35.0f);
                weight.put("酷狗",20.0f);
                weight.put("网易",20.0f);
                weight.put("酷我",15.0f);
                weight.put("虾米",10.0f);
                Map<String,Pair<Song,Float>> hotRank=new HashMap<>();//热歌分数
                for(Three<Song,Integer,String> three: hot){
                    float score=(float)hotCount.get(three.getC())/(float)three.getB()*weight.get(three.getC())*10;
                    if(hotRank.containsKey(three.getA().getKey())){
                        hotRank.get(three.getA().getKey()).setB(hotRank.get(three.getA().getKey()).getB()+score);
                    }else hotRank.put(three.getA().getKey(),new Pair<>(three.getA(),score));
                }
                List<Pair<Song, Float>> list1 = new ArrayList<>(hotRank.values());
                list1.sort((o1, o2) -> new Float(o2.getB()*100).intValue()-new Float(o1.getB()*100).intValue());
                int hot_max_issue=0;
                if(myDao.hasObject(Collections.singletonList("type"),Collections.singletonList(1),Rank.class))
                    hot_max_issue=myDao.selectSingle("select max(issue) from Rank where type=1",Integer.class);
                for(int i=0,len=list1.size();i<len;i++){
                    Pair<Song,Float> pair=list1.get(i);
                    Rank rank=new Rank(pair.getA().getKey(),pair.getB().intValue(),1,new Timestamp(new Date().getTime()),i+1,hot_max_issue+1);
                    myDao.save(rank);
                }
                for(Pair<Song,Float> pair: list1){
                    //存完排名后，还要在各个平台搜索一下这些歌曲，然后把所有相关的信息存进去
                    //在执行搜索之前，需要手动创造一个songSpider列表
                    SearchRunner runner=new SearchRunner(pair.getA().getName());
                    new Thread(runner).start();
                    try {
                        //服务器上最大的mysql连接数是151，本机windows上的最大连接数也是151
                        Thread.sleep(1000*10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
		}

		@Override
		public Site getSite() {
			return site;
		}
		static void updateRanks(){
            Set<String> urls=new HashSet<>();
			//酷狗热歌榜
            for(int i=1;i<=7;i++){
                urls.add("http://m.kugou.com/rank/info/8888?json=true&page="+i+"&mtype=hot");
            }
            //酷狗华语新歌榜
            for(int i=1;i<=2;i++){
                urls.add("http://m.kugou.com/rank/info/31308?json=true&page="+i+"&mtype=new");
            }
            //酷狗飙升榜
            for(int i=1;i<=2;i++){
                urls.add("http://m.kugou.com/rank/info/6666?json=true&page="+i+"&mtype=bs");
            }
//            //酷狗欧美新歌榜
//            urls.add("http://m.kugou.com/rank/info/31310?json=true");
//            //酷狗韩国新歌榜
//            urls.add("http://m.kugou.com/rank/info/31311?json=true");
//            //酷狗日本新歌榜
//            for(int i=1;i<=3;i++){
//                urls.add("http://m.kugou.com/rank/info/31312?json=true&page="+i);
//            }
//            //酷狗粤语新歌榜
//            for(int i=1;i<=2;i++){
//                urls.add("http://m.kugou.com/rank/info/31313?json=true&page="+i);
//            }
            urls.add("http://www.kuwo.cn/bang/content?name=酷我华语榜&mtype=hot");
            urls.add("http://www.kuwo.cn/bang/content?name=酷我首发榜&mtype=new");//酷我新歌榜
            urls.add("http://www.kuwo.cn/bang/content?name=酷我飙升榜&mtype=bs");
            urls.add("http://musicapi.leanapp.cn/top/list?idx=0&mtype=new");//网易云新歌榜
            urls.add("http://musicapi.leanapp.cn/top/list?idx=1&mtype=hot");//网易云热歌榜
            urls.add("http://musicapi.leanapp.cn/top/list?idx=3&mtype=bs");//网易云飙升榜
            urls.add("https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?topid=26&mtype=hot");//qq音乐热歌榜
            urls.add("https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?topid=27&mtype=new");//QQ音乐新歌榜
            urls.add("https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?topid=4&mtype=bs");//qq音乐飙升榜
            urls.add("http://www.xiami.com/chart/data?c=102&mtype=new");//虾米新歌榜
            urls.add("http://www.xiami.com/chart/data?c=103&mtype=hot");//虾米热歌榜
            m+=urls.size();
            Spider.create(new MySpider())
                    .addUrl(urls.toArray(new String[urls.size()]))
                    .run();
		}

	}

	private static class SearchRunner implements Runnable{
        private String keyword;
        private static SongService songService;
        static {
            BeanGetter beanGetter=BeanGetter.getBeanGetter();
            songService=beanGetter.getBean("songService",SongService.class);
        }
        SearchRunner(String keyword) {
            this.keyword=keyword;
        }

        @Override
        public void run() {
            SongTemplate songTemplate=new SongTemplate();
            List<SongSpider> songSpiders=new ArrayList<>();
            songSpiders.add(new KugouProcessor(songTemplate));
            songSpiders.add(new KuwoProcessor(songTemplate));
            songSpiders.add(new NetEaseProcessor(songTemplate));
            songSpiders.add(new QQProcessor(songTemplate));
            songSpiders.add(new XiamiProcessor(songTemplate));
            try {
                songService.search(keyword,1,Constant.SEARCH_SONG,songSpiders);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
