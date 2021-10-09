package cn.jdfo.domain;

import java.sql.Timestamp;
import java.util.Objects;

public class SongComment implements Comparable<SongComment>{
    private int id;
    private String author;
    private String content;
    private Timestamp time;
    private String img;
    private Integer like;
    private int songId;
    private String platform;
    private SongComment songCommentByReply;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
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
        SongComment that = (SongComment) o;
        return Objects.equals(author, that.author) &&
                Objects.equals(content, that.content) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {

        return Objects.hash(author, content, time);
    }

    public SongComment getSongCommentByReply() {
        return songCommentByReply;
    }

    public void setSongCommentByReply(SongComment songCommentByReply) {
        this.songCommentByReply = songCommentByReply;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public int compareTo(SongComment o) {
        return o.like-this.like;
    }
}
