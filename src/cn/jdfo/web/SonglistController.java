package cn.jdfo.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import cn.jdfo.dao.MyDao;
import cn.jdfo.service.SongService;
import cn.jdfo.tool.BeanGetter;
import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import cn.jdfo.domain.*;
import cn.jdfo.service.SonglistService;

@Controller
@RequestMapping(value = "/songlist")
public class SonglistController {
	private final SonglistService songlistService;
	private final SongService songService;

	@Autowired
	public SonglistController(SonglistService songlistService, SongService songService) {
		this.songlistService = songlistService;
		this.songService = songService;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> addSonglist(String name, HttpServletRequest request) {
		User user=(User)request.getSession().getAttribute("user");
		if(user==null)return ResponseEntity.status(200).body(new Message(200,"您还没有登录"));
		return ResponseEntity.status(HttpStatus.CREATED).body(songlistService.createSonglist(user.getId(), name));
	}
	
	@RequestMapping(method = RequestMethod.GET)
	@JsonView(View.SimpleSonglist.class)
	public ResponseEntity<Object> getOwnList(HttpServletRequest request){
		User user=(User)request.getSession().getAttribute("user");
		if(user==null)return ResponseEntity.status(200).body(new Message(200,"您还没有登录"));
		return ResponseEntity.ok(songlistService.getCreatedSongList(user.getId()));
	}

	@RequestMapping(value = "/{id}",method = RequestMethod.GET)
	@JsonView(View.DetailSonglist.class)
	public ResponseEntity<Object> getSonglist(@PathVariable Integer id){
		if(!songlistService.hasSonglist(id))return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(404,"不存在此歌单"));
		return ResponseEntity.status(HttpStatus.OK).body(songlistService.getSonglist(id));
	}


	//获取歌单的歌曲
	@RequestMapping(value="/{id}/song", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(View.DetailSonglist.class)
	public Set<Song> getSongs(@PathVariable int id){
		Songlist songlist = songlistService.getSonglist(id);
		return songlist.getSongs();
	}

	//收藏歌曲
	@RequestMapping(value = "/{id}/song/{key}", method = RequestMethod.POST)
    public ResponseEntity<Object> collectSong(@PathVariable Integer id, @PathVariable String key, HttpServletRequest request) throws UnsupportedEncodingException {
        if(!songlistService.hasSonglist(id))return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(404,"不存在此歌单"));
        if(!songService.hasSong(URLEncoder.encode(key,"utf-8")))return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(404,"数据库里面还没有这首歌"));
	    User user=(User) request.getSession().getAttribute("user");
	    if(user==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(401,"收藏歌曲之前，请先登录"));
	    int userId=user.getId();
	    if(!songlistService.collected(userId,id))return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(403,"您不是此歌单的主人，无权修改此歌单"));
	    if(songlistService.collectedSong(id,key))return ResponseEntity.status(HttpStatus.CONFLICT).body(new Message(409,"这张歌单已经收藏了这首歌曲"));
	    return ResponseEntity.status(HttpStatus.OK).body(songService.collectSong(id,key));
    }

	@RequestMapping(value="/{id}", method = RequestMethod.POST)
	public ResponseEntity<Object> updateOwnList(@PathVariable Integer id, String name, HttpServletRequest request) {
		User user=(User)request.getSession().getAttribute("user");
		if(user==null)return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Message(401,"您没有登录，无权修改此歌单"));
		if(!songlistService.collected(user.getId(),id))return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Message(403,"您不是此歌单的主人，无权修改此歌单"));
		if(!songlistService.hasSonglist(id))return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Message(404,"不存在此歌单"));
 		return ResponseEntity.status(HttpStatus.OK).body(songlistService.updateOwnSonglist(id,name));
	}

	@RequestMapping(value="/{id}" , method = RequestMethod.DELETE)
	@ResponseBody
	public boolean deleteSonglist(@PathVariable int id, HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		return user != null && songlistService.deleteSonglist(id, user.getId());
	}

	@RequestMapping(value = "/a", method = RequestMethod.GET)
	@ResponseBody
	public boolean test(){
		BeanGetter beanGetter=BeanGetter.getBeanGetter();
		MyDao myDao=beanGetter.getBean("myDao",MyDao.class);
		return myDao.hasObject(Collections.singletonList("id"),Collections.singletonList(111),Songlist.class);
	}
}
