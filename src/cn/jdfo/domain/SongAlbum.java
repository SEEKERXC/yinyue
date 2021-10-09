package cn.jdfo.domain;


import java.util.Objects;

public class SongAlbum {
    private int id;
    private Integer albumId;
    private Integer songId;

    public SongAlbum(Integer albumId, Integer songId) {
        this.albumId = albumId;
        this.songId = songId;
    }

    public SongAlbum() {

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
        SongAlbum songAlbum = (SongAlbum) o;
        return id == songAlbum.id &&
                Objects.equals(albumId, songAlbum.albumId) &&
                Objects.equals(songId, songAlbum.songId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, albumId, songId);
    }
}
