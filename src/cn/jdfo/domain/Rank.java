package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Timestamp;
import java.util.Objects;

public class Rank {
    private int id;
    @JsonView(View.DetailRank.class)
    private String key;
    @JsonView(View.SimpleRank.class)
    private Integer degree;//分值
    private Integer rnum;//排名
    private Integer issue;//期数

    public Rank() {
    }

    public Rank(String key, Integer degree, Integer type, Timestamp updateTime, Integer rnum, Integer issue) {

        this.key = key;
        this.degree = degree;
        this.type = type;
        this.updateTime = updateTime;
        this.rnum=rnum;
        this.issue=issue;
    }

    @JsonView(View.DetailRank.class)
    private Integer type;
    @JsonView(View.SimpleRank.class)
    private Timestamp updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getDegree() {
        return degree;
    }

    public void setDegree(Integer degree) {
        this.degree = degree;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rank rank = (Rank) o;
        return id == rank.id &&
                Objects.equals(key, rank.key) &&
                Objects.equals(degree, rank.degree) &&
                Objects.equals(type, rank.type) &&
                Objects.equals(updateTime, rank.updateTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, key, degree, type, updateTime);
    }

    public Integer getRnum() {
        return rnum;
    }

    public void setRnum(Integer rnum) {
        this.rnum = rnum;
    }

    public Integer getIssue() {
        return issue;
    }

    public void setIssue(Integer issue) {
        this.issue = issue;
    }
}
