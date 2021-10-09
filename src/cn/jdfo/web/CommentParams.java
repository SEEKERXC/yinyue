package cn.jdfo.web;

public class CommentParams {
    private String key;
    private Integer page;
    private Integer type;
    private String songOuterids;
    private String albumOuterids;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getSongOuterids() {
        return songOuterids;
    }

    public void setSongOuterids(String songOuterids) {
        this.songOuterids = songOuterids;
    }

    public String getAlbumOuterids() {
        return albumOuterids;
    }

    public void setAlbumOuterids(String albumOuterids) {
        this.albumOuterids = albumOuterids;
    }
}
