package cn.jdfo.domain;

import java.util.Objects;

public class AlbumOuterid {
    private int id;
    private int albumId;
    private String outerid;
    private String platform;

    public AlbumOuterid(String outerid, String platform) {
        this.outerid = outerid;
        this.platform = platform;
    }

    public AlbumOuterid() {
    }

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
        AlbumOuterid that = (AlbumOuterid) o;
        return id == that.id &&
                Objects.equals(outerid, that.outerid) &&
                Objects.equals(platform, that.platform);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, outerid, platform);
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
}
