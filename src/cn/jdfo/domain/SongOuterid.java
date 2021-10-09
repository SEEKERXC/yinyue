package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;

public class SongOuterid {
    private int id;
    private int songId;
    @JsonView({View.DetailSonglist.class,View.SimpleRank.class})
    private String outerid;
    @JsonView({View.DetailSonglist.class,View.SimpleRank.class})
    private String platform;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOuterid() {
        return outerid;
    }

    public void setOuterid(String outerid) {
        this.outerid = outerid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongOuterid outerid1 = (SongOuterid) o;
        return id == outerid1.id &&
                Objects.equals(outerid, outerid1.outerid) &&
                Objects.equals(platform, outerid1.platform);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, outerid, platform);
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }


}
