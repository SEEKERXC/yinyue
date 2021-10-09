package cn.jdfo.tool;

public class View {
     public interface AlbumDetail{}
     public interface SimpleRank{}
     public interface DetailRank extends SimpleRank{}
     public interface SimpleSonglist extends Message{}
     public interface DetailSonglist extends SimpleSonglist,Message{}
     public interface SimpleUser extends Message{}
     public interface Message{}
}
