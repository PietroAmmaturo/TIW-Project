package it.polimi.tiw.project.beans;

public class SongPlaylist {
    private int songId;
    private int playlistId;

    public SongPlaylist(int songId, int playlistId) {
        this.songId = songId;
        this.playlistId = playlistId;
    }

    public SongPlaylist() {
    }
    
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }
}