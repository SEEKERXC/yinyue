package cn.jdfo.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jdfo.tool.Constant;
import cn.jdfo.tool.Pair;
import cn.jdfo.tool.View;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.jdfo.domain.*;
import cn.jdfo.service.*;

@Controller
public class SongController {
	private final SongService songService;

	@Autowired
	public SongController(SongService songService) {
		this.songService = songService;
	}

	@RequestMapping(value="/search", method = RequestMethod.GET)
	public ResponseEntity<Object> 卧槽(String keyword, Integer page, Integer type) {
		if(keyword==null)return ResponseEntity.status(400).body(new Message(400,"缺少关键词参数keyword"));
		page=page==null?1:page;
		type=type==null?Constant.SEARCH_SONG:type;
		try{
		    SearchResult result = songService.search(keyword,page,type,null);
            return ResponseEntity.status(HttpStatus.OK).body(result);
		}catch (Exception e){
            System.out.println("搜索出差错了");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message(500,"搜索服务出错了"));
        }
	}

	@RequestMapping(value="/comment", method = RequestMethod.GET)
	public ResponseEntity<Object> 卧槽(CommentParams params) {
		if(params.getKey()==null||params.getKey().isEmpty()||params.getSongOuterids()==null||params.getSongOuterids().isEmpty())return ResponseEntity.status(400).body(new Message(400,"缺少关键参数"));
        List<SongOuterid> songOuterids= JSON.parseArray(params.getSongOuterids(),SongOuterid.class);
		List<Pair<String,String>> idPair=new ArrayList<>();
		for(SongOuterid outerid: songOuterids){
			idPair.add(new Pair<>(outerid.getPlatform(),outerid.getOuterid()));
		}
		try {
		    return ResponseEntity.status(200).body(songService.fetchComment(params.getKey(),idPair,params.getPage(),params.getType()));
        }catch (Exception e){
            System.out.println("评论出差错了");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Message(500,"获取评论服务出错了"));
        }
	}

	@RequestMapping(value = "/simpleRank", method = RequestMethod.GET)
	@JsonView(View.SimpleRank.class)
	public ResponseEntity<Object> 卧槽(Integer type, Integer limit,Integer offset) throws UnsupportedEncodingException {
	    type=type==null?1:type;
	    limit=limit==null?20:limit;
	    offset=offset==null?0:offset;
        return ResponseEntity.status(200).body(songService.getRank(type,limit,offset));
	}

	@RequestMapping(value = "/songRank/{key}", method = RequestMethod.GET)
	public ResponseEntity<Object> 卧槽啊(@PathVariable String key, Integer limit, Integer type) throws UnsupportedEncodingException {
		limit=limit==null?10:limit;
		type=type==null?1:type;
		key= URLEncoder.encode(key,"utf-8");
		if(!songService.hasSong(key))return ResponseEntity.status(404).body(new Message(404,"没有这首歌曲"));
		return ResponseEntity.ok(songService.trend(key,limit,type));
	}

	@RequestMapping(value = "/hasRank/{key}", method = RequestMethod.GET)
	@ResponseBody
	public boolean 卧槽啊啊(@PathVariable String key, Integer type) throws UnsupportedEncodingException {
		type = type == null ? 1 : type;
		key=URLEncoder.encode(key,"utf-8");
		return songService.hasSong(key) && songService.hasRank(key, type);
	}

	@RequestMapping(value = "/album/{id}", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(View.AlbumDetail.class)
	public Album 卧槽(@PathVariable int id){
		return songService.getAlbum(id);
	}
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String 卧槽(){
		return "index";
	}

	@RequestMapping(value = "/rankTotal", method = RequestMethod.GET)
	@ResponseBody
	public int 卧槽(Integer type){
		type=type==null?1:type;
		return songService.rankTotal(type);
	}
}
