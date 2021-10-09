package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Objects;

public class SongSource {
    private int id;
    @JsonView({View.DetailSonglist.class})
    private String platform;
    @JsonView({View.DetailSonglist.class})
    private String url;
    @JsonView({View.DetailSonglist.class})
    private String songUrl;
    private Integer songId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    public Integer getSongId() {
        return songId;
    }

    public void setSongId(Integer songId) {
        this.songId = songId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongSource that = (SongSource) o;
        return id == that.id &&
                Objects.equals(platform, that.platform) &&
                Objects.equals(url, that.url) &&
                Objects.equals(songUrl, that.songUrl) &&
                Objects.equals(songId, that.songId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, platform, url, songUrl, songId);
    }

}
