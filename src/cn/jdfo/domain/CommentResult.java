package cn.jdfo.domain;

import cn.jdfo.tool.Three;

import java.util.List;

public class CommentResult {
	private List<SongComment> recComments;
	private List<SongComment> hotComments;
	private List<Integer> totals;

	public List<Three<String, String, Integer>> getIdTotals() {
		return idTotals;
	}

	public void setIdTotals(List<Three<String, String, Integer>> idTotals) {
		this.idTotals = idTotals;
	}

	public List<Three<String, String, Integer>> getIdOutcome() {
		return idOutcome;
	}

	public void setIdOutcome(List<Three<String, String, Integer>> idOutcome) {
		this.idOutcome = idOutcome;
	}

	private List<Three<String,String,Integer>> idTotals;//每个id对应的评论总数
	private List<Three<String,String,Integer>> idOutcome;//每个id已获取到的评论数
	private int count;
	private int hotCount;
	private int type;//位于常量类中

	public List<SongComment> getRecComments() {
		return recComments;
	}

	public void setRecComments(List<SongComment> recComments) {
		this.recComments = recComments;
	}

	public List<SongComment> getHotComments() {
		return hotComments;
	}

	public void setHotComments(List<SongComment> hotComments) {
		this.hotComments = hotComments;
	}

	public List<Integer> getTotals() {
		return totals;
	}

	public void setTotals(List<Integer> totals) {
		this.totals = totals;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getHotCount() {
		return hotCount;
	}

	public void setHotCount(int hotCount) {
		this.hotCount = hotCount;
	}

}
