package cn.jdfo.domain;

import java.util.Objects;

public class SonglistUser {
    private int id;
    private Integer songlistId;
    private Integer userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSonglistId() {
        return songlistId;
    }

    public void setSonglistId(Integer songlistId) {
        this.songlistId = songlistId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SonglistUser that = (SonglistUser) o;
        return id == that.id &&
                Objects.equals(songlistId, that.songlistId) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, songlistId, userId);
    }
}
