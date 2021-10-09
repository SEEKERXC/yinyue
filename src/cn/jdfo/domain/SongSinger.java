package cn.jdfo.domain;

import java.util.Objects;

public class SongSinger {
    private int id;
    private int songId;
    private int singerId;

    public SongSinger() {
    }

    public SongSinger(int songId, int singerId) {

        this.songId = songId;
        this.singerId = singerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getSingerId() {
        return singerId;
    }

    public void setSingerId(int singerId) {
        this.singerId = singerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongSinger that = (SongSinger) o;
        return id == that.id &&
                songId == that.songId &&
                singerId == that.singerId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, songId, singerId);
    }
}
