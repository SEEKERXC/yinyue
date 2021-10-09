package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

public class Singer implements Comparable<Singer>{
    private int id;
    @JsonView({View.AlbumDetail.class,View.SimpleRank.class, View.DetailSonglist.class})
    private String name;
    private String brief;
    private String key;
    private String img;
    private Collection<Song> songs;
    private Collection<String> outerIds;

    @Override
    public String toString() {
        return "Singer{" +
                "name='" + name + '\'' +
                '}';
    }

    public Singer() {

    }

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

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Singer singer = (Singer) o;
        return id == singer.id &&
                Objects.equals(name, singer.name) &&
                Objects.equals(brief, singer.brief) &&
                Objects.equals(key, singer.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public Collection<Song> getSongs() {
        return songs;
    }

    public void setSongs(Collection<Song> songs) {
        this.songs = songs;
    }

    public Collection<String> getOuterIds() {
        return outerIds;
    }

    public void setOuterIds(Collection<String> outerIds) {
        this.outerIds = outerIds;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    @Override
    public int compareTo(Singer o) {
        return this.hashCode()-o.hashCode();
    }

}
