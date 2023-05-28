package it.polimi.tiw.project.beans;

import java.io.Serializable;

public class SongPlaylist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int songId;
    private int playlistId;
    private int precedence;

    public SongPlaylist(int songId, int playlistId, int precedence) {
        this.songId = songId;
        this.playlistId = playlistId;
        this.precedence = precedence;
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

	public int getPrecedence() {
		return precedence;
	}

	public void setPrecedence(int precedence) {
		this.precedence = precedence;
	}
    
}