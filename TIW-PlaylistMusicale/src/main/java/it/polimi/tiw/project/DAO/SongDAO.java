package it.polimi.tiw.project.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.project.beans.Song;

public class SongDAO {
	
	private Connection connection;

	public SongDAO() {
		// TODO Auto-generated constructor stub
	}

	public SongDAO(Connection con) {
		this.connection = con;
	}
	
	public Song findSongById(int songId) throws SQLException {
	    String query = "SELECT * FROM Song WHERE id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, songId);
	        try (ResultSet result = statement.executeQuery()) {
	            if (result.next()) {
	                Song song = new Song();
	                song.setId(result.getInt("id"));
	                song.setTitle(result.getString("title"));
	                song.setAudio(result.getString("audio"));
	                song.setAlbumId(result.getInt("album_id"));
	                return song;
	            } else {
	                return null;
	            }
	        }
	    }
	}
	
	public List<Song> findAllSongsByPlaylistId(int playlistId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    String sql = "SELECT s.* FROM Song s " +
	                 "INNER JOIN SongPlaylist sp ON s.id = sp.song_id " +
	                 "WHERE sp.playlist_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(sql)) {
	        statement.setInt(1, playlistId);
	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Song song = new Song();
	                song.setId(resultSet.getInt("id"));
	                song.setTitle(resultSet.getString("title"));
	                song.setAudio(resultSet.getString("audio"));
	                song.setAlbumId(resultSet.getInt("album_id"));
	                songs.add(song);
	            }
	        }
	    }
	    return songs;
	}

	public List<Song> findAllSongsByUserId(int userId) throws SQLException {
	    List<Song> songs = new ArrayList<>();
	    String query = "SELECT * FROM Song INNER JOIN Album ON Song.album_id = Album.id WHERE Album.user_id = ?";
	    try (PreparedStatement statement = connection.prepareStatement(query)) {
	        statement.setInt(1, userId);
	        try (ResultSet result = statement.executeQuery()) {
	            while (result.next()) {
	                Song song = new Song();
	                song.setId(result.getInt("id"));
	                song.setTitle(result.getString("title"));
	                song.setAudio(result.getString("audio"));
	                songs.add(song);
	            }
	        }
	    }
	    return songs;
	}
	
	public boolean doesSongBelongToUser(int songId, int userId) throws SQLException {
	    try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Song INNER JOIN Album ON Song.album_id = Album.id WHERE Song.id=? AND Album.user_id=?")) {
	        statement.setInt(1, songId);
	        statement.setInt(2, userId);
	        try (ResultSet result = statement.executeQuery()) {
	            result.next();
	            int count = result.getInt(1);
	            return count > 0;
	        }
	    }
	}
	
}
