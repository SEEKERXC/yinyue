package cn.jdfo.service;

import cn.jdfo.dao.*;
import cn.jdfo.domain.*;
import cn.jdfo.tool.BeanGetter;
import cn.jdfo.tool.Pair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 这是个特殊的service，用于在爬虫结束后将数据存储到本地，每次都新开一个线程来存储，所以不需要在spring启动时生成。
 */
public class StorageService implements Runnable {

	private MyDao myDao;

	private SearchResult searchResult;
	private CommentResult commentResult;

	StorageService(SearchResult searchResult) {
		this.searchResult = searchResult;
		initBeans();
	}

	StorageService(CommentResult commentResult) {
		this.commentResult = commentResult;
		initBeans();
	}

	private void initBeans(){
		BeanGetter beanGetter = BeanGetter.getBeanGetter();
		this.myDao=beanGetter.getBean("myDao",MyDao.class);
	}

	@Override
	public void run() {
		if(this.searchResult!=null){
			if(searchResult.getResult()==null||searchResult.getResult().size()<1)return;
			if(searchResult.getResult().get(0) instanceof Song){
				for(Object object: searchResult.getResult()){
					Song song=(Song)object;
					boolean hasSong;
					synchronized (this){//要在这里加个同步。如果并发访问一个不存在的key，然后都判断不存在，将要插入重复的数据，会报错。
						hasSong=myDao.hasObject(song.getKey(),Song.class);
					}
					if(hasSong){//有这首歌曲，补充新来源和新专辑（如果有的话）
						song.setId(myDao.getIdByKey(song.getKey(),Song.class));
						//补充新专辑来源
						for(Album album: song.getAlbums()){
							if(!myDao.hasObject(album.getKey(),Album.class)){
								album.setId(myDao.save(album));
								for(AlbumSource source: album.getSources()){
									if(!myDao.hasObject(Collections.singletonList("url"),Collections.singletonList(source.getUrl()),AlbumSource.class)){
										source.setAlbumId(album.getId());
										myDao.save(source);
									}
								}
								myDao.save(new SongAlbum(album.getId(),song.getId()));
							}else {
								album.setId(myDao.getIdByKey(album.getKey(),Album.class));
								for(AlbumSource albumSource: album.getSources()){
									if(!myDao.hasObject(Collections.singletonList("url"),Collections.singletonList(albumSource.getUrl()),AlbumSource.class)){
										albumSource.setAlbumId(album.getId());
										myDao.save(albumSource);
									}
								}
							}
						}
					}else{
						//要做一个长度的判断，不然真有那么一些特别奇葩的歌，key的长度大于1024了！
						//比如下面这个链接的歌曲：
						//http://www.kuwo.cn/yinyue/6128110?catalog=yueku2016
						if(song.getKey().length()>1024)continue;
						//将数据库还没有的这首歌的歌手添加进去
						for(Singer singer:song.getSingers()){
							if(!myDao.hasObject(singer.getKey(),Singer.class)){
								singer.setId(myDao.save(singer));
							}else singer.setId(myDao.getIdByKey(singer.getKey(),Singer.class));
						}
						//插入歌曲的专辑;插入歌曲-专辑表
						for(Album album: song.getAlbums()){
							if(album.getName().trim().equals("")||album.getName().length()>128)continue;
							if(!myDao.hasObject(album.getKey(),Album.class)){
								album.setId(myDao.save(album));
								for(AlbumSource albumSource: album.getSources()){
									albumSource.setAlbumId(album.getId());
									if(!myDao.hasObject(Collections.singletonList("url"),Collections.singletonList(albumSource.getUrl()),AlbumSource.class))myDao.save(albumSource);
								}
							}else album.setId(myDao.getIdByKey(album.getKey(),Album.class));
						}
						//插入歌曲以及相关信息
						//记一个报错：有时候，歌曲-专辑关系或者歌曲-歌手关系会出现重复，因此而报key重复的错误。待解决
						song.setId(myDao.save(song));
					}
					//插入歌曲的所有来源
					for(SongSource source:song.getSongSources()){
						source.setSongId(song.getId());
						if(!myDao.hasObject(Collections.singletonList("url"),Collections.singletonList(source.getUrl()),SongSource.class))myDao.save(source);
					}
					//插入歌曲的外部ID
					for(SongOuterid outerid: song.getSongOuterids()){
						outerid.setSongId(song.getId());
						if(!myDao.hasObject(Arrays.asList("outerid","platform"),Arrays.asList(outerid.getOuterid(),outerid.getPlatform()),SongOuterid.class))
							myDao.save(outerid);
					}
				}
			}else if(searchResult.getResult().get(0) instanceof Songlist){
				for(Object object: searchResult.getResult()){
					Songlist songList=(Songlist)object;
					if(!myDao.hasObject(Collections.singletonList("url"),Collections.singletonList(songList.getUrl()),Songlist.class)) myDao.save(songList);
				}
			}
		}
		else if(this.commentResult!=null){
			List<SongComment> allComments=new ArrayList<>();
			allComments.addAll(this.commentResult.getRecComments());
			allComments.addAll(this.commentResult.getHotComments());
			if(allComments.size()<1)return;
			if(allComments.get(0).getSongId()>0){
				int songId=allComments.get(0).getSongId();
				myDao.update(Collections.singletonList(new Pair<>("id",songId)),Collections.singletonList(new Pair<>("comment_sum",commentResult.getCount())),Song.class);
				for(SongComment comment: allComments){
					comment.setSongId(songId);
					//为了防止非UTF8编码，把所有评论作者和内容编码一次在存进数据库
					try {
						comment.setContent(URLEncoder.encode(comment.getContent(),"utf-8"));
						comment.setAuthor(URLEncoder.encode(comment.getAuthor(),"utf-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if(!myDao.hasObject(Arrays.asList("author","time_"),Arrays.asList(comment.getAuthor(),comment.getTime()),SongComment.class))myDao.save(comment);
				}
			}
		}
	}
}
