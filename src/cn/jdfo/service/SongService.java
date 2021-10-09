package cn.jdfo.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import cn.jdfo.dao.MyDao;
import cn.jdfo.tool.BeanGetter;
import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Pair;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jdfo.domain.*;
import cn.jdfo.spider.SongSpider;

@Service
public class SongService{
	
	private final MyDao myDao;
	private final BeanGetter beanGetter;

	@Autowired
	public SongService( MyDao myDao, BeanGetter beanGetter) {
		this.myDao = myDao;
		this.beanGetter = beanGetter;
	}

	public boolean collectSong(int songlistId, String songUnikey) throws UnsupportedEncodingException {
		if(!myDao.hasObject(URLEncoder.encode(songUnikey,"utf-8"),Song.class))return false;
		int songId=myDao.getIdByKey(URLEncoder.encode(songUnikey,"utf-8"),Song.class);
		if(!myDao.hasRelation(songlistId,songId,Songlist.class,Song.class)){
			SonglistSong songlistSong=new SonglistSong();
			songlistSong.setSongId(songId);
			songlistSong.setSonglistId(songlistId);
			songlistSong.setId(myDao.save(songlistSong));
			Songlist songlist=myDao.get(Songlist.class,songlistId);
			songlist.setUpdateTime(new Timestamp(new Date().getTime()));
			songlist.setSongCount(songlist.getSongCount()+1);
			myDao.update(songlist);
			return true;
		}else return false;
	}

	public SearchResult search(String keyword, int page, int type,List<SongSpider> songSpiders) throws InterruptedException {
		List resultList=new ArrayList<>();
		SearchResult result=new SearchResult();
		result.setTotals(new ArrayList<>());
		result.setResult(resultList);
		result.setType(type);
		result.setPage(page);
		if(songSpiders==null){
			songSpiders=new ArrayList<>();
			beanGetter.getSpiders(songSpiders);//获取当前会话的Spiders
		}
		for(SongSpider spider:songSpiders){
			spider.search(keyword,page,type,result);
		}
		for(int i=0;i<songSpiders.size();){
			boolean finished=songSpiders.get(i).searchFinished();
			if(!finished){
				Thread.sleep(50);
			}else{
				i++;
			}
		}
		if(resultList.isEmpty())return null;
		else {
			for(int i: result.getTotals()){
				result.setTotal(result.getTotal()+i);
			}
			if(resultList.get(0) instanceof Song){
				List<Song> songList=new ArrayList<>();
				for(Object o: resultList){
					songList.add((Song)o);
				}
				Map<String,Song> songMap=new LinkedHashMap<>();
				for(Song song:songList){
					Album theAlbum=new Album();
					if(!song.getAlbums().isEmpty()){
						boolean b=false;
						for(Album album: song.getAlbums()){
							if(album.getName().trim().equals(""))b=true;
							else {
								theAlbum=album;
							}
						}
						if(b)song.getAlbums().clear();
					}
					if(songMap.containsKey(song.getKey())){
						Song song1=songMap.get(song.getKey());
						if(!song.getSongSources().isEmpty()) song1.getSongSources().addAll(song.getSongSources());
						if(!song.getSongOuterids().isEmpty()) song1.getSongOuterids().addAll(song.getSongOuterids());
						if(song.getAlbums().isEmpty())continue;
						Set<String> unikeySet=new HashSet<>();
						for(Album album: song1.getAlbums()){
							unikeySet.add(album.getKey());
							if(theAlbum.getKey().equals(album.getKey())){
								album.getSources().addAll(theAlbum.getSources());
							}
						}
						if(!unikeySet.contains(theAlbum.getKey())){
							song1.getAlbums().add(theAlbum);
						}
					}else{
						songMap.put(song.getKey(), song);
					}
				}
				songList.clear();
				songList.addAll(songMap.values());
				result.setResult(songList);
			}else if(resultList.get(0) instanceof Singer){
				List<Singer> singerList=new ArrayList<>();
				for(Object o: resultList){
					singerList.add((Singer)o);
				}
				Map<String,Singer> singerMap=new LinkedHashMap<>();
				for(Singer singer:singerList){
					if(singerMap.containsKey(singer.getName())){
						result.setTotal(result.getTotal()-1);
						Singer singer1=singerMap.get(singer.getName());
						if(singer.getOuterIds().size()>0)
							singer1.getOuterIds().addAll(singer.getOuterIds());
						if(singer1.getImg().length()<5)
							singer1.setImg(singer.getImg());
					}else {
						singerMap.put(singer.getName(),singer);
					}
				}
				singerList.clear();
				singerList.addAll(singerMap.values());
				result.setResult(singerList);
			}else if(resultList.get(0) instanceof Songlist){
				List<Songlist> songListList=new ArrayList<>();
				for(Object o: resultList){
					songListList.add((Songlist)o);
				}
				Collections.sort(songListList);
				result.setResult(songListList);
			}else if(resultList.get(0) instanceof Album){
				List<Album> albumList=new ArrayList<>();
				for(Object o: resultList){
					albumList.add((Album)o);
				}
				int count=albumList.size();
				int left=count;
				Map<String,Integer> keyMap=new HashMap<>();
				for(int i=0;i<count;i++){
					if(keyMap.containsKey(albumList.get(i).getKey())){
						result.setTotal(result.getTotal()-1);
						Album album=albumList.get(i);
						int index=keyMap.get(album.getKey());
						left--;
						Album album1=albumList.get(index);
						album1.getOuterids().addAll(album.getOuterids());
						album1.getSources().addAll(album.getSources());
//						album1.getImgUrls().add(album.getImgUrls().get(0));图片暂且保留一张
						if(album1.getScore()<album.getScore())album1.setScore(album.getScore());
						if(album1.getSongCount()==null)album1.setSongCount(album.getSongCount());
						else if(album.getSongCount()!=null&&album1.getSongCount()<album.getSongCount())album1.setSongCount(album.getSongCount());
					}else {
						keyMap.put(albumList.get(i).getKey(),i);
					}
				}
				result.setResult(albumList.subList(0,left));
			}
		}
		//新开一个线程来存储搜索到的东西
		new Thread(new StorageService(result)).start();
		return result;
	}

