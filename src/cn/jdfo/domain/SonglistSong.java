package cn.jdfo.domain;

import net.minidev.json.annotate.JsonIgnore;

import java.util.Objects;

public class SonglistSong {
    private int id;
    private int songlistId;
    private int songId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSonglistId() {
        return songlistId;
    }

    public void setSonglistId(int songlistId) {
        this.songlistId = songlistId;
    }

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SonglistSong that = (SonglistSong) o;
        return id == that.id &&
                songlistId == that.songlistId &&
                songId == that.songId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, songlistId, songId);
    }
}
