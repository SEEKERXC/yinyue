package cn.jdfo.domain;

import cn.jdfo.tool.View;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Song {
    @JsonView(View.AlbumDetail.class)
    private int id;
    @JsonView({View.AlbumDetail.class,View.SimpleRank.class,View.DetailSonglist.class})
    private String name;
    private String lyric;
    private String zuoci;
    private String zuoqu;
    private Integer duration;
    private Integer commentSum;
    @JsonView({View.AlbumDetail.class,View.SimpleRank.class,View.DetailSonglist.class})
    private String key;
    private Set<SongComment> songComments;
    @JsonView({View.DetailSonglist.class,View.SimpleRank.class})
    private Set<SongOuterid> songOuterids;
    @JsonView({View.DetailSonglist.class})
    private Set<SongSource> songSources;
    @JsonView({View.SimpleRank.class,View.DetailSonglist.class})
    private Set<Album> albums;
    @JsonView({View.AlbumDetail.class,View.SimpleRank.class,View.DetailSonglist.class})
    private Set<Singer> singers;
    private Set<Songlist> songlists;

    public Song() {
    }

    public Song(String name) {

        this.name = name;
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

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getZuoci() {
        return zuoci;
    }

    public void setZuoci(String zuoci) {
        this.zuoci = zuoci;
    }

    public String getZuoqu() {
        return zuoqu;
    }

    public void setZuoqu(String zuoqu) {
        this.zuoqu = zuoqu;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getCommentSum() {
        return commentSum;
    }

    public void setCommentSum(Integer commentSum) {
        this.commentSum = commentSum;
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
        Song song = (Song) o;
        return id == song.id &&
                Objects.equals(name, song.name) &&
                Objects.equals(lyric, song.lyric) &&
                Objects.equals(zuoci, song.zuoci) &&
                Objects.equals(zuoqu, song.zuoqu) &&
                Objects.equals(duration, song.duration) &&
                Objects.equals(commentSum, song.commentSum) &&
                Objects.equals(key, song.key);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, lyric, zuoci, zuoqu, duration, commentSum, key);
    }


    public Set<Album> getAlbums() {
        return albums;
    }

    @Override
    public String toString() {
        return "Song{" +
                "name='" + name + '\'' +
                '}';
    }

    public void setAlbums(Set<Album> albums) {
        this.albums = albums;
    }

    public Set<Singer> getSingers() {
        return singers;
    }

    public void setSingers(Set<Singer> singers) {
        this.singers = singers;
    }

    public Set<Songlist> getSonglists() {
        return songlists;
    }

    public void setSonglists(Set<Songlist> songlists) {
        this.songlists = songlists;
    }

    public Set<SongComment> getSongComments() {
        return songComments;
    }

    public void setSongComments(Set<SongComment> songComments) {
        this.songComments = songComments;
    }

    public Set<SongOuterid> getSongOuterids() {
        return songOuterids;
    }

    public void setSongOuterids(Set<SongOuterid> songOuterids) {
        this.songOuterids = songOuterids;
    }

    public Set<SongSource> getSongSources() {
        return songSources;
    }

    public void setSongSources(Set<SongSource> songSources) {
        this.songSources = songSources;
    }

}
