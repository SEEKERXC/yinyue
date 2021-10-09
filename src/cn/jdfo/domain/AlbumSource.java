package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

public class AlbumSource {
    private int id;
    private Integer albumId;
    @JsonView({View.SimpleRank.class,View.AlbumDetail.class})
    private String url;
    @JsonView(View.AlbumDetail.class)
    private String img;

    public AlbumSource() {
    }

    public AlbumSource(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlbumSource that = (AlbumSource) o;
        return id == that.id &&
                Objects.equals(albumId, that.albumId) &&
                Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, albumId, url);
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
