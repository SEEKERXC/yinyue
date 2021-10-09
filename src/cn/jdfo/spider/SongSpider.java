package cn.jdfo.spider;

import java.util.List;
import java.util.Map;

import cn.jdfo.domain.*;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
* @ClassName: MusicService 
* @Description: 描述了所有关于音乐爬虫的功能，继承了爬虫接口
* @author 肖飞  13319202082@163.com
 */
public interface SongSpider extends PageProcessor,Runnable{
	
	/**
	 * 
	 * @Title: searchSong
	 * @Description: 搜索歌曲，将搜索到的歌曲添加到列表
	 * @param songs
	 * @param keyword
	 * @param page
	 */
	void search(String keyword, int page, int type, SearchResult result) throws InterruptedException;
	void fetchComments(String outerId, int page, int type, CommentResult result) throws InterruptedException;
	boolean searchFinished();
	boolean gettingCommentsFinished();
	String getPlatformName();
}
