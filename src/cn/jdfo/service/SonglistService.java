package cn.jdfo.service;

import java.sql.Timestamp;
import java.util.*;

import cn.jdfo.tool.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.jdfo.dao.*;
import cn.jdfo.domain.*;

@Service
public class SonglistService {

	private final MyDao myDao;

    @Autowired
    public SonglistService(MyDao myDao) {
        this.myDao = myDao;
    }

    public Songlist createSonglist(int userId, String name){
		Songlist songlist=new Songlist();
		songlist.setUserId(userId);
		songlist.setName(name);
		songlist.setUpdateTime(new Timestamp(new Date().getTime()));
		songlist.setImg("http://p4.music.126.net/7XAb8vrYspEo5MqyC94h-Q==/1393081234067977.jpg?param=200y200");
		songlist.setSongCount(0);
		songlist.setBrief("暂时还没有简介。");
		songlist.setId(myDao.save(songlist));
		return songlist;
	}
	
	public List<Songlist> getCreatedSongList(int userId){
        return myDao.oneToMany(userId,User.class,Songlist.class);
	}
	
	public boolean updateOwnSonglist(int id, String name){
		myDao.update(Collections.singletonList(new Pair<>("id",id)),Collections.singletonList(new Pair<>("name",name)),Songlist.class);
		return true;
	}

	public boolean deleteSonglist(int id, int userId){
        List<SonglistUser> songlistUsers=myDao.get(SonglistUser.class,Arrays.asList("songlist_id","user_id"),Arrays.asList(id,userId));
        if(songlistUsers==null||songlistUsers.isEmpty())return false;
		myDao.delete(Collections.singletonList("songlist_id"),Collections.singletonList(id),SonglistUser.class);
		myDao.delete(Collections.singletonList("songlist_id"),Collections.singletonList(id),SonglistSong.class);
		myDao.delete(id,Songlist.class);
		return true;
	}

	public Songlist getSonglist(int id){
    	return myDao.load(Songlist.class,id);
	}
	public boolean hasSonglist(int id){
    	return myDao.hasObject(id,Songlist.class);
	}
	public boolean collected(int userId, int songlistId){
    	return myDao.hasObject(Arrays.asList("user_id","id"),Arrays.asList(userId,songlistId),Songlist.class);
	}

	public boolean collectedSong(int songlistId, String key){
    	if(!myDao.hasObject(key,Song.class))return false;
    	int songId=myDao.getIdByKey(key,Song.class);
		return myDao.hasObject(Arrays.asList("songlist_id", "song_id"), Arrays.asList(songlistId, songId), SonglistSong.class);
	}

}
