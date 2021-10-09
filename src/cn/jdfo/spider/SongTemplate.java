package cn.jdfo.spider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

import cn.jdfo.domain.*;
import cn.jdfo.tool.Pair;
import org.springframework.stereotype.Component;

@Component
public class SongTemplate {

	public void addSong(List<Object> resultList, List<String> ids, List<String> names,
			List<String> urls, List<List<String>> singerlist, List<String> albumUrls, 
			List<String> albumNames, String platform) throws UnsupportedEncodingException {
		System.out.println(platform+" starts add song");
		for(int i=0;i<names.size();i++){
			if(names.get(i).equals("该艺人演唱的其他版本"))continue;
			Song song=new Song();
			Set<SongOuterid> songOuterids=new HashSet<>();
			SongOuterid songOuterid=new SongOuterid();
			songOuterid.setPlatform(platform);
			songOuterid.setOuterid(ids.get(i));
			songOuterids.add(songOuterid);
			song.setSongOuterids(songOuterids);
			List<Singer> singers=new ArrayList<>();
			for(String s:singerlist.get(i)){
				Singer singer=new Singer();
				String key= URLEncoder.encode(s,"utf-8");//生成歌手KEY
				singer.setName(s);
				singer.setKey(key);
				if(s.length()>0){
					singers.add(singer);
				}else{
					Singer singer1=new Singer();
					singer1.setName("未知歌手");
					singers.add(singer);
				}
			}
			Set<SongSource> sources=new HashSet<>();
			SongSource source=new SongSource();
			source.setPlatform(platform);
			source.setUrl(urls.get(i));
			sources.add(source);
			String name=names.get(i);
			if(name.contains("cover")){
				int index=name.indexOf("cover");
				name=name.substring(0, index-1);
			}else if(name.contains("Cover")){
				int index=name.indexOf("Cover");
				name=name.substring(0, index-1);
			}
			name=name.replaceAll("&nbsp;","");
			String unikey;
			StringBuilder singername= new StringBuilder();//这两个加起来用来生成歌曲ID
			Singer[] singerArray=new Singer[singers.size()];
			for(int j=0;j<singers.size();j++){
				singerArray[j]=singers.get(j);
			}
			Arrays.sort(singerArray);//在生成歌曲ID的时候，歌手应该按照名字排好序
			for(Singer singer:singerArray){
				singername.append(singer.getName());
			}
			unikey=name+singername;
			unikey=URLEncoder.encode(unikey,"utf-8");//生成歌曲key
			Album album=new Album();
			if(albumNames.get(i).trim().equals(""))albumNames.set(i,"未知专辑");
			album.setName(albumNames.get(i));
			Set<AlbumSource> albumSources=new HashSet<>();
			albumSources.add(new AlbumSource(albumUrls.get(i)));
			album.setSources(albumSources);
			album.setSinger(singers.get(0));
			album.setKey(URLEncoder.encode(singers.get(0).getName(),"utf-8")+URLEncoder.encode(albumNames.get(i),"utf-8"));
			Set<Album> albumSet=new HashSet<>();
			albumSet.add(album);
			song.setName(name);
			song.setSongSources(sources);
			song.setAlbums(albumSet);
			song.setSingers(new HashSet<>(Arrays.asList(singerArray)));//排好序之后的歌手列表
			song.setKey(unikey);
			resultList.add(song);
		}
		System.out.println(platform+" adding song finished");
	}
	
	public void addSonglist(List<Object> resultList, List<String> urls, List<String> names,
			List<String> authors, List<String> imgUrls, List<Integer> collectCount,String platform){
		for(int i=0;i<urls.size();i++){
			Songlist songlist=new Songlist();
			songlist.setUrl(urls.get(i));
			songlist.setName(names.get(i));
			songlist.setAuthorName(authors.get(i));
			songlist.setImg(imgUrls.get(i));
			songlist.setCollectCount(collectCount.get(i));
			songlist.setPlatform(platform);
			resultList.add(songlist);
		}
		System.out.println(platform+"adding songlist finished");
	}
	
	public void addSinger(List<Object> resultList, List<String> singerIds, List<String> singerNames, List<String> imgUrls){
		for(int i=0;i<singerIds.size();i++){
			Singer singer=new Singer();
			List<String> outerIds=new ArrayList<>();
			outerIds.add(singerIds.get(i));
			singer.setOuterIds(outerIds);//注：虾米的ID直接是歌手的详情页面
			singer.setName(singerNames.get(i));
			String key= null;
			try {
				key = URLEncoder.encode(singerNames.get(i),"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			singer.setKey(key);
			singer.setImg(imgUrls.size()>0?imgUrls.get(i):"");
			resultList.add(singer);
		}
	}

	public void addAlbum(List<Object> resultList, List<String> albumIds, List<String> urls, List<String> albumNames,
						 List<String> imgUrls, List<String> publishTimes,
						 List<String> singerNames, List<String> songCount, List<String> scores, String platform){
		//其中有可能空的：songCount,scores，其他都不空
		//有sorces的：酷我和虾米；有songCount的：酷狗、网易、QQ。
		for (int i=0;i<albumIds.size();i++){
		    Album album=new Album();
		    Set<AlbumOuterid> outerIds=new HashSet<>();
		    outerIds.add(new AlbumOuterid(albumIds.get(i),platform));
		    album.setOuterids(outerIds);
		    Set<AlbumSource> sources=new HashSet<>();
		    sources.add(new AlbumSource(urls.get(i)));
		    album.setSources(sources);
		    album.setName(albumNames.get(i));
		    album.setImg(imgUrls.get(i));
		    album.setTime(publishTimes.get(i));
		    Singer singer=new Singer();
		    singer.setName(singerNames.get(i));
		    album.setSinger(singer);
			try {
				album.setKey(URLEncoder.encode(album.getSinger().getName(),"utf-8")+URLEncoder.encode(album.getName(),"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(songCount.size()>0)album.setSongCount(Integer.parseInt(songCount.get(i)));
		    if(scores.size()>0)album.setScore(Float.parseFloat(scores.get(i)));
		    resultList.add(album);
        }
	}

	public void addComments(List<SongComment> comments, List<String> author, List<String> content, List<Long> time,
	                        List<String> authorImg, List<Integer> liked, String platform, SongComment[] beReplied){
		for(int i=0,len=content.size();i<len;i++){
			SongComment comment=new SongComment();
			comment.setAuthor(author.get(i));
			comment.setContent(content.get(i));
			comment.setTime(new Timestamp(time.get(i)));
			comment.setImg(authorImg.get(i));
			comment.setLike(liked.get(i));
			comment.setPlatform(platform);
			if(beReplied!=null&&beReplied[i]!=null)comment.setSongCommentByReply(beReplied[i]);
			comments.add(comment);
		}
	}
}
