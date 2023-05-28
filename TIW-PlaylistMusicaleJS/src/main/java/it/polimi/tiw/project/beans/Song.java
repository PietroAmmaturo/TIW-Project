package it.polimi.tiw.project.beans;

import java.io.Serializable;

public class Song implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
    private String title;
    private String audio;
    private String genre;
    private int albumId;
    private String albumTitle;

    public Song(int id, String title, String audio, int albumId, String genre) {
        this.id = id;
        this.title = title;
        this.audio = audio;
        this.albumId = albumId;
        this.genre = genre;
    }

    public Song() {
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getGenre() {
    	return genre;
    }
    
    public void setGenre(String genre) {
    	this.genre = genre;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
    
    public String getAlbumTitle() {
    	return albumTitle;
    }
    
    public void setAlbumTitle(String albumTitle) {
    	this.albumTitle = albumTitle;
    }
    
}