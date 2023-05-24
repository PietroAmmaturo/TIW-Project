package it.polimi.tiw.project.beans;

import java.io.Serializable;

public class SongPlaylist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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