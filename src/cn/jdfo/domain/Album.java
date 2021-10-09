package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.*;

public class Album {
    @JsonView(View.AlbumDetail.class)
    private int id;
    @JsonView({View.AlbumDetail.class,View.SimpleRank.class, View.DetailSonglist.class})
    private String name;
    private Integer singerId;
    @JsonView({View.AlbumDetail.class, View.DetailSonglist.class})
    private Integer songCount;
    @JsonView({View.AlbumDetail.class, View.DetailSonglist.class})
    private Integer commentCount;
    @JsonView({View.AlbumDetail.class, View.DetailSonglist.class})
    private String key;
    @JsonView({View.AlbumDetail.class, View.DetailSonglist.class})
    private String time;
    @JsonView({View.AlbumDetail.class, View.DetailSonglist.class})
    private Singer singer;
    @JsonView(View.AlbumDetail.class)
    private String img;
    @JsonView(View.AlbumDetail.class)
    private float score;
    @JsonView(View.AlbumDetail.class)
    private Set<AlbumOuterid> outerids;
    @JsonView(View.AlbumDetail.class)
    private Set<Song> songs;

    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                '}';
    }

    @JsonView({View.SimpleRank.class,View.AlbumDetail.class})
    private Set<AlbumSource> sources;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSingerId() {
        return singerId;
    }

    public void setSingerId(Integer singerId) {
        this.singerId = singerId;
    }

    public Integer getSongCount() {
        return songCount;
    }

    public void setSongCount(Integer songCount) {
        this.songCount = songCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return id == album.id &&
                Objects.equals(name, album.name) &&
                Objects.equals(singerId, album.singerId) &&
                Objects.equals(songCount, album.songCount) &&
                Objects.equals(commentCount, album.commentCount) &&
                Objects.equals(key, album.key) &&
                Objects.equals(time, album.time);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, singerId, songCount, commentCount, key, time);
    }

    public Set<AlbumOuterid> getOuterids() {
        return outerids;
    }

    public void setOuterids(Set<AlbumOuterid> outerids) {
        this.outerids = outerids;
    }

    public Set<AlbumSource> getSources() {
        return sources;
    }

    public void setSources(Set<AlbumSource> sources) {
        this.sources = sources;
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void setSongs(Set<Song> songs) {
        this.songs = songs;
    }
}
