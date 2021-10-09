package cn.jdfo.domain;

import java.util.List;

public class SearchResult {
	private List result;
	private int total;
	private int page;
	private List<Integer> totals;
	private int type;//位于常量类中

	public List getResult() {
		return result;
	}

	public void setResult(List result) {
		this.result = result;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public List<Integer> getTotals() {
		return totals;
	}

	public void setTotals(List<Integer> totals) {
		this.totals = totals;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