	public CommentResult fetchComment(String uniKey, List<Pair<String,String>> idList, int page, int type) throws InterruptedException {
		CommentResult commentResult=new CommentResult();
		commentResult.setRecComments(new ArrayList<>());
		commentResult.setHotComments(new ArrayList<>());
		commentResult.setTotals(new ArrayList<>());
		commentResult.setIdTotals(new ArrayList<>());
		commentResult.setIdOutcome(new ArrayList<>());
		List<SongSpider> spiders=new ArrayList<>();
		beanGetter.getSpiders(spiders);//获取当前会话的Spiders
		Set<String> idSet=new HashSet<>();
		for(SongSpider spider: spiders){
			String pname=spider.getPlatformName();
			for(Pair<String,String> pair: idList){
				if(pair.getA().equals(pname)&&!idSet.contains(pair.getB())){
					spider.fetchComments(pair.getB(),page,type,commentResult);
					idSet.add(pair.getB());
				}
			}
		}
		for(int i=0,j=0;i<spiders.size();){
			if(!spiders.get(i).gettingCommentsFinished()){
				Thread.sleep(50);
				if(++j>60)i++;
			}else{
				i++;
			}
		}
		for(int i:commentResult.getTotals()){
			commentResult.setCount(commentResult.getCount()+i);
		}
		commentResult.setHotCount(commentResult.getHotComments().size());
		switch (type){
			case Constant.SONG_COMMENT:
				int id=myDao.getIdByKey(uniKey,Song.class);
				for(SongComment comment: commentResult.getRecComments())comment.setSongId(id);
				for(SongComment comment: commentResult.getHotComments())comment.setSongId(id);
				break;
			case Constant.SONGLIST_COMMENT:
				break;
		}
		//新开一个线程来存储获取到的评论
		new Thread(new StorageService(commentResult)).start();
		Collections.sort(commentResult.getHotComments());//根据赞数对热评排序
		commentResult.getRecComments().sort((comment1, comment2) -> (int) (comment2.getTime().getTime() / 1000) - (int) (comment1.getTime().getTime() / 1000));//根据时间对最新评论排序
		return commentResult;
	}

	public List<Pair<Song,Rank>> getRank(int type, int limit, int offset) throws UnsupportedEncodingException {
		if(type<1||type>4)return null;
		List<Pair<Song,Rank>> result=new ArrayList<>();
		int hot_max_issue=myDao.selectSingle("select max(issue) from Rank where type=1",Integer.class);
		List<Rank> ranks= myDao.get(Rank.class,Arrays.asList("type","issue"),Arrays.asList(type,hot_max_issue)," order by degree desc",limit,offset);
		for(Rank rank: ranks){
			if(myDao.hasObject(rank.getKey(),Song.class)){
				Song song=myDao.get(Song.class,rank.getKey());
				result.add(new Pair<>(song,rank));
			}else{
				result.add(new Pair<>(new Song(URLDecoder.decode(rank.getKey(),"utf-8")),rank));
			}
		}
		return result;
	}

	public List<Rank> trend(String key, int limit, int type){
		return myDao.get(Rank.class,Arrays.asList("key_","type"),Arrays.asList(key,type)," order by issue desc",limit,0);
	}

	public boolean hasRank(String key, int type){
		return myDao.hasObject(Arrays.asList("key_","type"),Arrays.asList(key,type),Rank.class);
	}

	public int rankTotal(int type){
		int hot_max_issue=myDao.selectSingle("select max(issue) from Rank where type=1",Integer.class);
		return myDao.selectSingle("select count(*) from Rank where issue="+hot_max_issue+" and type="+type,Long.class).intValue();
	}


	public Album getAlbum(int id){
		return  myDao.load(Album.class,id);
	}

	public boolean hasSong(String key){return myDao.hasObject(key,Song.class);}
}
