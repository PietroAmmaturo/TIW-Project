package it.polimi.tiw.project.beans;

public class Song {
    private int id;
    private String title;
    private String audio;
    private int albumId;

    public Song(int id, String title, String audio, int albumId) {
        this.id = id;
        this.title = title;
        this.audio = audio;
        this.albumId = albumId;
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
}