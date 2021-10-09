package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Songlist implements Comparable<Songlist>{
    @JsonView(View.SimpleSonglist.class)
    private int id;
    @JsonView(View.SimpleSonglist.class)
    private String img;
    @JsonView(View.SimpleSonglist.class)
    private String name;
    @JsonView(View.SimpleSonglist.class)
    private Timestamp updateTime;
    @JsonView(View.SimpleSonglist.class)
    private Integer songCount;
    @JsonView(View.SimpleSonglist.class)
    private String brief;
    @JsonView(View.SimpleSonglist.class)
    private String url;
    @JsonView(View.SimpleSonglist.class)
    private Integer playCount;
    @JsonView(View.SimpleSonglist.class)
    private String authorName;
    @JsonView(View.SimpleSonglist.class)
    private Integer userId;
    @JsonView(View.SimpleSonglist.class)
    private Integer collectCount;
    @JsonView(View.SimpleSonglist.class)
    private Timestamp collectTime;
    @JsonView(View.SimpleSonglist.class)
    private String platform;
    @JsonIgnore
    private User user;
    @JsonView(View.DetailSonglist.class)
    private Set<Song> songs;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Integer playCount) {
        this.playCount = playCount;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCollectCount() {
        return collectCount;
    }

    public void setCollectCount(Integer collectCount) {
        this.collectCount = collectCount;
    }

    public Timestamp getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(Timestamp collectTime) {
        this.collectTime = collectTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Songlist songlist = (Songlist) o;
        return id == songlist.id &&
                Objects.equals(img, songlist.img) &&
                Objects.equals(name, songlist.name) &&
                Objects.equals(updateTime, songlist.updateTime) &&
                Objects.equals(songCount, songlist.songCount) &&
                Objects.equals(brief, songlist.brief) &&
                Objects.equals(url, songlist.url) &&
                Objects.equals(playCount, songlist.playCount) &&
                Objects.equals(authorName, songlist.authorName) &&
                Objects.equals(userId, songlist.userId) &&
                Objects.equals(collectCount, songlist.collectCount) &&
                Objects.equals(collectTime, songlist.collectTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, img, name, updateTime, songCount, brief, url, playCount, authorName, userId, collectCount, collectTime);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userByUserId) {
        this.user = userByUserId;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public int compareTo(Songlist o) {
        return o.collectCount-this.collectCount;
    }
}
